package net.starly.cashshop.database;

import net.starly.cashshop.executor.AsyncExecutors;
import net.starly.cashshop.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class SQLProcessor {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<GenericFutureListener<SQLResult>> listeners;
    private Set<Pair<String, Consumer<PreparedStatementWrapper>>> stmts;
    private Connection connection;
    private ExecutorService service;
    private Future<?> future;
    private boolean closeAfterProcess = true;

    public SQLProcessor addListener(GenericFutureListener<SQLResult> listener) {
        lock.writeLock().lock();
        if (listeners == null)
            listeners = new HashSet<>();

        listeners.add(listener);
        lock.writeLock().unlock();
        return this;
    }

    public SQLProcessor connection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public SQLProcessor prepare(String query, Consumer<PreparedStatementWrapper> wrapperConsumer) {
        lock.writeLock().lock();
        if (stmts == null)
            stmts = new HashSet<>();

        stmts.add(new Pair<>(query, wrapperConsumer));
        lock.writeLock().unlock();
        return this;
    }

    public SQLProcessor closeAfterProcess(boolean bool) {
        closeAfterProcess = bool;
        return this;
    }

    public SQLProcessor executor(ExecutorService service) {
        this.service = service;
        return this;
    }

    public SQLProcessor sync() {
        try {
            future.get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    private void onFinished(Exception ex) {
        listeners.forEach((l) -> {
            try {
                l.operationComplete(new SQLResult() {
                    @Override
                    public boolean isSuccess() {
                        return ex == null;
                    }

                    @Override
                    public Exception getCause() {
                        return ex;
                    }

                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return true;
                    }

                    @Override
                    public Void get() throws InterruptedException, ExecutionException {
                        return null;
                    }

                    @Override
                    public Void get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        return null;
                    }
                });

            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        });
    }

    @SuppressWarnings("all")
    private synchronized void process() {
        if (service == null)
            service = AsyncExecutors.getService();

        future = service.submit(new ExceptionHandle(() -> {
            try {
                lock.readLock().lock();
                if (connection == null)
                    connection = ConnectionPoolManager.getInternalPool().getConnection();

                Iterator iter = stmts.iterator();

                while (iter.hasNext()) {
                    Pair<String, Consumer<PreparedStatementWrapper>> stmt = (Pair<String, Consumer<PreparedStatementWrapper>>) iter.next();
                    PreparedStatement statement = connection.prepareStatement(stmt.getFirst());
                    stmt.getSecond().accept(new PreparedStatementWrapper(statement));
                    statement.close();
                }

                onFinished(null);
                if (closeAfterProcess)
                    connection.close();

                lock.readLock().unlock();
            } catch (Exception e) {
                onFinished(e);
            }
        }));

    }

    public SQLProcessor start() {
        if (future != null)
            throw new UnsupportedOperationException("이미 실행중인 SQL Processor 입니다.");
        else {
            process();
            return this;
        }
    }

}

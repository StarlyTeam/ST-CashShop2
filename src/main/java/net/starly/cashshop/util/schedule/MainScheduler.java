package net.starly.cashshop.util.schedule;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainScheduler implements STScheduler {
    private final AtomicLong taskId = new AtomicLong(0L);
    private final LinkedList<SchedulerRunnable> runnableList = new LinkedList<>();
    private final Queue<SchedulerRunnable> nextTasks = new LinkedList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastTick = 0L;

    public SchedulerRunnable runTask(Runnable r) {
        SchedulerRunnable runnable = new SchedulerRunnable(this.lastTick, 0L, 0L, r, this.taskId.getAndIncrement());
        this.lock.writeLock().lock();
        this.nextTasks.add(runnable);
        this.lock.writeLock().unlock();
        return runnable;
    }

    public SchedulerRunnable runTaskLater(Runnable r, long delay) {
        SchedulerRunnable runnable = new SchedulerRunnable(this.lastTick, 0L, delay, r, this.taskId.getAndIncrement());
        this.lock.writeLock().lock();
        this.nextTasks.add(runnable);
        this.lock.writeLock().unlock();
        return runnable;
    }

    public SchedulerRunnable runTaskTimer(Runnable r, long startupDelay, long period) {
        SchedulerRunnable runnable = new SchedulerRunnable(this.lastTick, period, startupDelay, r, this.taskId.getAndIncrement());
        this.lock.writeLock().lock();
        this.nextTasks.add(runnable);
        this.lock.writeLock().unlock();
        return runnable;
    }

    public SchedulerRunnable getTask(int taskId) {
        LinkedList<SchedulerRunnable> list = (LinkedList<SchedulerRunnable>)this.runnableList.clone();
        Iterator var3 = list.iterator();

        SchedulerRunnable r;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            r = (SchedulerRunnable)var3.next();
        } while(r.getTaskId() != (long)taskId);

        return r;
    }

    public long heartBeatProcess(long currentTick) {
        this.lastTick = currentTick;

        while(!this.nextTasks.isEmpty()) {
            SchedulerRunnable runnable = (SchedulerRunnable)this.nextTasks.poll();
            this.runnableList.addLast(runnable);
        }

        ListIterator<SchedulerRunnable> it = this.runnableList.listIterator();

        while(it.hasNext()) {
            SchedulerRunnable run = it.next();
            if (run.processTickAndCheck(currentTick)) {
                run.run();
            }

            if (run.haveToDestroy()) {
                it.remove();
            }
        }

        return 0L;
    }
}
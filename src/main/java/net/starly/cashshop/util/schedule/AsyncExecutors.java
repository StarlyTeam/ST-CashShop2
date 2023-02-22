package net.starly.cashshop.util.schedule;

import net.starly.cashshop.database.ExceptionHandle;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncExecutors {

    private static ExecutorService service = null;
    private static MainScheduler mainScheduler = new MainScheduler();
    private static AsyncScheduler aSyncScheduler = new AsyncScheduler();
    public static ExecutorService getService() {
        if(service == null)
            service = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    (
                            new BasicThreadFactory.Builder())
                            .namingPattern("STAsyncThread-%d")
                            .daemon(true)
                            .priority(5).uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread t, Throwable e) {
                            Logger.getLogger("Minecraft").log(Level.SEVERE, t.getName() + " Thread 예외 발생.");
                            e.printStackTrace();
                        }}).build());

        return service;
    }

    public static void run(Runnable runnable) { getService().execute(new ExceptionHandle(runnable)); }

    public static SchedulerRunnable runTask(Runnable r) {
        return mainScheduler.runTask(r);
    }

    public static SchedulerRunnable runTimer(long initTick, long tick, Runnable runnable) {
        return mainScheduler.runTaskTimer(runnable, initTick, tick);
    }

    public static SchedulerRunnable runLater(long tick, Runnable runnable) {
        return mainScheduler.runTaskLater(runnable, tick);
    }

    public static SchedulerRunnable runTaskAsync(Runnable r) {
        return aSyncScheduler.runTask(r);
    }

    public static SchedulerRunnable runTimerAsync(long initTick, long tick, Runnable runnable) {
        return aSyncScheduler.runTaskTimer(runnable, initTick, tick);
    }

    public static SchedulerRunnable runLaterAsync(long tick, Runnable runnable) {
        return aSyncScheduler.runTaskLater(runnable, tick);
    }

    public static void shutdown() {
        if(service != null && service.isTerminated())
            service.shutdown();
    }

}

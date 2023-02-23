package net.starly.cashshop.util.schedule;

import net.starly.cashshop.database.ExceptionHandle;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncExecutors {

    private static ExecutorService service = null;
    public static ExecutorService getService() {
        if(service == null)
            service = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    (
                            new BasicThreadFactory.Builder())
                            .namingPattern("STAsyncThread-%d")
                            .daemon(true)
                            .priority(5).uncaughtExceptionHandler((t, e) -> {
                                Logger.getLogger("Minecraft").log(Level.SEVERE, t.getName() + " Thread 예외 발생.");
                                e.printStackTrace();
                            }).build());

        return service;
    }

    public static void run(Runnable runnable) { getService().execute(new ExceptionHandle(runnable)); }

    public static void shutdown() {
        if(service != null && service.isTerminated())
            service.shutdown();
    }

}

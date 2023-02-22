package net.starly.cashshop.util.schedule;

public interface STScheduler {
    SchedulerRunnable runTask(Runnable var1);

    SchedulerRunnable runTaskLater(Runnable var1, long var2);

    SchedulerRunnable runTaskTimer(Runnable var1, long var2, long var4);

    SchedulerRunnable getTask(int var1);

    long heartBeatProcess(long var1);
}
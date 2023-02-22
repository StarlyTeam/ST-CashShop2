package net.starly.cashshop.util.schedule;

public class SchedulerRunnable implements Runnable {
    private long initTick;
    private long period;
    private long startDelay;
    private Runnable obj;
    private long taskId;

    public SchedulerRunnable(long initTick, long period, long startDelay, Runnable obj, long taskId) {
        this.initTick = initTick;
        this.period = period;
        this.startDelay = startDelay;
        this.obj = obj;
        this.taskId = taskId;
    }

    protected final boolean processTickAndCheck(long currentTick) {
        long eslaped = currentTick - this.initTick;
        if (eslaped == this.startDelay) {
            this.startDelay = 0L;
            this.initTick = currentTick;
        } else if (eslaped < this.startDelay) {
            return false;
        }

        if (this.period == 0L) {
            this.period = -1L;
            return true;
        } else if (this.period <= -1L) {
            return false;
        } else {
            long a = currentTick - this.initTick;
            return a % this.period == 0L;
        }
    }

    protected final boolean haveToDestroy() {
        return this.period == -1L;
    }

    public void destroy() {
        this.period = -1L;
    }

    public void run() {
        this.obj.run();
    }

    public long getTaskId() {
        return this.taskId;
    }
}
package net.starly.cashshop.database;

public class ExceptionHandle implements Runnable {
    private final Runnable runnable;

    public ExceptionHandle(Runnable r) { runnable = r; }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

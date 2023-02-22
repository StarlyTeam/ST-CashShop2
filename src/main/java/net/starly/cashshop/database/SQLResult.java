package net.starly.cashshop.database;

import java.util.concurrent.Future;

public interface SQLResult extends Future<Void> {

    boolean isSuccess();
    Exception getCause();

}

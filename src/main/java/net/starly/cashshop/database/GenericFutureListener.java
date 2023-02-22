package net.starly.cashshop.database;

import java.util.EventListener;
import java.util.concurrent.Future;

public interface GenericFutureListener<F extends Future<?>> extends EventListener {

    void operationComplete(F future) throws Exception;

}

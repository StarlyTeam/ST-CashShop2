package net.starly.cashshop.command;

@FunctionalInterface
public interface STCommandExecutor<S> {

    void execute(S sender, boolean korean, String[] args);

}

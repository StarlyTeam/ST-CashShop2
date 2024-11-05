package net.starly.cashshop.cash;

import lombok.Getter;

import java.util.UUID;

public interface PlayerCash {

    UUID getOwner();

    String getOwnerName();

    int getId();

    long getCash();

    PlayerCash addCash(String source, Type type, long cash);

    PlayerCash subCash(String source, Type type, long cash);

    PlayerCash setCash(String source, Type type, long cash);

    PlayerCash save(boolean async);

    void writeLog(Type type, String source, long amount);

    void load();

    enum Type {
        ADD("입금"),
        SUB("출금"),
        SET("설정");
        @Getter
        private final String logName;

        Type(String logName) {
            this.logName = logName;
        }
    }

}

package net.starly.cashshop.cash.impl;

import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.exception.InvalidValueException;
import org.bukkit.Bukkit;

import java.util.UUID;

public class YamlPlayerCashImpl implements PlayerCash {

    private long cash;
    private final UUID uniqueId;
    private final int id;

    public YamlPlayerCashImpl(UUID uniqueId, int id) { this(uniqueId, 0, id); }
    public YamlPlayerCashImpl(UUID uniqueId, long cash, int id) {
        this.uniqueId = uniqueId;
        this.cash = cash;
        this.id = id;
    }

    @Override public int getId() { return id; }

    @Override public UUID getOwner() { return uniqueId; }

    @Override
    public String getOwnerName() {
        return Bukkit.getOfflinePlayer(uniqueId).getName();
    }

    @Override
    public long getCash() {
        return cash;
    }

    @Override
    public PlayerCash setCash(String source, Type type, long cash) {
        if(cash < 0) cash = 0;
        if(cash == this.cash) return this;
        this.cash = cash;
        return this;
    }

    @Override
    public PlayerCash addCash(String source, Type type, long cash) {

        return this;
    }
    @Override
    public PlayerCash subCash(String source, Type type, long cash) {

        return this;
    }

    @Override
    public PlayerCash save(boolean async) {

        return this;
    }

    @Override
    public void writeLog(Type type, String source, long amount) {

    }

    @Override
    public void load() {

    }

}

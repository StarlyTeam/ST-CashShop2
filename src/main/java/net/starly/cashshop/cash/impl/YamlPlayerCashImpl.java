package net.starly.cashshop.cash.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.util.schedule.AsyncExecutors;
import net.starly.cashshop.util.JsonUtil;
import org.bukkit.Bukkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class YamlPlayerCashImpl implements PlayerCash {

    private long cash;
    private final UUID uniqueId;
    private final int id;
    private boolean changed = false;
    public Boolean isChanged() { return changed; }

    public YamlPlayerCashImpl(UUID uniqueId, int id) { this(uniqueId, 0, id); }
    public YamlPlayerCashImpl(UUID uniqueId, long cash, int id) {
        this.uniqueId = uniqueId;
        this.cash = cash;
        this.id = id;
    }

    @Override public int getId() { return id; }

    @Override public UUID getOwner() { return uniqueId; }

    public File getSaveFile() { return new File(CashShopMain.getPlugin().getDataFolder(), "players/"+uniqueId+".json"); }

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
        changed = true;
        writeLog(Type.SET, source, cash);
        return this;
    }

    @Override
    public PlayerCash addCash(String source, Type type, long cash) {
        if(cash <= 0) return this;
        this.cash += cash;
        changed = true;
        writeLog(Type.ADD, source, cash);
        return this;
    }
    @Override
    public PlayerCash subCash(String source, Type type, long cash) {
        long temp = this.cash - cash;
        if(temp < 0) temp = 0;
        if(this.cash == temp) return this;
        this.cash = temp;
        changed = true;
        writeLog(Type.SUB, source, cash);
        return this;
    }

    public void save() { save(false); }

    @Override
    public PlayerCash save(boolean async) {
        if(async) AsyncExecutors.run(()->{
            changed = false;
            JsonUtil.toJsonFile(getSaveFile(), this);
        });
        else {
            changed = false;
            JsonUtil.toJsonFile(getSaveFile(), this);
        }
        return this;
    }

    @Override
    public void writeLog(Type type, String source, long amount) {
        File logFolder = new File(CashShopMain.getPlugin().getDataFolder(), "logs");
        File logFile = new File(logFolder, new SimpleDateFormat("yy_MM_dd_kk").format(new Date()) + ".log");
        try {
            if (!logFile.exists()) logFile.createNewFile();
            AsyncExecutors.run(() -> {
                try (BufferedWriter br = new BufferedWriter(new FileWriter(logFile))) {
                    br.append("uniqueId: ["+uniqueId+"] 타입: [" + type.getLogName() + "] 상대: [" + source + "] 금액: [" + amount + "] 잔고: [" + cash + "]");
                    br.flush();
                } catch (IOException ignored) { }
            });
        }catch (Exception ignored) {}
    }

    @Override
    public void load() {
        File file = getSaveFile();
        if(file.exists())
            this.cash = Objects.requireNonNull(JsonUtil.<YamlPlayerCashImpl>fromJsonFile(file, YamlPlayerCashImpl.class)).cash;
    }

}

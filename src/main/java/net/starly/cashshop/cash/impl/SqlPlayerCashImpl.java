package net.starly.cashshop.cash.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.database.ConnectionPoolManager;
import net.starly.cashshop.database.DatabaseContext;
import net.starly.cashshop.database.executor.AsyncExecutors;
import net.starly.cashshop.exception.InvalidValueException;
import org.bukkit.Bukkit;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class SqlPlayerCashImpl implements PlayerCash {

    private long cash;
    private final UUID uniqueId;
    private boolean changed = false;
    private int id;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yy년 MM월 dd일 kk:mm:ss");

    public SqlPlayerCashImpl(UUID uniqueID, int id) { this(uniqueID, 0, id); }
    public SqlPlayerCashImpl(UUID uniqueID, long cash, int id) {
        this.cash = cash;
        this.uniqueId = uniqueID;
        this.id = id;
    }

    @Override public int getId() { return id; }

    @Override
    public UUID getOwner() {
        return uniqueId;
    }

    @Override
    public String getOwnerName() {
        return Bukkit.getServer().getOfflinePlayer(uniqueId).getName();
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
        changed = idChecker();
        writeLog(Type.SET, source, cash);
        return this;
    }

    @Override
    public PlayerCash addCash(String source, Type type, long cash) {
        if(cash <= 0) return this;
        this.cash += cash;
        changed = idChecker();
        writeLog(Type.ADD, source, cash);
        return this;
    }

    @Override
    public PlayerCash subCash(String source, Type type, long cash) {
        long temp = this.cash - cash;
        if(temp < 0) temp = 0;
        if(this.cash == temp) return this;
        this.cash = temp;
        changed = idChecker();
        writeLog(Type.SUB, source, cash);
        return this;
    }

    private boolean idChecker() {
        if(id != -1) return true;
        try (
                Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                PreparedStatement stmt = con.prepareStatement(DatabaseContext.PLAYER_CASH_CREATE);
        ) {
            stmt.setString(1, uniqueId.toString());
            stmt.setLong(2, cash);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        id = CashShopMain.getPlugin().getPlayerCashRepository().getPlayerCash(uniqueId).getId();
        return false;
    }

    @Override
    public void writeLog(Type type, String source, long amount) {
        AsyncExecutors.run(()-> {
            try (
                    Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                    PreparedStatement stmt = con.prepareStatement(DatabaseContext.PLAYER_CASH_LOG_INSERT);
            ) {
                stmt.setInt(1, getId());
                stmt.setString(2, type.getLogName());
                stmt.setString(3, source);
                stmt.setLong(4, amount);
                stmt.setLong(5, cash);
                stmt.setString(6, dateFormat.format(new Date()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public PlayerCash save(boolean async) {
        if(!changed) return this;
        changed = false;
        try (
                Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                PreparedStatement stmt = con.prepareStatement(DatabaseContext.PLAYER_CASH_INSERT);
        ) {
            stmt.setLong(1, cash);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void load() {

    }

}

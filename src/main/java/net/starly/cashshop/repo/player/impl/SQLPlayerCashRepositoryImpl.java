package net.starly.cashshop.repo.player.impl;

import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.cash.impl.SqlPlayerCashImpl;
import net.starly.cashshop.database.ConnectionPoolManager;
import net.starly.cashshop.database.DatabaseContext;
import net.starly.cashshop.repo.player.PlayerCashRepository;
import net.starly.core.data.Config;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.UUID;

public class SQLPlayerCashRepositoryImpl implements PlayerCashRepository {

    @Override
    public void initializing(Config config) {
        ConnectionPoolManager.initializingPoolManager(config);
        try (
                Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                Statement statement = con.createStatement();
        ) {
            statement.executeUpdate(DatabaseContext.CREATE_CASH_TABLE_QUERY);
            statement.executeUpdate(DatabaseContext.CREATE_LOG_TABLE_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerCash getPlayerCash(OfflinePlayer player) {
        UUID uniqueId = player.getUniqueId();
        try (
                Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                PreparedStatement stmt = con.prepareStatement(DatabaseContext.PLAYER_CASH_GET);
        ) {
            stmt.setString(1, uniqueId.toString());
            ResultSet set = stmt.executeQuery();
            if (set.next())
                return new SqlPlayerCashImpl(uniqueId, set.getLong("balance"), set.getInt("id"));
            return null;
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlayerCash getPlayerCash(UUID uniqueId) {
        try (
                Connection con = ConnectionPoolManager.getInternalPool().getConnection();
                PreparedStatement stmt = con.prepareStatement(DatabaseContext.PLAYER_CASH_GET);
        ) {
            stmt.setString(1, uniqueId.toString());
            ResultSet set = stmt.executeQuery();
            if (set.next())
                return new SqlPlayerCashImpl(uniqueId, set.getLong("balance"), set.getInt("id"));
            return new SqlPlayerCashImpl(uniqueId, -1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlayerCash unregisterCash(UUID uniqueId) {
        return null;
    }

    @Override
    public void registerPlayerCash(PlayerCash playerCash) {
    }

    @Override
    public void close() {
    }
}

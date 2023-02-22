package net.starly.cashshop.repo.player;

import net.starly.cashshop.cash.PlayerCash;
import net.starly.core.data.Config;

import java.util.UUID;

public interface PlayerCashRepository {

    void initializing(Config config);
    PlayerCash getPlayerCash(UUID uniqueId);
    PlayerCash unregisterCash(UUID uniqueId);
    void registerPlayerCash(PlayerCash playerCash);
    void close();

}

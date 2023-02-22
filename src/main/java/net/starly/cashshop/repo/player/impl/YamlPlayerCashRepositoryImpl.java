package net.starly.cashshop.repo.player.impl;

import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.repo.player.PlayerCashRepository;
import net.starly.core.data.Config;

import java.util.UUID;
import java.util.function.Consumer;

public class YamlPlayerCashRepositoryImpl implements PlayerCashRepository {

    @Override
    public void initializing(Config config) {

    }

    @Override
    public PlayerCash getPlayerCash(UUID uniqueId) {
        return null;
    }

    @Override
    public PlayerCash unregisterCash(UUID uniqueId) {
        return null;
    }

    @Override
    public void registerPlayerCash(PlayerCash playerCash) {

    }
}

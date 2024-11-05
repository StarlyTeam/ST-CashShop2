package net.starly.cashshop.repo.player.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.cash.impl.YamlPlayerCashImpl;
import net.starly.cashshop.repo.player.PlayerCashRepository;
import net.starly.cashshop.util.JsonUtil;
import net.starly.core.data.Config;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class YamlPlayerCashRepositoryImpl implements PlayerCashRepository {

    private File playersFolder;
    private File logFolder;
    private Map<UUID, YamlPlayerCashImpl> cashMap = new HashMap<>();
    private BukkitTask saveTask = null;

    @Override
    public void initializing(Config config) {
        JavaPlugin plugin = CashShopMain.getPlugin();
        playersFolder = new File(plugin.getDataFolder(), "players");
        logFolder = new File(plugin.getDataFolder(), "logs");
        if (!playersFolder.exists()) playersFolder.mkdirs();
        if (!logFolder.exists()) logFolder.mkdirs();
        for (File file : Objects.requireNonNull(playersFolder.listFiles())) {
            YamlPlayerCashImpl impl = JsonUtil.fromJsonFile(file, YamlPlayerCashImpl.class);
            if (impl != null)
                cashMap.put(impl.getOwner(), new YamlPlayerCashImpl(impl.getOwner(), impl.getCash(), impl.getId()));
        }
        if (saveTask == null)
            saveTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::saveAll, 1200, 1200);
    }

    @Override
    public PlayerCash getPlayerCash(UUID uniqueId) {
        return cashMap.computeIfAbsent(uniqueId, (it) -> {
            YamlPlayerCashImpl impl = new YamlPlayerCashImpl(uniqueId, 0, -1);
            impl.load();
            return impl;
        });
    }

    @Override
    public PlayerCash getPlayerCash(OfflinePlayer player) {
        if (cashMap.containsKey(player.getUniqueId())) return getPlayerCash(player.getUniqueId());
        return null;
    }

    @Override
    public PlayerCash unregisterCash(UUID uniqueId) {
        return cashMap.remove(uniqueId).save(true);
    }

    @Override
    public void registerPlayerCash(PlayerCash playerCash) {

    }

    @Override
    public void close() {
        if (saveTask != null) saveTask.cancel();
        saveTask = null;
        saveAll();
    }

    private void saveAll() {
        cashMap.values().stream().filter(YamlPlayerCashImpl::isChanged).forEach(YamlPlayerCashImpl::save);
    }

}

package net.starly.cashshop;

import lombok.Getter;
import net.starly.cashshop.command.executor.CashCommand;
import net.starly.cashshop.command.executor.CashShopCommand;
import net.starly.cashshop.database.ConnectionPoolManager;
import net.starly.cashshop.database.DatabaseContext;
import net.starly.cashshop.database.executor.AsyncExecutors;
import net.starly.cashshop.message.MessageLoader;
import net.starly.cashshop.repo.player.PlayerCashRepository;
import net.starly.cashshop.repo.player.impl.SQLPlayerCashRepositoryImpl;
import net.starly.cashshop.repo.player.impl.YamlPlayerCashRepositoryImpl;
import net.starly.cashshop.repo.shop.CashShopRepository;
import net.starly.cashshop.repo.shop.impl.EmptyCashShopRepositoryImpl;
import net.starly.core.bstats.Metrics;
import net.starly.core.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CashShopMain extends JavaPlugin {
    private static CashShopMain plugin;

    @Getter private Config stConfig;
    @Getter private CashShopRepository cashShopRepository;
    @Getter private PlayerCashRepository playerCashRepository;

    @Override
    public void onLoad() { plugin = this; }

    @Override
    public void onEnable() {
        // DEPENDENCY
        if (Bukkit.getPluginManager().getPlugin("ST-Core") == null) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + plugin.getName() + "] 다운로드 링크 : §fhttp://starly.kr/discord");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // VERSION CONTROL
        VersionController.getInstance();

        new Metrics(this, 12345); // TODO: 수정

        // CONFIG
        stConfig = new Config("config", this);
        stConfig.loadDefaultConfig();

        // COMMAND
        new CashCommand(this, "cash");
        new CashShopCommand(this, "net/starly/cashshop");

        // EVENT

        // INITIALIZING
        loadConfiguration(false);

    }

    public void loadConfiguration() { loadConfiguration(true); }
    private void loadConfiguration(boolean reload) {
        if(reload) {
            stConfig.reloadConfig();
            ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
            if(pool != null) pool.closePool();
        }
        DatabaseContext.initializingContext(stConfig);
        MessageLoader.load(stConfig);
        cashShopRepository = new EmptyCashShopRepositoryImpl();
        if(stConfig.getBoolean("database.use")) playerCashRepository = new SQLPlayerCashRepositoryImpl();
        else playerCashRepository = new YamlPlayerCashRepositoryImpl();
        playerCashRepository.initializing(stConfig);
    }

    @Override
    public void onDisable() {
        AsyncExecutors.shutdown();
        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
        if(pool != null) pool.closePool();
    }

    public static CashShopMain getPlugin() {
        return plugin;
    }
}
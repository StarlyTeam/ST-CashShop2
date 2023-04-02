package net.starly.cashshop;

import ch.njol.skript.Skript;
import lombok.Getter;
import net.starly.cashshop.command.executor.CashCommand;
import net.starly.cashshop.command.executor.CashShopCommand;
import net.starly.cashshop.database.ConnectionPoolManager;
import net.starly.cashshop.database.DatabaseContext;
import net.starly.cashshop.repo.sound.SoundRepository;
import net.starly.cashshop.listener.ContainerListener;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.listener.ShopListener;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.support.placeholder.CashExpansion;
import net.starly.cashshop.util.ItemStackNameUtil;
import net.starly.cashshop.executor.AsyncExecutors;
import net.starly.cashshop.message.MessageLoader;
import net.starly.cashshop.repo.player.PlayerCashRepository;
import net.starly.cashshop.repo.player.impl.SQLPlayerCashRepositoryImpl;
import net.starly.cashshop.repo.player.impl.YamlPlayerCashRepositoryImpl;
import net.starly.cashshop.repo.shop.CashShopRepository;
import net.starly.cashshop.repo.shop.impl.CashShopRepositoryImpl;
import net.starly.cashshop.util.VersionController;
import net.starly.core.bstats.Metrics;
import net.starly.core.data.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CashShopMain extends JavaPlugin {
    private static CashShopMain plugin;

    @Getter private Config stConfig;
    @Getter private CashShopRepository cashShopRepository;
    @Getter private PlayerCashRepository playerCashRepository;
    @Getter private SoundRepository soundRepository;

    @Override
    public void onLoad() { plugin = this; }

    @Override
    public void onEnable() {
        // DEPENDENCY
        if (!isPluginEnabled("net.starly.core.StarlyCore")) {
            Bukkit.getLogger().warning("[" + plugin.getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            Bukkit.getLogger().warning("[" + plugin.getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        // VERSION CONTROL
        VersionController.getInstance();

        // INITIALIZING LANGUAGE
        ItemStackNameUtil.initializingLocale(getServer());

        new Metrics(this, 17798);

        // CONFIG
        stConfig = new Config("config", this);
        stConfig.loadDefaultConfig();

        // COMMAND
        new CashCommand(this, "cash");
        new CashShopCommand(this, "cashshop");

        // EVENT
        getServer().getPluginManager().registerEvents(new ContainerListener(), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);

        // INITIALIZING
        loadConfiguration(false);

        // SUPPORTS
        if (!isPluginEnabled("ch.njol.skript.Skript"))
            Bukkit.getLogger().warning("[" + plugin.getName() + "] Skript 플러그인이 존재하지 않아 Skript 기능이 비활성화 됩니다.");
        else {
            try { Skript.registerAddon(this).loadClasses("net.starly.cashshop.support", "skript"); }
            catch (Exception e) { e.printStackTrace(); }
        }
        if (!isPluginEnabled("me.clip.placeholderapi.PlaceholderAPIPlugin"))
            Bukkit.getLogger().warning("[" + plugin.getName() + "] PlaceholderAPI 플러그인이 존재하지 않아 PAPI 기능이 비활성화 됩니다.");
        else new CashExpansion(this).register();
    }

    public void loadConfiguration() { loadConfiguration(true); }
    private void loadConfiguration(boolean reload) {
        if(reload) {
            stConfig.reloadConfig();
            ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
            if(pool != null) pool.closePool();
        }
        GlobalShopSettings.getInstance().initializing(stConfig);
        DatabaseContext.initializingContext(stConfig);
        MessageLoader.load(stConfig);
        cashShopRepository = new CashShopRepositoryImpl();
        if(stConfig.getBoolean("database.use")) playerCashRepository = new SQLPlayerCashRepositoryImpl();
        else playerCashRepository = new YamlPlayerCashRepositoryImpl();
        if(soundRepository == null) soundRepository = SoundRepository.getInstance();

        playerCashRepository.initializing(stConfig);
        cashShopRepository.initializing(stConfig);
        soundRepository.initializing(this);
    }

    @Override
    public void onDisable() {
        cashShopRepository.getShops().forEach((shop)->shop.save(false));
        STContainer.closeShopAll();
        AsyncExecutors.shutdown();
        ConnectionPoolManager pool = ConnectionPoolManager.getInternalPool();
        playerCashRepository.close();
        if(pool != null) pool.closePool();
    }

    public static CashShopMain getPlugin() {
        return plugin;
    }

    private boolean isPluginEnabled(String path) {
        try {
            Class.forName(path);
            return true;
        } catch (NoClassDefFoundError ignored) {
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }
}
package net.starly.cashshop.shop.settings;

import lombok.Getter;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.core.data.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import java.util.stream.Collectors;

public class GlobalShopSettings {

    private static GlobalShopSettings instance;
    public static GlobalShopSettings getInstance() {
        if(instance == null) instance = new GlobalShopSettings();
        return instance;
    }
    private GlobalShopSettings() {}

    private boolean loaded = false;

    @Getter private boolean printNpcName = false;
    @Getter @Nullable private PlayerHeadSetting headSetting = null;
    @Getter @Nullable private ItemSetting itemSetting = null;

    public String getStarlyKey(CashShopImpl shop) {
        if(printNpcName) return "ST-shop-" + ChatColor.stripColor(shop.getNpc());
        else return "ST-shop-" + ChatColor.stripColor(shop.getName());
    }

    public void initializing(Config config) {
        if(loaded) {
            printNpcName = false;
            headSetting = null;
            itemSetting = null;
            loaded = false;
        }
        ConfigurationSection section = config.getConfigurationSection("shop");
        if(section == null) return;

        printNpcName = section.getBoolean("print-npc-name");
        headSetting = section.getBoolean("player-head-info.use") ? new PlayerHeadSetting(
                section.getInt("player-head-info.slot"),
                ChatColor.translateAlternateColorCodes('&', section.getString("player-head-info.name")),
                section.getStringList("player-head-info.lore").stream().map((it)->ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList())
        ) : null;
        try {
            ConfigurationSection i = section.getConfigurationSection("item");
            itemSetting = new ItemSetting(
                    i.getBoolean("hide-attribute"),
                    i.getBoolean("hide-unbreakable"),
                    i.getBoolean("hide-enchants"),
                    i.getBoolean("hide-none-value-item-info"),
                    new ItemSetting.ClickEvent(
                            ItemSetting.ClickEvent.Type.valueOf(i.getString("click.type")),
                            i.getInt("click.amount"),
                            ChatColor.translateAlternateColorCodes('&', i.getString("click.text"))
                    ),
                    new ItemSetting.ClickEvent(
                            ItemSetting.ClickEvent.Type.valueOf(i.getString("shift-click.type")),
                            i.getInt("shift-click.amount"),
                            ChatColor.translateAlternateColorCodes('&', i.getString("shift-click.text"))
                    ),
                    i.getStringList("default-lore").stream().map((it)->ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList()),
                    i.getStringList("sale-lore").stream().map((it)->ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList()),
                    i.getStringList("limited-lore").stream().map((it)->ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList()),
                    i.getStringList("limited-sale-lore").stream().map((it)->ChatColor.translateAlternateColorCodes('&', it)).collect(Collectors.toList())
            );
        } catch (Exception e) {
            e.printStackTrace();
            itemSetting = null;
        }
        loaded = true;
    }


}

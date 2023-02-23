package net.starly.cashshop.support.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.util.FormattingUtil;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CashExpansion2 extends PlaceholderExpansion {

    private final CashShopMain plugin;
    public CashExpansion2(CashShopMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "starly";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ST-CashShop";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(player == null) return null;
        if(params.equalsIgnoreCase("test")) {
            return player.getName();
        } else return null;
    }

}

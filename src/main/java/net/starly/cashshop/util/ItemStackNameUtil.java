package net.starly.cashshop.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ItemStackNameUtil {

    public static String getItemName(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.hasDisplayName()) return itemMeta.getLocalizedName();
        String displayName = itemStack.getType().toString();
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        return new String(displayName.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

}

package net.starly.cashshop.shop.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.util.FormattingUtil;
import net.starly.cashshop.util.PlayerSkullManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class PlayerHeadSetting {

    private int slot;
    private String name;
    private List<String> loreSetting;

    public ItemStack getHeadItem(Player player, PlayerCash cash) {
        ItemStack head = PlayerSkullManager.getPlayerSkull(player.getUniqueId());
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(convertLore(player, cash));
        head.setItemMeta(meta);
        return head;
    }

    private List<String> convertLore(Player player, PlayerCash cashData) {
        return loreSetting
                .stream()
                .map((it)-> it
                        .replace("{cash}", FormattingUtil.formattingCash(cashData.getCash()))
                        .replace("{player}", player.getName())
                ).collect(Collectors.toList());
    }


}

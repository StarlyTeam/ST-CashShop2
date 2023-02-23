package net.starly.cashshop.listener;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.shop.container.impl.CashShopContainer;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;

public class ShopListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if(event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
            if (player.getOpenInventory() != null) {
                InventoryView openInventory = player.getOpenInventory();
                if(!openInventory.getType().equals(InventoryType.PLAYER) && !openInventory.getType().equals(InventoryType.CREATIVE))
                    return;
            }
            Entity entity = event.getRightClicked();
            if(entity.getCustomName() == null) return;
            CashShopImpl shop = CashShopMain.getPlugin().getCashShopRepository().getShopAtNpcName(entity.getCustomName());
            if(shop != null) {
                if(shop.isClosed()) {
                    if(!player.isOp()) {
                        CashShopMessageContextImpl.getInstance().get(MessageContext.Type.ERROR, "shopIsClosed").send(player);
                        return;
                    }
                }
                new CashShopContainer(GlobalShopSettings.getInstance().isPrintNpcName() ? entity.getCustomName() : shop.getName(), shop).open(player, true);
            }
        }
    }

}

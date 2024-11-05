package net.starly.cashshop.shop.container.impl;

import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.container.wrapper.InventoryClickEventWrapper;
import net.starly.cashshop.shop.impl.CashShopImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class CashShopInsertItemContainer extends STContainer {

    public CashShopInsertItemContainer(CashShopImpl shop) {
        super(shop.getLine() * 9, shop.getName() + "§r [아이템 설정]", false, shop);
    }

    @Override
    protected void guiClick(InventoryClickEventWrapper event) {

    }

    @Override
    protected void guiClose(InventoryCloseEvent event) {
        shop.registerContents(event.getInventory().getContents());
        shop.setClose(true);
        new CashShopSettingContainer(shop).open((Player) event.getPlayer());
    }

    @Override
    protected void guiDrag(InventoryDragEvent event) {

    }

    @Override
    protected void initializingInventory(Inventory inventory) {
        shop.forEachIndexed((index, shopItem) -> inventory.setItem(index, shopItem == null ? null : shopItem.getOriginal()));
    }
}
package net.starly.cashshop.listener;

import net.starly.cashshop.shop.container.STContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

public class ContainerListener implements Listener {

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        STContainer container = getSTContainer(event.getView());
        if(container != null) container.$click(event);
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        STContainer container = getSTContainer(event.getView());
        if(container != null) container.$close(event);
    }

    @EventHandler
    public void onDragInventory(InventoryDragEvent event) {
        STContainer container = getSTContainer(event.getView());
        if(container != null) container.$drag(event);
    }

    private STContainer getSTContainer(InventoryView view) {
        if(view == null || view.getTopInventory() == null) return null;
        InventoryHolder topHolder = view.getTopInventory().getHolder();
        if(topHolder == null) return null;
        return topHolder instanceof STContainer ? (STContainer) topHolder : null;
    }
}

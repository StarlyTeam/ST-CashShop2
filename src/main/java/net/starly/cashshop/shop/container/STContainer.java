package net.starly.cashshop.shop.container;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.repo.sound.STSound;
import net.starly.cashshop.repo.sound.SoundRepository;
import net.starly.cashshop.shop.container.button.STButton;
import net.starly.cashshop.shop.container.wrapper.ButtonClickEventWrapper;
import net.starly.cashshop.shop.container.wrapper.InventoryClickEventWrapper;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.shop.settings.PlayerHeadSetting;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class STContainer implements InventoryHolder {

    private static final Map<String, String> viewerMap = new HashMap<>();
    private static final CashShopMain plugin;
    private static final Server server;
    static {
        plugin = CashShopMain.getPlugin();
        server = plugin.getServer();
    }
    private static void registerPlayer(String shopKey, Player player) { viewerMap.put(player.getName(), shopKey); }
    private static void unregisterPlayer(Player player) { viewerMap.remove(player.getName()); }
    public static void closeShopAll() {
        viewerMap.keySet().forEach(STContainer::closeFunc);
    }
    public static void closeShop(String shopKey) {
        viewerMap.entrySet().stream()
                .filter((entry)-> entry.getValue().equals(shopKey))
                .map(Map.Entry::getKey)
                .forEach(STContainer::closeFunc);
    }
    private static void closeFunc(String playerName) {
        Player target = server.getPlayer(playerName);
        if(target != null) {
            target.closeInventory();
            CashShopMessageContextImpl.getInstance().get(MessageContext.Type.ERROR, "closeShop").send(target);
        } else viewerMap.remove(playerName);
    }

    protected Inventory inventory;
    protected Player viewer;

    @Override public Inventory getInventory() { return inventory; }

    private final HashMap<Integer, STButton> slotMap = new HashMap<>();
    private InventoryType type;
    private final int size;
    private final String title;
    private final boolean cancel;
    public final String starlyShopKey;
    protected final CashShopImpl shop;
    public STContainer(InventoryType type, String title, boolean cancel, CashShopImpl shop) {
        this(-1, title, cancel, shop);
        this.type = type;
        createInventory();
    }

    public STContainer(int size, String title, boolean cancel, CashShopImpl shop) {
        type = null;
        this.shop = shop;
        this.cancel = cancel;
        this.size = size;
        this.title = title;
        starlyShopKey = "ST-shop-"+ ChatColor.stripColor(title);
        createInventory();
    }

    private void createInventory() {
        Server server = CashShopMain.getPlugin().getServer();
        inventory = type == null ? server.createInventory(this, size, title) : server.createInventory(this, type, title);
        initializingInventory(inventory);
    }

    public void registerButton(int slot, STButton button) {
        if(slotMap.containsKey(slot)) {
            STButton old = slotMap.get(slot);
            ItemStack oldItem = inventory.getItem(slot);
            oldItem.setType(button.getOriginalItemStack().getType());
            oldItem.setDurability(button.getOriginalItemStack().getDurability());
            oldItem.setAmount(button.getOriginalItemStack().getAmount());
            oldItem.setItemMeta(button.getOriginalItemStack().getItemMeta());
            return;
        }
        slotMap.put(slot, button);
        inventory.setItem(slot, button.getItemStack());
    }

    public void refresh() {
        for(int i = 0; i < size; i++) {
            if(slotMap.containsKey(i)) {
                if (!slotMap.get(i).isCleanable()) continue;
            }
            inventory.setItem(i, null);
            slotMap.remove(i);
        }
        initializingInventory(inventory);
        openedInitializing();
    }

    public void open(Player player) { this.open(player, false); }
    public void open(Player player, boolean playSound) {
        if (player == null || !player.isOnline()) return;

        try {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory().getType() != InventoryType.CRAFTING) {
                    player.closeInventory();
                }
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    viewer = player;
                    openedInitializing();
                    player.openInventory(inventory);
                    registerPlayer(starlyShopKey, player);
                    if (playSound) {
                        STSound sound = SoundRepository.getInstance().getSound(shop.getSoundKey());
                        if (sound != null) sound.playSound(player);
                    }
                }, 1L);
            }, 1L);
        } catch (Exception e) { e.printStackTrace(); }
    }
    protected void openedInitializing() {}

    protected void headItemSetting() {
        PlayerHeadSetting setting = GlobalShopSettings.getInstance().getHeadSetting();
        if(setting != null) if(size > setting.getSlot()) {
            ItemStack skull = setting.getHeadItem(viewer, plugin.getPlayerCashRepository().getPlayerCash(viewer.getUniqueId()));
            if(inventory.getItem(setting.getSlot()) != null) inventory.getItem(setting.getSlot()).setItemMeta(skull.getItemMeta());
            else inventory.setItem(setting.getSlot(), skull);
        }
    }
    public void $click(InventoryClickEvent event) {
        if(cancel) event.setCancelled(true);
        InventoryClickEventWrapper wrapper = new InventoryClickEventWrapper(event, false);
        guiClick(wrapper);
        ItemStack itemChecker = event.getCurrentItem();
        if(itemChecker == null || itemChecker.getType().equals(Material.AIR)) return;
        if(!wrapper.isButtonCancelled() && slotMap.containsKey(wrapper.getRawSlot())) {
            STButton button = slotMap.get(wrapper.getRawSlot());
            button.execute(this, new ButtonClickEventWrapper(event, button));
        }
    }
    public void $close(InventoryCloseEvent event) {
        unregisterPlayer((Player) event.getPlayer());
        guiClose(event);
    }

    public void $drag(InventoryDragEvent event) { guiDrag(event); }
    public void closeShop() { closeShop(starlyShopKey); }

    protected abstract void guiClick(InventoryClickEventWrapper event);
    protected abstract void guiClose(InventoryCloseEvent event);
    protected abstract void guiDrag(InventoryDragEvent event);
    protected abstract void initializingInventory(Inventory inventory);

}

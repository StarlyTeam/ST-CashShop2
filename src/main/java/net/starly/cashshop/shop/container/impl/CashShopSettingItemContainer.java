package net.starly.cashshop.shop.container.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.container.button.STButton;
import net.starly.cashshop.shop.container.wrapper.InventoryClickEventWrapper;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.util.Replacer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

public class CashShopSettingItemContainer extends STContainer {

    private static final CashShopMain plugin = CashShopMain.getPlugin();
    private boolean changed = false;
    private boolean close = true;

    public CashShopSettingItemContainer(CashShopImpl shop) {
        super(shop.getLine() * 9, shop.getName() + "§r [아이템 세부 설정]", true, shop);
    }

    @Override protected void guiClick(InventoryClickEventWrapper event) { }

    @Override
    protected void guiClose(InventoryCloseEvent event) {
        if(close && changed) {
            shop.setClose(true);
            shop.save(true);
            changed = false;
            new CashShopSettingContainer(shop).open((Player) event.getPlayer());
        } else if(close) new CashShopSettingContainer(shop).open((Player) event.getPlayer());
    }

    @Override
    protected void guiDrag(InventoryDragEvent event) {

    }

    @Override
    protected void initializingInventory(Inventory inventory) {
        close = true;
        CashShopMessageContextImpl context = CashShopMessageContextImpl.getInstance();
        shop.forEachIndexed((index, shopItem)-> {
            if(shopItem == null) return;
            new STButton.STButtonBuilder(shopItem.getSettingItem())
                    .setCleanable(false)
                    .setClickFunction((wrapper, container) -> {
                        if(wrapper.pressQButton()) {
                            shopItem.setNowAmount(shopItem.getAmount());
                            context.get(MessageContext.Type.DEFAULT, "systemMessage", new Replacer.ReplacerBuilder().appendSystemMessage("재고 채우기").build().getFunction()).send(wrapper.getPlayer());
                            shop.setClose(true);
                            changed = true;
                            refresh();
                        }
                        else if(wrapper.isShift()) {
                            shop.removeItem(index);
                            context.get(MessageContext.Type.DEFAULT, "systemMessage", new Replacer.ReplacerBuilder().appendSystemMessage("아이템 삭제 [slot:"+index+"]").build().getFunction()).send(wrapper.getPlayer());
                            shop.setClose(true);
                            wrapper.getItemStack().setType(Material.AIR);
                            changed = true;
                            refresh();
                        } else if(wrapper.isRight()) {
                            close = false;
                            wrapper.getPlayer().closeInventory();
                            Listener listener = new Listener() {};
                            plugin.getServer().getScheduler().runTaskLater(plugin, ()->{
                                try {
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                } catch (Exception ignored) {}
                            }, 3000L);
                            wrapper.getPlayer().sendMessage(context.getOnlyString(MessageContext.Type.DEFAULT, "prefix") + "희망하는 가격을 채팅으로 입력해주세요.");
                            plugin.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, EventPriority.LOWEST, (unused, e)-> {
                                AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
                                if(wrapper.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                                    event.setCancelled(true);
                                    try {
                                        long value = Long.parseLong(event.getMessage());
                                        shopItem.setOriginalCost(value);
                                        context.get(MessageContext.Type.DEFAULT, "systemMessage", new Replacer.ReplacerBuilder().appendSystemMessage("가격 수정 > "+ value).build().getFunction()).send(event.getPlayer());
                                        changed = true;
                                    } catch (NumberFormatException ignored) {
                                        context.get(MessageContext.Type.ERROR, "notNumber").send(event.getPlayer());
                                    }
                                    refresh();
                                    open(event.getPlayer());
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                }
                            }, plugin);
                        } else if(wrapper.isWheel()) {
                            close = false;
                            wrapper.getPlayer().closeInventory();
                            Listener listener = new Listener() {};
                            plugin.getServer().getScheduler().runTaskLater(plugin, ()->{
                                try {
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                } catch (Exception ignored) {}
                            }, 3000L);
                            wrapper.getPlayer().sendMessage(context.getOnlyString(MessageContext.Type.DEFAULT, "prefix") + "희망하는 재고를 채팅으로 입력해주세요. §7(무제한 : 음수)");
                            plugin.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, EventPriority.LOWEST, (unused, e)-> {
                                AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
                                if(wrapper.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                                    event.setCancelled(true);
                                    try {
                                        int value = Integer.parseInt(event.getMessage());
                                        if(shopItem.setAmount(value)) changed = true;
                                        context.get(MessageContext.Type.DEFAULT, "systemMessage", new Replacer.ReplacerBuilder().appendSystemMessage("재고 수정 > "+ shopItem.getAmount()).build().getFunction()).send(event.getPlayer());
                                    } catch (NumberFormatException ignored) {
                                        context.get(MessageContext.Type.ERROR, "notNumber").send(event.getPlayer());
                                    }
                                    refresh();
                                    open(event.getPlayer());
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                }
                            }, plugin);
                        } else if(wrapper.isLeft()) {
                            close = false;
                            wrapper.getPlayer().closeInventory();
                            Listener listener = new Listener() {};
                            plugin.getServer().getScheduler().runTaskLater(plugin, ()->{
                                try {
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                } catch (Exception ignored) {}
                            }, 3000L);
                            wrapper.getPlayer().sendMessage(context.getOnlyString(MessageContext.Type.DEFAULT, "prefix") + "희망하는 할인율을 채팅으로 입력해주세요. §7(0 < n < 100)");
                            plugin.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, EventPriority.LOWEST, (unused, e)-> {
                                AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
                                if(wrapper.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                                    event.setCancelled(true);
                                    try {
                                        double value = Double.parseDouble(event.getMessage());
                                        if(shopItem.setSale(value)) changed = true;
                                        context.get(MessageContext.Type.DEFAULT, "systemMessage", new Replacer.ReplacerBuilder().appendSystemMessage("할인율 설정 > "+ shopItem.getSale()).build().getFunction()).send(event.getPlayer());
                                    } catch (NumberFormatException ignored) {
                                        context.get(MessageContext.Type.ERROR, "notNumber").send(event.getPlayer());
                                    }
                                    refresh();
                                    open(event.getPlayer());
                                    AsyncPlayerChatEvent.getHandlerList().unregister(listener);
                                }
                            }, plugin);
                        }
                    }).build().setSlot(this, index);
        });
    }
}
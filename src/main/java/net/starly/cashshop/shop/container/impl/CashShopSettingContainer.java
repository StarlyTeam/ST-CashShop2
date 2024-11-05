package net.starly.cashshop.shop.container.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.repo.sound.STSound;
import net.starly.cashshop.repo.sound.SoundRepository;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.container.button.STButton;
import net.starly.cashshop.shop.container.wrapper.InventoryClickEventWrapper;
import net.starly.cashshop.shop.impl.CashShopImpl;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

public class CashShopSettingContainer extends STContainer {

    private static final CashShopMain plugin = CashShopMain.getPlugin();
    private boolean changed = false;

    public CashShopSettingContainer(CashShopImpl shop) {
        super(27, shop.getName() + "§r [설정]", true, shop);
    }

    @Override
    protected void guiClick(InventoryClickEventWrapper event) {

    }

    @Override
    protected void guiClose(InventoryCloseEvent event) {
        if (changed) {
            shop.save(true);
            changed = false;
        }
    }

    @Override
    protected void guiDrag(InventoryDragEvent event) {

    }

    @Override
    protected void initializingInventory(Inventory inventory) {
        new STButton.STButtonBuilder(shop.isClosed() ? Material.RED_SHULKER_BOX : Material.GREEN_SHULKER_BOX)
                .setCleanable(false)
                .setDisplayName("§6상점 §7(활성화/비활성화)")
                .setLore(Arrays.asList("", "§e ▸ §f상점을 §a§l§n활성화§f/§c§l§n비활성화§f 할 수 있습니다.", "§e ▸ §f현재 상태 : " + (!shop.isClosed() ? "§a§l[활성화]" : "§c§l[비활성화]")))
                .setClickFunction((wrapper, container) -> {
                    shop.setClose(!shop.isClosed());
                    changed = true;
                    container.refresh();
                }).build().setSlot(this, 9);

        new STButton.STButtonBuilder(Material.EMERALD)
                .setDisplayName("§6NPC 설정")
                .setCleanable(false)
                .setLore(Arrays.asList("", "§e ▸ §f상점 §e§l§nNPC§f 를 연결할 수 있습니다.", "§e ▸ §f현재 NPC : " + (shop.getNpc().isEmpty() ? "§7설정되지 않음" : shop.getNpc())))
                .setClickFunction((wrapper, container) -> {
                    wrapper.getPlayer().sendMessage(CashShopMessageContextImpl.getInstance().getOnlyString(MessageContext.Type.DEFAULT, "prefix") + "§7NPC로 설정할 엔티티를 우클릭하세요.");
                    wrapper.getPlayer().closeInventory();
                    Listener listener = new Listener() {
                    };
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        try {
                            PlayerInteractAtEntityEvent.getHandlerList().unregister(listener);
                        } catch (Exception ignored) {
                        }
                    }, 3000L);
                    plugin.getServer().getPluginManager().registerEvent(PlayerInteractAtEntityEvent.class, listener, EventPriority.LOWEST, (unused, e) -> {
                        PlayerInteractAtEntityEvent event = (PlayerInteractAtEntityEvent) e;
                        if (wrapper.getPlayer().getUniqueId().equals(event.getPlayer().getUniqueId())) {
                            if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                                event.setCancelled(true);
                                Entity target = event.getRightClicked();
                                if (target.getCustomName() == null) return;
                                shop.setNpc(target.getCustomName());
                                shop.save(true);
                                PlayerInteractAtEntityEvent.getHandlerList().unregister(listener);
                            }
                        }
                    }, plugin);
                }).build().setSlot(this, 10);

        new STButton.STButtonBuilder(Material.CHEST)
                .setDisplayName("§6아이템 설정")
                .setLore("", "§e ▸ §f상점에 아이템을 설정할 수 있습니다.", "§e ▸ §f클릭 후, 새로운 GUI 에 아이템을 집어넣으면 등록됩니다.")
                .setCleanable(false)
                .setClickFunction((wrapper, container) -> {
                    wrapper.getPlayer().closeInventory();
                    new CashShopInsertItemContainer(shop).open(wrapper.getPlayer());
                }).build().setSlot(this, 11);

        new STButton.STButtonBuilder(Material.ENDER_CHEST)
                .setDisplayName("§6아이템 세부 설정")
                .setCleanable(false)
                .setLore("", "§e ▸ §f상점 아이템의 세부 설정을 할 수 있습니다.", "§e ▸ §f클릭 후, 새로운 GUI 에 자세한 설명이 있습니다.")
                .setClickFunction((wrapper, container) -> {
                    wrapper.getPlayer().closeInventory();
                    new CashShopSettingItemContainer(shop).open(wrapper.getPlayer());
                }).build().setSlot(this, 12);
        STSound sound = SoundRepository.getInstance().getSound(shop.getSoundKey());
        new STButton.STButtonBuilder(Material.NOTE_BLOCK)
                .setDisplayName("§6사운드 설정 §e§l[입장]")
                .setCleanable(false)
                .setLore("", "§e ▸ §f상점을 열 때 출력 될 사운드를 설정합니다.", "§e ▸ §f클릭 시 변경됩니다.", "§e ▸ §f현재 사운드 : §b" + (sound == null ? "§7설정 값 없음" : sound.getTemplateName()))
                .setClickFunction((wrapper, container) -> {
                    String key = SoundRepository.getInstance().next(shop.getSoundKey());
                    shop.setSoundKey(key);
                    STSound temp = SoundRepository.getInstance().getSound(key);
                    if (temp != null) temp.playSound(wrapper.getPlayer());
                    changed = true;
                    refresh();
                }).build().setSlot(this, 14);
        sound = SoundRepository.getInstance().getSound(shop.getCloseSoundKey());
        new STButton.STButtonBuilder(Material.NOTE_BLOCK)
                .setDisplayName("§6사운드 설정 §e§l[퇴장]")
                .setCleanable(false)
                .setLore("", "§e ▸ §f상점을 닫을 때 출력 될 사운드를 설정합니다.", "§e ▸ §f클릭 시 변경됩니다.", "§e ▸ §f현재 사운드 : §b" + (sound == null ? "§7설정 값 없음" : sound.getTemplateName()))
                .setClickFunction((wrapper, container) -> {
                    String key = SoundRepository.getInstance().next(shop.getCloseSoundKey());
                    shop.setCloseSoundKey(key);
                    STSound temp = SoundRepository.getInstance().getSound(key);
                    if (temp != null) temp.playSound(wrapper.getPlayer());
                    changed = true;
                    refresh();
                }).build().setSlot(this, 15);
        sound = SoundRepository.getInstance().getSound(shop.getBuySoundKey());
        new STButton.STButtonBuilder(Material.NOTE_BLOCK)
                .setDisplayName("§6사운드 설정 §e§l[구매]")
                .setCleanable(false)
                .setLore("", "§e ▸ §f상점에서 아이템을 구매했을 때 출력 될 사운드를 설정합니다.", "§e ▸ §f클릭 시 변경됩니다.", "§e ▸ §f현재 사운드 : §b" + (sound == null ? "§7설정 값 없음" : sound.getTemplateName()))
                .setClickFunction((wrapper, container) -> {
                    String key = SoundRepository.getInstance().next(shop.getBuySoundKey());
                    shop.setBuySoundKey(key);
                    STSound temp = SoundRepository.getInstance().getSound(key);
                    if (temp != null) temp.playSound(wrapper.getPlayer());
                    changed = true;
                    refresh();
                }).build().setSlot(this, 16);
        sound = SoundRepository.getInstance().getSound(shop.getFailSoundKey());
        new STButton.STButtonBuilder(Material.NOTE_BLOCK)
                .setDisplayName("§6사운드 설정 §e§l[실패]")
                .setCleanable(false)
                .setLore("", "§e ▸ §f상점에서 아이템 구매에 실패했을 때 출력 될 사운드를 설정합니다.", "§e ▸ §f클릭 시 변경됩니다.", "§e ▸ §f현재 사운드 : §b" + (sound == null ? "§7설정 값 없음" : sound.getTemplateName()))
                .setClickFunction((wrapper, container) -> {
                    String key = SoundRepository.getInstance().next(shop.getFailSoundKey());
                    shop.setFailSoundKey(key);
                    STSound temp = SoundRepository.getInstance().getSound(key);
                    if (temp != null) temp.playSound(wrapper.getPlayer());
                    changed = true;
                    refresh();
                }).build().setSlot(this, 17);

    }
}

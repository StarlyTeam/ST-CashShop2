package net.starly.cashshop.shop.container.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.repo.sound.STSound;
import net.starly.cashshop.repo.sound.SoundRepository;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.container.button.STButton;
import net.starly.cashshop.shop.container.wrapper.InventoryClickEventWrapper;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.shop.settings.ItemSetting;
import net.starly.cashshop.shop.settings.PlayerHeadSetting;
import net.starly.cashshop.util.FormattingUtil;
import net.starly.cashshop.util.ItemStackNameUtil;
import net.starly.cashshop.util.Replacer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CashShopContainer extends STContainer {

    private static final CashShopMain plugin = CashShopMain.getPlugin();

    public CashShopContainer(String title, CashShopImpl shop) {
        super(shop.getLine() * 9, title, true, shop);
    }

    @Override
    protected void guiClick(InventoryClickEventWrapper event) {

    }

    @Override
    protected void guiClose(InventoryCloseEvent event) {
        STSound sound = SoundRepository.getInstance().getSound(shop.getCloseSoundKey());
        if (sound != null) sound.playSound((Player) event.getPlayer());
    }

    @Override
    protected void guiDrag(InventoryDragEvent event) {

    }

    @Override
    protected void openedInitializing() {
        headItemSetting();
    }

    @Override
    protected void initializingInventory(Inventory inventory) {
        CashShopMessageContextImpl context = CashShopMessageContextImpl.getInstance();
        GlobalShopSettings setting = GlobalShopSettings.getInstance();
        ItemSetting itemSetting = setting.getItemSetting();
        PlayerHeadSetting headSetting = setting.getHeadSetting();
        shop.forEachIndexed((index, item) -> {
            if (item == null) return;
            if (headSetting != null && index == headSetting.getSlot()) return;
            new STButton.STButtonBuilder(item.getSellingItem())
                    .setCleanable(false)
                    .setClickFunction((wrapper, container) -> {
                        Player player = wrapper.getPlayer();
                        if (item.getCost() < 0) {
                            // empty line
                        } else {
                            // TODO: 판매 로직
                            if (item.getNowAmount() <= 0 && item.getAmount() > 0) {
                                context.get(MessageContext.Type.ERROR, "notEnoughAmount").send(player);
                                STSound sound = SoundRepository.getInstance().getSound(shop.getFailSoundKey());
                                if (sound != null) sound.playSound(player);
                                return;
                            }
                            int buyAmount = 0;
                            if (wrapper.isShift()) {
                                switch (itemSetting.getShift().getType()) {
                                    case ALL:
                                        buyAmount = itemSetting.getShift().getAmount();
                                        break;
                                    case LEFT:
                                        buyAmount = wrapper.isShiftLeft() ? itemSetting.getShift().getAmount() : 0;
                                        break;
                                    case RIGHT:
                                        buyAmount = wrapper.isShiftRight() ? itemSetting.getShift().getAmount() : 0;
                                        break;
                                }
                            } else {
                                switch (itemSetting.getSingle().getType()) {
                                    case ALL:
                                        buyAmount = itemSetting.getSingle().getAmount();
                                        break;
                                    case LEFT:
                                        buyAmount = wrapper.isLeft() ? itemSetting.getSingle().getAmount() : 0;
                                        break;
                                    case RIGHT:
                                        buyAmount = wrapper.isRight() ? itemSetting.getSingle().getAmount() : 0;
                                        break;
                                }
                            }
                            if (buyAmount <= 0) return;
                            if (item.isLimited()) buyAmount = 1;
                            long cost = item.getCost() * buyAmount;
                            ItemStack stack = item.getOriginal().clone();
                            stack.setAmount(stack.getAmount() * buyAmount);
                            PlayerCash cash = plugin.getPlayerCashRepository().getPlayerCash(player.getUniqueId());
                            if (cash.getCash() < cost) {
                                long l = cost - cash.getCash();
                                context.get(MessageContext.Type.ERROR, "notEnoughCash", new Replacer.ReplacerBuilder().append(l, true).build().getFunction()).send(player);
                                STSound sound = SoundRepository.getInstance().getSound(shop.getFailSoundKey());
                                if (sound != null) sound.playSound(player);
                                return;
                            }
                            Map<Integer, ItemStack> map = player.getInventory().addItem(stack);
                            String message;
                            if (map.isEmpty()) {
                                if (buyAmount == 1) message = context.get(MessageContext.Type.DEFAULT, "buy").getText();
                                else message = context.get(MessageContext.Type.DEFAULT, "buyMany").getText();
                            } else {
                                for (ItemStack i : map.values()) buyAmount -= i.getAmount();
                                if (buyAmount <= 0) {
                                    context.get(MessageContext.Type.ERROR, "notEnoughInventorySlot").send(player);
                                    STSound sound = SoundRepository.getInstance().getSound(shop.getFailSoundKey());
                                    if (sound != null) sound.playSound(player);
                                    return;
                                } else message = context.get(MessageContext.Type.DEFAULT, "buySome").getText();
                            }

                            cost = buyAmount * item.getCost();
                            cash.subCash("상점/" + ChatColor.stripColor(shop.getName()) + "/" + ItemStackNameUtil.getItemName(stack) + " (x" + buyAmount + ")", PlayerCash.Type.SUB, cost).save(true);

                            message = message.replace("{item}", ItemStackNameUtil.getItemName(stack))
                                    .replace("{useCash}", FormattingUtil.formattingCash(cost))
                                    .replace("{cash}", FormattingUtil.formattingCash(cash.getCash()))
                                    .replace("{amount}", buyAmount + "");
                            player.sendMessage(message);
                            STSound sound = SoundRepository.getInstance().getSound(shop.getBuySoundKey());
                            if (sound != null) sound.playSound(player);
                            if (item.isLimited()) item.setNowAmount(item.getNowAmount() - buyAmount);
                            refresh();
                        }
                    }).build().setSlot(this, index);
        });
    }

}

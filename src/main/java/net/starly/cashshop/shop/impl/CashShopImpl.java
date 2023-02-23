package net.starly.cashshop.shop.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.item.STCashShopItem;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.util.ShopByteArrayUtility;
import net.starly.cashshop.executor.AsyncExecutors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@NoArgsConstructor
@AllArgsConstructor
public class CashShopImpl implements STCashShop {

    private String npc;
    private String name;
    private int line;
    private boolean close;
    @Getter@Setter private String soundKey;
    @Getter@Setter private String closeSoundKey;
    @Getter@Setter private String buySoundKey;
    @Getter@Setter private String failSoundKey;
    private STCashShopItem[] contents;

    @Override
    public boolean isChanged() {
        return Arrays.stream(contents).anyMatch(STCashShopItem::isChanged);
    }

    @Override
    public String getNpc() {
        return npc;
    }

    @Override
    public void setNpc(String npcName) {
        this.npc = npcName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public boolean isClosed() {
        return close;
    }

    @Override
    public void setClose(boolean close) {
        this.close = close;
        STContainer.closeShop(GlobalShopSettings.getInstance().getStarlyKey(this));
    }

    public void removeItem(int index) {
        contents[index] = null;
    }

    @Override
    public void registerContents(ItemStack[] items) {
        AtomicBoolean isChanged = new AtomicBoolean(false);
        forEachIndexed((index, shopItem)-> {
            ItemStack item = items[index];
            if(shopItem == null && (item == null || item.getType().equals(Material.AIR))) return;
            if(shopItem != null) {
                 if(shopItem.getOriginal().isSimilar(item)) {
                     if(shopItem.getOriginal().getAmount() != item.getAmount()) {
                         shopItem.getOriginal().setAmount(item.getAmount());
                         isChanged.set(true);
                     }
                     return;
                 }
            }
            if(item == null || item.getType().equals(Material.AIR)) contents[index] = null;
            else contents[index] = new STCashShopItem(item);
            isChanged.set(true);
        });
        if(isChanged.get()) save(true);
    }

    @Override
    public STCashShopItem[] getContents() {
        return contents;
    }

    @Override
    public CashShopImpl save(boolean async) {
        if(async) AsyncExecutors.run(this::save);
        else save();
        return this;
    }

    @Override public void forEach(Consumer<STCashShopItem> function) { Arrays.stream(contents).forEach(function); }
    @Override public void forEachIndexed(BiConsumer<Integer, STCashShopItem> function) {
        for(int i = 0; i < contents.length; i++) function.accept(i, contents[i]);
    }

    @SuppressWarnings("all")
    private void save() {
        File file = new File(CashShopMain.getPlugin().getDataFolder(), "shops/" + ChatColor.stripColor(name)+".bin");
        try {
            if (!file.exists()) file.createNewFile();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(ShopByteArrayUtility.toByteArray(this));
                stream.flush();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

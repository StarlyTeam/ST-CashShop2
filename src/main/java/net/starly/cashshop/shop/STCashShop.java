package net.starly.cashshop.shop;

import net.starly.cashshop.shop.item.STCashShopItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface STCashShop {

    String getNpc();
    void setNpc(String npcName);
    String getName();
    int getLine();
    void setSoundKey(String sound);
    String getSoundKey();
    void setCloseSoundKey(String sound);
    String getCloseSoundKey();
    void setBuySoundKey(String sound);
    String getBuySoundKey();
    void setFailSoundKey(String sound);
    String getFailSoundKey();
    boolean isClosed();
    boolean isChanged();
    void setClose(boolean close);
    void registerContents(ItemStack[] items);
    STCashShopItem[] getContents();
    STCashShop save(boolean async);
    void forEach(Consumer<STCashShopItem> function);
    void forEachIndexed(BiConsumer<Integer, STCashShopItem> function);

}

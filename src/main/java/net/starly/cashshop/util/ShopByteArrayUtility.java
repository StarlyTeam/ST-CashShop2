package net.starly.cashshop.util;

import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.content.STCashShopItem;
import net.starly.cashshop.shop.impl.CashShopImpl;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ShopByteArrayUtility {

    public static byte[] toByteArray(STCashShop shop) {
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BukkitObjectOutputStream boos = new BukkitObjectOutputStream(bos);
        ) {
            boos.writeUTF(shop.getNpc());
            boos.writeUTF(shop.getName());
            boos.writeInt(shop.getLine());
            boos.writeBoolean(shop.isClosed());
            boos.writeObject(shop.getSoundKey());
            boos.writeObject(shop.getCloseSoundKey());
            boos.writeObject(shop.getBuySoundKey());
            boos.writeObject(shop.getFailSoundKey());
            STCashShopItem[] shopItems = shop.getContents();
            boos.writeInt(shopItems.length);
            for (STCashShopItem shopItem : shopItems) {
                if (shopItem == null || shopItem.getOriginal() == null) boos.writeObject(null);
                else {
                    boos.writeObject(shopItem.getOriginal());
                    boos.writeDouble(shopItem.getOriginalSale());
                    boos.writeInt(shopItem.getAmount());
                    boos.writeInt(shopItem.getNowAmount());
                    boos.writeLong(shopItem.getOriginalCost());
                }
            }
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CashShopImpl fromByteArray(byte[] byteArray) {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            BukkitObjectInputStream bois = new BukkitObjectInputStream(bis);
        ) {
            String v1 = bois.readUTF();
            String v2 = bois.readUTF();
            int i1 = bois.readInt();
            boolean b1 = bois.readBoolean();
            Object o0 = bois.readObject();
            String v3;
            if(o0 == null) v3 = null;
            else v3 = (String) o0;
            o0 = bois.readObject();
            String v4;
            if(o0 == null) v4 = null;
            else v4 = (String) o0;
            o0 = bois.readObject();
            String v5;
            if(o0 == null) v5 = null;
            else v5 = (String) o0;
            o0 = bois.readObject();
            String v6;
            if(o0 == null) v6 = null;
            else v6 = (String) o0;
            int i2 = bois.readInt();
            STCashShopItem[] result = new STCashShopItem[i2];
            for(int i = 0; i < result.length; i++) {
                Object o = bois.readObject();
                if(o == null) continue;
                result[i] = new STCashShopItem(
                        (ItemStack) o,
                        bois.readDouble(),
                        bois.readInt(),
                        bois.readInt(),
                        bois.readLong(),
                        false
                );
            }
            return new CashShopImpl(v1, v2, i1, b1, v3, v4, v5, v6, result);
        } catch (Exception e) { return null; }
    }

}

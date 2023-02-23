package net.starly.cashshop.util;

import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.content.STCashShopItem;
import net.starly.cashshop.shop.impl.CashShopImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
            int i = 0;
            for (STCashShopItem shopItem : shopItems) {
                if (shopItem == null || shopItem.getOriginal() == null || shopItem.getOriginal().getType().equals(Material.AIR)) boos.writeObject(null);
                else {
                    boos.writeObject(shopItem.getOriginal());
                    boos.writeDouble(shopItem.getOriginalSale());
                    boos.writeInt(shopItem.getAmount());
                    boos.writeInt(shopItem.getNowAmount());
                    boos.writeLong(shopItem.getOriginalCost());
                }
            }
            boos.writeObject("EOF");
            return compressing(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] compressing(byte[] array) throws IOException {
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(array);
        dfl.finish();
        ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
        byte[] buf = new byte[1024];
        while(!dfl.finished()) {
            int count = dfl.deflate(buf);
            out.write(buf, 0, count);
        }
        out.close();
        return out.toByteArray();
    }

    private static byte[] decompressing(byte[] array) throws IOException, DataFormatException {
        Inflater ifl = new Inflater();
        ifl.setInput(array);
        ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
        byte[] buf = new byte[1024];
        while(!ifl.finished()) {
            int count = ifl.inflate(buf);
            out.write(buf, 0, count);
        }
        out.close();
        return out.toByteArray();
    }

    public static CashShopImpl fromByteArray(byte[] byteArray) {
        try(ByteArrayInputStream bis = new ByteArrayInputStream(decompressing(byteArray));
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
                ItemStack item = (ItemStack) o;
                if(item.getType().equals(Material.AIR)) continue;
                double sd1 = bois.readDouble();
                int si1 = bois.readInt();
                int si2 = bois.readInt();
                long sl1 = bois.readLong();
                result[i] = new STCashShopItem(item, sd1, si1, si2, sl1, false);
            }
            return new CashShopImpl(v1, v2, i1, b1, v3, v4, v5, v6, result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

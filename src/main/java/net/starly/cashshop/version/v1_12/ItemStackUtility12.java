package net.starly.cashshop.version.v1_12;

import com.google.gson.Gson;
import net.starly.cashshop.VersionController;
import net.starly.cashshop.version.ItemStackUtility;
import net.starly.cashshop.version.nms.wrapper.NmsItemStackWrapper;
import net.starly.cashshop.version.nms.wrapper.NmsNbtTagCompoundWrapper;
import net.starly.cashshop.version.nms.support.NmsItemStackSupport;
import net.starly.cashshop.version.nms.support.NmsNbtTagCompoundSupport;
import org.bukkit.inventory.ItemStack;

public class ItemStackUtility12 extends ItemStackUtility {

    private NmsItemStackSupport itemWrapper;
    private NmsNbtTagCompoundSupport nbtWrapper;

    @Override
    public NmsItemStackSupport getItemWrapper() throws ClassNotFoundException, NoSuchMethodException {
        if(itemWrapper == null)
            itemWrapper = new NmsItemStackSupport(VersionController.getInstance().getVersion());
        return itemWrapper;
    }

    /*@Override
    public NmsNbtTagCompoundWrapper getNptCompoundWrapper() throws ClassNotFoundException, NoSuchMethodException {
        //if(nbtWrapper == null)
            //nbtWrapper = new NmsNbtTagCompoundWrapper("net.minecraft.server.v1_12_R1.NBTTagCompound");
        return nbtWrapper;
    }*/

    @Override
    public <T> ItemStack addNbtTagCompound(T data, ItemStack item, Class<T> clazz) {
        try {
            NmsItemStackWrapper nmsItemStack = getItemWrapper().asNMSCopy(item);
            NmsNbtTagCompoundWrapper tag = nmsItemStack.getTag();
            if (tag == null) tag = nbtWrapper.newInstance();
            tag.setString(clazz.getSimpleName(), new Gson().toJson(data));
            nmsItemStack.setTag(tag);
            return getItemWrapper().asBukkitCopy(nmsItemStack);
        } catch (Exception ignored) { }
        return item;
    }

    public <T> void applyNbtTagCompound(T data, ItemStack item, Class<T> clazz) {
        try {
            NmsItemStackWrapper nmsItemStack = getItemWrapper().asNMSCopy(item);
            NmsNbtTagCompoundWrapper tag = nmsItemStack.getTag();
            if (tag == null) tag = nbtWrapper.newInstance();
            tag.setString(clazz.getSimpleName(), new Gson().toJson(data));
            nmsItemStack.setTag(tag);
            item.setItemMeta(getItemWrapper().asBukkitCopy(nmsItemStack).getItemMeta());
        } catch (Exception ignored) { }
    }

    @Override
    public <T> T getNbtTagCompound(Class<T> clazz, ItemStack item) {
        try {
            NmsItemStackWrapper nmsItem = getItemWrapper().asNMSCopy(item);
            NmsNbtTagCompoundWrapper tag = nmsItem.getTag();
            if (tag == null) return null;
            else {
                String data = tag.getString(clazz.getSimpleName());
                if (data == null) return null;
                return new Gson().fromJson(data, clazz);
            }
        } catch (Exception ignored) { }
        return null;
    }
}

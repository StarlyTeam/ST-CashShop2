package net.starly.cashshop.version;

import net.starly.cashshop.version.nms.wrapper.NmsItemStackWrapper;
import net.starly.cashshop.version.nms.wrapper.NmsNbtTagCompoundWrapper;
import org.bukkit.inventory.ItemStack;

public abstract class ItemStackUtility {

    public abstract NmsItemStackWrapper getItemWrapper() throws ClassNotFoundException, NoSuchMethodException;
    public abstract NmsNbtTagCompoundWrapper getNptCompoundWrapper() throws ClassNotFoundException, NoSuchMethodException;

    public abstract <T> ItemStack addNbtTagCompound(T data, ItemStack item, Class<T> clazz);
    public abstract <T> void applyNbtTagCompound(T data, ItemStack item, Class<T> clazz);
    public abstract <T> T getNbtTagCompound(Class<T> data, ItemStack item);



}

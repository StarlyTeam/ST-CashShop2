package net.starly.cashshop.nms.tank;

import lombok.Getter;
import net.starly.cashshop.util.VersionController;
import net.starly.cashshop.nms.wrapper.ItemStackWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NmsItemStackTank {

    private Method bukkitCopyMethod;
    private Method nmsCopyMethod;
    @Getter private Method setTagMethod;
    @Getter private Method getTagMethod;
    @Getter private NmsNbtTagCompoundTank nbtTagCompoundWrapper;
    private NmsItemTank nmsItemSupport;

    public NmsItemStackTank(VersionController.Version version) throws ClassNotFoundException, NoSuchMethodException {
        String craftItemStackClassName = "org.bukkit.craftbukkit." + version.getVersion() + ".inventory.CraftItemStack";
        String nmsItemStackClassName = "net.minecraft.server." + version.getVersion() + ".ItemStack";
        NmsNbtTagCompoundTank nbtTagCompoundWrapper = new NmsNbtTagCompoundTank("net.minecraft.server."+ version.getVersion() +".NBTTagCompound");

        Class<?> craftItemStack = Class.forName(craftItemStackClassName);
        Class<?> NMSItemStack;
        try { NMSItemStack = Class.forName(nmsItemStackClassName); }
        catch (Exception e) { NMSItemStack = Class.forName("net.minecraft.world.item.ItemStack"); }
        try { nmsItemSupport = new NmsItemTank("net.minecraft.server."+version.getVersion()+".Item", NMSItemStack); }
        catch (Exception e) { nmsItemSupport = new NmsItemTank("net.minecraft.world.item.Item", NMSItemStack); }
        bukkitCopyMethod = craftItemStack.getDeclaredMethod("asBukkitCopy", NMSItemStack);
        nmsCopyMethod = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        try {
            setTagMethod = NMSItemStack.getDeclaredMethod("setTag", nbtTagCompoundWrapper.getNBTTagCompound());
        } catch (Exception e) { setTagMethod = NMSItemStack.getDeclaredMethod("a", nbtTagCompoundWrapper.getNBTTagCompound()); }
        try { getTagMethod = NMSItemStack.getDeclaredMethod("getTag"); }
        catch (Exception e) { getTagMethod = NMSItemStack.getDeclaredMethod("u"); }
    }

    public ItemStack asBukkitCopy(ItemStackWrapper nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        return (ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack.getNmsItemStack());
    }

    public ItemStackWrapper asNMSCopy(ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        return new ItemStackWrapper(nmsCopyMethod.invoke(null, itemStack), nmsItemSupport,this);
    }

}

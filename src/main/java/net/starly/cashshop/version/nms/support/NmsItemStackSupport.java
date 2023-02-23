package net.starly.cashshop.version.nms.support;

import lombok.Getter;
import net.starly.cashshop.VersionController;
import net.starly.cashshop.version.nms.wrapper.NmsItemStackWrapper;
import net.starly.cashshop.version.nms.wrapper.NmsItemWrapper;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NmsItemStackSupport {

    private Method bukkitCopyMethod;
    private Method nmsCopyMethod;
    @Getter private Method setTagMethod;
    @Getter private Method getTagMethod;
    @Getter private NmsNbtTagCompoundSupport nbtTagCompoundWrapper;
    private NmsItemSupport nmsItemSupport;

    public NmsItemStackSupport(VersionController.Version version) throws ClassNotFoundException, NoSuchMethodException {
        String craftItemStackClassName = "org.bukkit.craftbukkit." + version.getVersion() + ".inventory.CraftItemStack";
        String nmsItemStackClassName = "net.minecraft.server." + version.getVersion() + ".ItemStack";
        NmsNbtTagCompoundSupport nbtTagCompoundWrapper = new NmsNbtTagCompoundSupport("net.minecraft.server."+ version.getVersion() +".NBTTagCompound");


        Class<?> craftItemStack = Class.forName(craftItemStackClassName);
        Class<?> NMSItemStack = Class.forName(nmsItemStackClassName);
        nmsItemSupport = new NmsItemSupport("net.minecraft.server."+version.getVersion()+".Item", NMSItemStack);
        bukkitCopyMethod = craftItemStack.getDeclaredMethod("asBukkitCopy", NMSItemStack);
        nmsCopyMethod = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        setTagMethod = NMSItemStack.getDeclaredMethod("setTag", nbtTagCompoundWrapper.getNBTTagCompound());
        getTagMethod = NMSItemStack.getDeclaredMethod("getTag");
    }

    public ItemStack asBukkitCopy(NmsItemStackWrapper nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        return (ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack.getNmsItemStack());
    }

    public NmsItemStackWrapper asNMSCopy(ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        return new NmsItemStackWrapper(nmsCopyMethod.invoke(null, itemStack), nmsItemSupport,this);
    }

}

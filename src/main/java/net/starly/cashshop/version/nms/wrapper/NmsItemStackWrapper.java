package net.starly.cashshop.version.nms.wrapper;

import lombok.Getter;
import net.starly.cashshop.version.nms.NmsItemStack;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NmsItemStackWrapper {

    private final Method bukkitCopyMethod;
    private final Method nmsCopyMethod;
    @Getter private Method setTagMethod;
    @Getter private Method getTagMethod;
    @Getter private final NmsNbtTagCompoundWrapper nbtTagCompoundWrapper;

    public NmsItemStackWrapper(
            String craftItemStackClassName,
            String nmsItemStackClassName,
            NmsNbtTagCompoundWrapper nbtTagCompoundWrapper
    ) throws ClassNotFoundException, NoSuchMethodException {
        this.nbtTagCompoundWrapper = nbtTagCompoundWrapper;

        Class<?> craftItemStack = Class.forName(craftItemStackClassName);
        Class<?> NMSItemStack = Class.forName(nmsItemStackClassName);
        bukkitCopyMethod = craftItemStack.getDeclaredMethod("asBukkitCopy", NMSItemStack);
        nmsCopyMethod = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        setTagMethod = NMSItemStack.getDeclaredMethod("setTag", nbtTagCompoundWrapper.getNBTTagCompound());
        getTagMethod = NMSItemStack.getDeclaredMethod("getTag");
    }

    public ItemStack asBukkitCopy(NmsItemStack nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        return (ItemStack) bukkitCopyMethod.invoke(null, nmsItemStack.getNmsItemStack());
    }

    public NmsItemStack asNMSCopy(ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        return new NmsItemStack(nmsCopyMethod.invoke(null, itemStack), this);
    }

}

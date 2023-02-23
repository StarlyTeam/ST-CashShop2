package net.starly.cashshop.nms.tank;

import lombok.Getter;
import net.starly.cashshop.nms.wrapper.NBTTagCompoundWrapper;

import java.lang.reflect.Method;

public class NmsNbtTagCompoundTank {

    @Getter private Class<?> NBTTagCompound;
    @Getter private Method getStringMethod;
    @Getter private Method setStringMethod;

    NmsNbtTagCompoundTank(
            String nbtTagCompoundClassName
    ) throws ClassNotFoundException, NoSuchMethodException {
        try {
            NBTTagCompound = Class.forName(nbtTagCompoundClassName); } catch (Exception ignored) {
            NBTTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
        }
        try { getStringMethod = NBTTagCompound.getDeclaredMethod("getString", String.class); }
        catch (Exception e) {getStringMethod = NBTTagCompound.getDeclaredMethod("l", String.class); }
        try { setStringMethod = NBTTagCompound.getDeclaredMethod("setString", String.class, String.class); }
        catch (Exception e) { setStringMethod = NBTTagCompound.getDeclaredMethod("a", String.class, String.class); }
    }

    public NBTTagCompoundWrapper newInstance() throws InstantiationException, IllegalAccessException {
        return new NBTTagCompoundWrapper(NBTTagCompound.newInstance(), this);
    }

}

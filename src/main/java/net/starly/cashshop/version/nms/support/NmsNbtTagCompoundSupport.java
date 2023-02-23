package net.starly.cashshop.version.nms.support;

import lombok.Getter;
import net.starly.cashshop.version.nms.wrapper.NmsNbtTagCompoundWrapper;

import java.lang.reflect.Method;

public class NmsNbtTagCompoundSupport {

    @Getter private Class<?> NBTTagCompound;
    @Getter private Method getStringMethod;
    @Getter private Method setStringMethod;

    NmsNbtTagCompoundSupport(
            String nbtTagCompoundClassName
    ) throws ClassNotFoundException, NoSuchMethodException {
        try {
            NBTTagCompound = Class.forName(nbtTagCompoundClassName); } catch (Exception ignored) {
            NBTTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
        }
        getStringMethod = NBTTagCompound.getDeclaredMethod("getString", String.class);
        setStringMethod = NBTTagCompound.getDeclaredMethod("setString", String.class, String.class);
    }

    public NmsNbtTagCompoundWrapper newInstance() throws InstantiationException, IllegalAccessException {
        return new NmsNbtTagCompoundWrapper(NBTTagCompound.newInstance(), this);
    }

}

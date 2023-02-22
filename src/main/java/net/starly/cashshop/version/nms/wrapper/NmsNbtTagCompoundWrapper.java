package net.starly.cashshop.version.nms.wrapper;

import lombok.Getter;
import net.starly.cashshop.version.nms.NmsNbtTagCompound;

import java.lang.reflect.Method;

public class NmsNbtTagCompoundWrapper {

    @Getter private Class<?> NBTTagCompound;
    @Getter private Method getStringMethod;
    @Getter private Method setStringMethod;

    public NmsNbtTagCompoundWrapper(
            String nbtTagCompoundClassName
    ) throws ClassNotFoundException, NoSuchMethodException {
        NBTTagCompound = Class.forName(nbtTagCompoundClassName);
        getStringMethod = NBTTagCompound.getDeclaredMethod("getString", String.class);
        setStringMethod = NBTTagCompound.getDeclaredMethod("setString", String.class, String.class);
    }

    public NmsNbtTagCompound newInstance() throws InstantiationException, IllegalAccessException {
        return new NmsNbtTagCompound(NBTTagCompound.newInstance(), this);
    }

}

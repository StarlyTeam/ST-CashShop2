package net.starly.cashshop.nms.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.nms.tank.NmsNbtTagCompoundTank;

import java.lang.reflect.InvocationTargetException;

@Data
@AllArgsConstructor
public class NBTTagCompoundWrapper {

    private Object nbtTagCompound;
    private NmsNbtTagCompoundTank wrapper;

    public String getString(String key) throws InvocationTargetException, IllegalAccessException {
        Object result = wrapper.getGetStringMethod().invoke(nbtTagCompound, key);
        if(result == null) return null;
        else return (String) result;
    }

    public void setString(String key, String value) throws InvocationTargetException, IllegalAccessException {
        wrapper.getSetStringMethod().invoke(nbtTagCompound, key, value);
    }

}

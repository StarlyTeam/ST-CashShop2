package net.starly.cashshop.version.nms;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.version.nms.wrapper.NmsNbtTagCompoundWrapper;

import java.lang.reflect.InvocationTargetException;

@Data
@AllArgsConstructor
public class NmsNbtTagCompound {

    private Object nbtTagCompound;
    private NmsNbtTagCompoundWrapper wrapper;

    public String getString(String key) throws InvocationTargetException, IllegalAccessException {
        Object result = wrapper.getGetStringMethod().invoke(nbtTagCompound, key);
        if(result == null) return null;
        else return (String) result;
    }

    public void setString(String key, String value) throws InvocationTargetException, IllegalAccessException {
        wrapper.getSetStringMethod().invoke(nbtTagCompound, key, value);
    }

}

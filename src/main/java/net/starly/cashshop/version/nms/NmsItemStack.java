package net.starly.cashshop.version.nms;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.version.nms.wrapper.NmsItemStackWrapper;

import java.lang.reflect.InvocationTargetException;

@Data
@AllArgsConstructor
public class NmsItemStack {

    private Object nmsItemStack;
    private NmsItemStackWrapper wrapper;

    public NmsNbtTagCompound getTag() throws InvocationTargetException, IllegalAccessException {
        Object obj = wrapper.getGetTagMethod().invoke(nmsItemStack);
        if(obj == null) return null;
        return new NmsNbtTagCompound(obj, wrapper.getNbtTagCompoundWrapper());
    }

    public void setTag(NmsNbtTagCompound tag) throws InvocationTargetException, IllegalAccessException {
        wrapper.getSetTagMethod().invoke(nmsItemStack, tag.getNbtTagCompound());
    }

}

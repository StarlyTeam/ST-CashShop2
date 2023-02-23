package net.starly.cashshop.version.nms.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.version.nms.support.NmsItemStackSupport;
import net.starly.cashshop.version.nms.support.NmsItemSupport;

import java.lang.reflect.InvocationTargetException;

@Data
@AllArgsConstructor
public class NmsItemStackWrapper {

    private Object nmsItemStack;
    private NmsItemSupport itemSupport;
    private NmsItemStackSupport wrapper;

    public NmsNbtTagCompoundWrapper getTag() throws InvocationTargetException, IllegalAccessException {
        Object obj = wrapper.getGetTagMethod().invoke(nmsItemStack);
        if(obj == null) return null;
        return new NmsNbtTagCompoundWrapper(obj, wrapper.getNbtTagCompoundWrapper());
    }

    public void setTag(NmsNbtTagCompoundWrapper tag) throws InvocationTargetException, IllegalAccessException {
        wrapper.getSetTagMethod().invoke(nmsItemStack, tag.getNbtTagCompound());
    }

    public NmsItemWrapper getItem() {
        return new NmsItemWrapper(itemSupport, this);
    }

}

package net.starly.cashshop.nms.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.nms.tank.NmsItemStackTank;
import net.starly.cashshop.nms.tank.NmsItemTank;

import java.lang.reflect.InvocationTargetException;

@Data
@AllArgsConstructor
public class ItemStackWrapper {

    private Object nmsItemStack;
    private NmsItemTank itemSupport;
    private NmsItemStackTank wrapper;

    public NBTTagCompoundWrapper getTag() throws InvocationTargetException, IllegalAccessException {
        Object obj = wrapper.getGetTagMethod().invoke(nmsItemStack);
        if(obj == null) return null;
        return new NBTTagCompoundWrapper(obj, wrapper.getNbtTagCompoundWrapper());
    }

    public void setTag(NBTTagCompoundWrapper tag) throws InvocationTargetException, IllegalAccessException {
        wrapper.getSetTagMethod().invoke(nmsItemStack, tag.getNbtTagCompound());
    }

    public NmsItemWrapper getItem() {
        return new NmsItemWrapper(itemSupport, this);
    }

}

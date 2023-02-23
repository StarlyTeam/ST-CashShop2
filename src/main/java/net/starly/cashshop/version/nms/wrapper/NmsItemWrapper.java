package net.starly.cashshop.version.nms.wrapper;

import lombok.Getter;
import net.starly.cashshop.version.nms.support.NmsItemSupport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NmsItemWrapper {

    private NmsItemSupport support;
    @Getter private Object Item;

    public NmsItemWrapper(NmsItemSupport itemSupport, NmsItemStackWrapper nmsItemStackWrapper) {
        try {
            support = itemSupport;
            Method getItemMethod = itemSupport.getNmsItemStackClass().getMethod("getItem");
            Item = getItemMethod.invoke(nmsItemStackWrapper.getNmsItemStack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUnlocalizedName(NmsItemStackWrapper nmsItemStack) {
        try {
            return (String) support.getJMethod().invoke(Item, nmsItemStack.getNmsItemStack());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}

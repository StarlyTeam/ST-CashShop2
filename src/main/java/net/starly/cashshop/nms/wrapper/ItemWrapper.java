package net.starly.cashshop.nms.wrapper;

import lombok.Getter;
import net.starly.cashshop.nms.tank.NmsItemTank;

import java.lang.reflect.Method;

public class ItemWrapper {

    private NmsItemTank support;
    @Getter private Object Item;

    public ItemWrapper(NmsItemTank itemSupport, ItemStackWrapper nmsItemStackWrapper) {
        try {
            support = itemSupport;
            Method getItemMethod;
            try {
                getItemMethod = itemSupport.getNmsItemStackClass().getMethod("getItem");
            } catch (Exception e) { getItemMethod = itemSupport.getNmsItemStackClass().getMethod("c"); }
            Item = getItemMethod.invoke(nmsItemStackWrapper.getNmsItemStack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUnlocalizedName(ItemStackWrapper nmsItemStack) {
        try {
            return (String) support.getJMethod().invoke(Item, nmsItemStack.getNmsItemStack());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}

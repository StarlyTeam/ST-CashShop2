package net.starly.cashshop.version.nms.support;

import lombok.Getter;

import java.lang.reflect.Method;

public class NmsItemSupport {

    @Getter private final Class<?> NmsItemClass;
    @Getter private final Class<?> NmsItemStackClass;
    @Getter private Method jMethod;

    NmsItemSupport(String className, Class<?> nmsItemStackClass) throws ClassNotFoundException, NoSuchMethodException {
        NmsItemClass = Class.forName(className);
        NmsItemStackClass = nmsItemStackClass;
        jMethod = NmsItemClass.getMethod("j", nmsItemStackClass);
    }

}

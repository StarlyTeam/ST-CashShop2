package net.starly.cashshop.message.impl;

import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.STMessage;
import net.starly.cashshop.util.Pair;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CashShopMessageContextImpl implements MessageContext {

    private static CashShopMessageContextImpl instance;

    public static CashShopMessageContextImpl getInstance() {
        if (instance == null) instance = new CashShopMessageContextImpl();
        return instance;
    }

    private final Map<Pair<Type, String>, String> map = new HashMap<>();

    private CashShopMessageContextImpl() {
    }

    @Override
    public STMessage get(Type type, String key, String def) {
        return new STMessage(map.getOrDefault(new Pair<>(Type.DEFAULT, "prefix"), ""), map.getOrDefault(new Pair<>(type, key), def));
    }

    @Override
    public STMessage get(Type type, String key) {
        return get(type, key, "");
    }

    @Override
    public String getOnlyString(Type type, String key) {
        return map.getOrDefault(new Pair<>(Type.DEFAULT, key), "");
    }

    @Override
    public STMessage get(Type type, String key, String def, Function<String, String> replacer) {
        return new STMessage(map.getOrDefault(new Pair<>(Type.DEFAULT, "prefix"), ""), replacer.apply(get(type, key, def).getMessage()));
    }

    @Override
    public STMessage get(Type type, String key, Function<String, String> replacer) {
        return get(type, key, "", replacer);
    }

    @Override
    public void set(Type type, String key, String value) {
        map.put(new Pair<>(type, key), ChatColor.translateAlternateColorCodes('&', value));
    }

    @Override
    public void reset() {
        map.clear();
    }

}

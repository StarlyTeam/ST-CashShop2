package net.starly.cashshop.message;

import java.util.function.Function;

public interface MessageContext {

    STMessage get(Type type, String key, String def);

    STMessage get(Type type, String key);

    String getOnlyString(Type type, String key);

    STMessage get(Type type, String key, String def, Function<String, String> replacer);

    STMessage get(Type type, String key, Function<String, String> replacer);

    void set(Type type, String key, String value);

    void reset();

    enum Type {
        ERROR,
        DEFAULT
    }

}

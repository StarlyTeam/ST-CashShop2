package net.starly.cashshop.message;

import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.core.data.Config;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class MessageLoader {

    private static boolean loaded = false;

    public static void load(Config config) {
        if(loaded) {
            CashShopMessageContextImpl.getInstance().reset();
            CashMessageContextImpl.getInstance().reset();
            loaded = false;
        }

        ConfigurationSection messagesSection = Objects.requireNonNull(config.getConfigurationSection("messages"));
        ConfigurationSection errorMessagesSection = Objects.requireNonNull(config.getConfigurationSection("errorMessages"));

        loadMessageSection(messagesSection.getConfigurationSection("cash"), MessageContext.Type.DEFAULT, false);
        loadMessageSection(messagesSection.getConfigurationSection("cashshop"), MessageContext.Type.DEFAULT, true);
        loadMessageSection(errorMessagesSection.getConfigurationSection("cash"), MessageContext.Type.ERROR, false);
        loadMessageSection(errorMessagesSection.getConfigurationSection("cashshop"), MessageContext.Type.ERROR, true);

        loaded = true;
    }

    private static void loadMessageSection(ConfigurationSection section, MessageContext.Type type, boolean shop) {
        if(section == null) return;
        MessageContext context;
        if(shop) context = CashShopMessageContextImpl.getInstance();
        else context = CashMessageContextImpl.getInstance();
        section.getKeys(false).forEach((key)-> context.set(type, key, section.getString(key)));
    }


}

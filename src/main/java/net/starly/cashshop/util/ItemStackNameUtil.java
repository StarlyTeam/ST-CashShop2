package net.starly.cashshop.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.VersionController;
import net.starly.cashshop.version.nms.wrapper.NmsItemStackWrapper;
import net.starly.cashshop.version.nms.support.NmsItemStackSupport;
import net.starly.cashshop.version.nms.wrapper.NmsItemWrapper;
import org.apache.commons.io.IOUtils;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class ItemStackNameUtil {

    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Map<String, String> languageMap = new HashMap<>();

    public static void initializingLocale(Server server) {
        try(InputStream var1 = CashShopMain.getPlugin().getResource("ko_kr_12.lang")) {
            for (String var3 : IOUtils.readLines(var1, StandardCharsets.UTF_8)) {
                if (!var3.isEmpty() && var3.charAt(0) != '#') {
                    String[] var4 = Iterables.toArray(splitter.split(var3), String.class);
                    if (var4 != null && var4.length == 2) {
                        String var5 = var4[0];
                        String var6 = pattern.matcher(var4[1]).replaceAll("%$1s");
                        languageMap.put(var5, var6);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static String getItemName(ItemStack itemStack) {
        if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            return itemStack.getItemMeta().getDisplayName();

        try {
            NmsItemStackSupport nmsItem = new NmsItemStackSupport(VersionController.getInstance().getVersion());
            NmsItemStackWrapper nmsItemStack = nmsItem.asNMSCopy(itemStack);
            NmsItemWrapper item = nmsItemStack.getItem();
            String unlocalizedName = item.getUnlocalizedName(nmsItemStack);
            if (languageMap.containsKey(unlocalizedName + ".name"))
                return languageMap.get(unlocalizedName + ".name");
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return itemStack.getType().getData().getName();
    }

}

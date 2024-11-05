package net.starly.cashshop.repo.shop.impl;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.repo.shop.CashShopRepository;
import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.container.STContainer;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.shop.item.STCashShopItem;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.util.ShopByteArrayUtility;
import net.starly.core.data.Config;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class CashShopRepositoryImpl implements CashShopRepository {

    public static final File shopFolder = new File(CashShopMain.getPlugin().getDataFolder(), "shops");
    private final Map<String, CashShopImpl> cashShopMap = new HashMap<>();

    static {
        if (!shopFolder.exists()) shopFolder.mkdirs();
    }

    @Override
    public void initializing(Config config) {
        for (File file : Arrays.stream(Objects.requireNonNull(shopFolder.listFiles())).filter(it -> it.getName().endsWith(".bin")).collect(Collectors.toList())) {
            String shopName = file.getName();
            shopName = shopName.substring(0, shopName.length() - 4);
            try (FileInputStream stream = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                stream.read(data);
                cashShopMap.put(shopName, ShopByteArrayUtility.fromByteArray(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> getShopNames() {
        return cashShopMap.keySet().stream().map(it -> it.replace(" ", "_")).collect(Collectors.toList());
    }

    public List<STCashShop> getShops() {
        return new ArrayList<>(cashShopMap.values());
    }

    @Override
    public CashShopImpl getShop(String shopName) {
        shopName = ChatColor.stripColor(shopName).replace("_", " ");
        if (cashShopMap.containsKey(shopName)) return cashShopMap.get(shopName);
        return null;
    }

    @Override
    public CashShopImpl getShopAtNpcName(String npcName) {
        return cashShopMap
                .entrySet()
                .stream()
                .filter((it) -> it.getValue().getNpc().equals(npcName))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    @Override
    public boolean registerShop(String shopName, int line) {
        shopName = ChatColor.translateAlternateColorCodes('&', shopName.replace("_", " "));
        if (cashShopMap.containsKey(ChatColor.stripColor(shopName))) return false;
        cashShopMap.put(ChatColor.stripColor(shopName), new CashShopImpl("", shopName, line, true, null, null, null, null, new STCashShopItem[line * 9]).save(true));
        return true;
    }

    @Override
    public void unregisterShop(CashShopImpl shop) {
        STContainer.closeShop(GlobalShopSettings.getInstance().getStarlyKey(shop));
        String stripName = ChatColor.stripColor(shop.getName());
        cashShopMap.remove(stripName);
        File file = new File(shopFolder, stripName + ".bin");
        if (file.exists()) file.delete();
    }

}

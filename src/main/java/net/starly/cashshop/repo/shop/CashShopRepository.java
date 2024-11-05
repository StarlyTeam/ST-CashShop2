package net.starly.cashshop.repo.shop;

import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.core.data.Config;

import java.util.List;

public interface CashShopRepository {

    void initializing(Config config);

    List<String> getShopNames();

    List<STCashShop> getShops();

    CashShopImpl getShop(String shopName);

    CashShopImpl getShopAtNpcName(String npcName);

    boolean registerShop(String shopName, int line);

    void unregisterShop(CashShopImpl shop);

}

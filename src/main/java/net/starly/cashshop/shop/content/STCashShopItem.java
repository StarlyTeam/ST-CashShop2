package net.starly.cashshop.shop.content;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.shop.settings.ItemSetting;
import net.starly.cashshop.util.FormattingUtil;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

@AllArgsConstructor
@RequiredArgsConstructor
public class STCashShopItem {

    @Getter private final ItemStack original;
    private double sale = .0;
    @Getter private int amount = -1;
    @Getter private int nowAmount = -1;
    public void setNowAmount(int nowAmount) {
        this.nowAmount = nowAmount;
        isChanged = true;
    }
    @Getter private long originalCost = -1;

    @Getter@Setter private boolean isChanged = false;

    public void setOriginalCost(long cost) {
        if(cost < 0) cost = -1;
        if(cost == originalCost) return;
        isChanged = true;
        originalCost = cost;
    }

    public ItemStack getSettingItem() {
        ItemStack result = original.clone();
        ItemMeta meta = result.getItemMeta();
        meta.setLore(Arrays.asList("",
                "§8-------------------------------------",
                "§e ▸ §f가격 : §a" + FormattingUtil.formattingCash(originalCost),
                "§e ▸ §f할인 : §a" + FormattingUtil.formattingDouble(getSale()),
                "§e ▸ §f재고 : §a" + (amount <= -1 ? "제한 없음" : amount),
                "§e ▸ §f남은 재고 : §a" + (amount <= -1 ? "제한 없음" : nowAmount),
                "§8-------------------------------------",
                "§e ▸ §f가격 설정 : §a우클릭",
                "§e ▸ §f할인 설정 : §a좌클릭",
                "§e ▸ §f재고 설정 : §a휠클릭",
                "§e ▸ §c삭제 : §a쉬프트+클릭",
                "§e ▸ §f재고 채우기 : §a키보드 Q",
                "§8-------------------------------------"
        ));
        result.setItemMeta(meta);
        return result;
    }

    public ItemStack getSellingItem() {
        ItemSetting setting = GlobalShopSettings.getInstance().getItemSetting();
        ItemStack result = original.clone();
        ItemMeta meta = result.getItemMeta();
        if(setting.isHideNoneValueItemInfo()) {
            if(originalCost < 0) {
                meta.setDisplayName("§f");
                meta.setLore(null);
                result.setItemMeta(meta);
                return result;
            }
        }
        if(setting.isHideAttribute()) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if(setting.isHideUnbreakable()) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if(setting.isHideEnchants()) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(setting.convertLore(this));
        result.setItemMeta(meta);
        return result;
    }

    public boolean isSale() { return sale > 0; }
    public boolean isLimited() { return amount >= 0; }
    public boolean setAmount(int amount) {
        if(amount < 0) amount = -1;
        if(this.amount == amount) return false;
        nowAmount = amount;
        this.amount = amount;
        isChanged = true;
        return true;
    }

    public boolean setSale(double sale) {
        if(sale < 0 || sale > 100) return false;
        if(sale == getSale()) return false;
        this.sale = sale / 100.0;
        isChanged = true;
        return true;
    }

    public double getSale() {
        return sale * 100.0;
    }

    public double getOriginalSale() {
        return sale;
    }

    public long getCost() {
        long result = originalCost;
        if(result < 0) return result;
        if(sale > 0) {
            if(sale >= 0.9999) return 0;
            BigDecimal cost = new BigDecimal(originalCost);
            BigDecimal discount = cost.multiply(new BigDecimal(sale));
            BigDecimal res = cost.subtract(discount).setScale(0, RoundingMode.HALF_UP);
            result = res.longValue();
        }
        return result;
    }


}

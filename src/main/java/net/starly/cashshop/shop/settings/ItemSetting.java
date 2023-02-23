package net.starly.cashshop.shop.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.shop.content.STCashShopItem;
import net.starly.cashshop.util.FormattingUtil;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ItemSetting {

    private boolean hideAttribute;
    private boolean hideUnbreakable;
    private boolean hideEnchants;
    private boolean hideNoneValueItemInfo;

    private ClickEvent single;
    private ClickEvent shift;

    private List<String> defaultLore;
    private List<String> saleLore;
    private List<String> limitedLore;
    private List<String> limitedSaleLore;

    public List<String> convertLore(STCashShopItem cashShopItem) {
        boolean isSale = cashShopItem.getSale() > .0;
        boolean isLimited = cashShopItem.getAmount() >= 0;
        List<String> target;
        if(!isSale && !isLimited) target = defaultLore;
        else if(isSale && isLimited) target = limitedSaleLore;
        else if(isSale) target = saleLore;
        else target = limitedLore;

        return target
                .stream()
                .map((it)->it
                        .replace("{cash}", FormattingUtil.formattingCash(cashShopItem.getCost()))
                        .replace("{original}", FormattingUtil.formattingCash(cashShopItem.getOriginalCost()))
                        .replace("{click-type}", single.text)
                        .replace("{click-amount}", single.amount + "")
                        .replace("{shift-click-type}", shift.text)
                        .replace("{shift-click-amount}", shift.amount + "")
                        .replace("{amount}", FormattingUtil.formattingInteger(cashShopItem.getNowAmount()))
                        .replace("{max-amount}", FormattingUtil.formattingInteger(cashShopItem.getAmount()))
                        .replace("{sale}", FormattingUtil.formattingDouble(cashShopItem.getSale()))
                ).collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    public static class ClickEvent {

        private Type type;
        private int amount;
        private String text;

        public enum Type { LEFT, RIGHT, ALL }
    }

}

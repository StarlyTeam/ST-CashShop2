package net.starly.cashshop.util;

import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;

import java.text.DecimalFormat;

public class FormattingUtil {

    private static DecimalFormat cashFormat = new DecimalFormat("#,##0");
    private static DecimalFormat doubleFormat = new DecimalFormat("#,###.###");

    public static String formattingCash(long cash) {
        return cashFormat.format(cash) + CashMessageContextImpl.getInstance().getOnlyString(MessageContext.Type.DEFAULT, "suffix");
    }

    public static String formattingLong(long value) {
        return cashFormat.format(value);
    }

    public static String formattingInteger(int integer) {
        return cashFormat.format(integer);
    }

    public static String formattingDouble(double value) {
        return doubleFormat.format(value);
    }

}

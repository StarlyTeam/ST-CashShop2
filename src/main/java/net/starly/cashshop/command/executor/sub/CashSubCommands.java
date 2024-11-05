package net.starly.cashshop.command.executor.sub;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import net.starly.cashshop.command.STSubCommand;
import net.starly.cashshop.executor.AsyncExecutors;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.util.Replacer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CashSubCommands {

    private static final STSubCommand moneySub = new STSubCommand("<캐시>", "<amount>", "player");
    private static final STSubCommand playerSubNoMoney = new STSubCommand("<닉네임>", "<player>", "player");
    private static final STSubCommand playerSubMoney = new STSubCommand("<닉네임>", "<player>", "player", moneySub, (a, b) -> {
    });
    private static final CashMessageContextImpl context = CashMessageContextImpl.getInstance();
    public static final STSubCommand INFO = new STSubCommand("확인", "info", "해당 유저의 캐시를 확인합니다.", playerSubNoMoney, (sender, args) -> {
        CashShopMain plugin = CashShopMain.getPlugin();
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            AsyncExecutors.run(() -> {
                OfflinePlayer off = plugin.getServer().getOfflinePlayer(args[0]);
                if (!off.hasPlayedBefore())
                    context.get(MessageContext.Type.ERROR, "playerNotFound");
                else
                    context.get(MessageContext.Type.DEFAULT, "info", new Replacer.ReplacerBuilder().append(off).append(plugin.getPlayerCashRepository().getPlayerCash(off.getUniqueId()).getCash(), false).build().getFunction()).send(sender);
            });
        } else
            context.get(MessageContext.Type.DEFAULT, "info", new Replacer.ReplacerBuilder().append(target).append(plugin.getPlayerCashRepository().getPlayerCash(target.getUniqueId()).getCash(), false).build().getFunction()).send(sender);
    });
    public static final STSubCommand RESET = new STSubCommand("초기화", "reset", "해당 유저의 캐시를 초기화합니다.", playerSubNoMoney, (sender, args) -> {

    });
    public static final STSubCommand ADD = new STSubCommand("지급", "add", "해당 유저에게 캐시를 지급합니다.", playerSubMoney, (sender, args) -> {
        CashShopMain plugin = CashShopMain.getPlugin();
        String source = sender instanceof ConsoleCommandSender ? "콘솔" : sender.getName();
        long value;
        try {
            value = Long.parseLong(args[1]);
        } catch (Exception e) {
            context.get(MessageContext.Type.ERROR, "notNumber").send(sender);
            return;
        }
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            AsyncExecutors.run(() -> {
                OfflinePlayer off = plugin.getServer().getOfflinePlayer(args[0]);
                if (!off.hasPlayedBefore())
                    context.get(MessageContext.Type.ERROR, "playerNotFound").send(sender);
                else {
                    plugin.getPlayerCashRepository().getPlayerCash(off.getUniqueId()).addCash(source, PlayerCash.Type.ADD, value).save(true);
                    context.get(MessageContext.Type.DEFAULT, "addCash", new Replacer.ReplacerBuilder().append(off).append(value, false).build().getFunction()).send(sender);
                }
            });
        } else {
            plugin.getPlayerCashRepository().getPlayerCash(target.getUniqueId()).addCash(source, PlayerCash.Type.ADD, value).save(true).getCash();
            context.get(MessageContext.Type.DEFAULT, "addCash", new Replacer.ReplacerBuilder().append(target).append(value, false).build().getFunction()).send(sender);
            context.get(MessageContext.Type.DEFAULT, "addCashToPlayer", new Replacer.ReplacerBuilder().append(sender).append(value, false).build().getFunction()).send(target);
        }
    });
    public static final STSubCommand SUBTRACT = new STSubCommand("차감", "sub", "해당 유저의 캐시를 차감합니다.", playerSubMoney, (sender, args) -> {
        CashShopMain plugin = CashShopMain.getPlugin();
        String source = sender instanceof ConsoleCommandSender ? "콘솔" : sender.getName();
        long value;
        try {
            value = Long.parseLong(args[1]);
        } catch (Exception e) {
            context.get(MessageContext.Type.ERROR, "notNumber").send(sender);
            return;
        }
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            AsyncExecutors.run(() -> {
                OfflinePlayer off = plugin.getServer().getOfflinePlayer(args[0]);
                if (!off.hasPlayedBefore())
                    context.get(MessageContext.Type.ERROR, "playerNotFound").send(sender);
                else {
                    plugin.getPlayerCashRepository().getPlayerCash(off.getUniqueId()).subCash(source, PlayerCash.Type.ADD, value).save(true);
                    context.get(MessageContext.Type.DEFAULT, "removeCash", new Replacer.ReplacerBuilder().append(off).append(value, false).build().getFunction()).send(sender);
                }
            });
        } else {
            plugin.getPlayerCashRepository().getPlayerCash(target.getUniqueId()).subCash(source, PlayerCash.Type.ADD, value).save(true).getCash();
            context.get(MessageContext.Type.DEFAULT, "removeCash", new Replacer.ReplacerBuilder().append(target).append(value, false).build().getFunction()).send(sender);
            context.get(MessageContext.Type.DEFAULT, "removeCashToPlayer", new Replacer.ReplacerBuilder().append(sender).append(value, false).build().getFunction()).send(target);
        }
    });
    public static final STSubCommand SET = new STSubCommand("설정", "set", "해당 유저의 캐시를 설정합니다.", playerSubMoney, (sender, args) -> {
        CashShopMain plugin = CashShopMain.getPlugin();
        String source = sender instanceof ConsoleCommandSender ? "콘솔" : sender.getName();
        long value;
        try {
            value = Long.parseLong(args[1]);
        } catch (Exception e) {
            context.get(MessageContext.Type.ERROR, "notNumber").send(sender);
            return;
        }
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            AsyncExecutors.run(() -> {
                OfflinePlayer off = plugin.getServer().getOfflinePlayer(args[0]);
                if (!off.hasPlayedBefore())
                    context.get(MessageContext.Type.ERROR, "playerNotFound").send(sender);
                else {
                    plugin.getPlayerCashRepository().getPlayerCash(off.getUniqueId()).setCash(source, PlayerCash.Type.ADD, value).save(true);
                    context.get(MessageContext.Type.DEFAULT, "setCash", new Replacer.ReplacerBuilder().append(off).append(value, false).build().getFunction()).send(sender);
                }
            });
        } else {
            plugin.getPlayerCashRepository().getPlayerCash(target.getUniqueId()).setCash(source, PlayerCash.Type.ADD, value).save(true).getCash();
            context.get(MessageContext.Type.DEFAULT, "setCash", new Replacer.ReplacerBuilder().append(target).append(value, false).build().getFunction()).send(sender);
            context.get(MessageContext.Type.DEFAULT, "setCashToPlayer", new Replacer.ReplacerBuilder().append(sender).append(value, false).build().getFunction()).send(target);
        }
    });

}

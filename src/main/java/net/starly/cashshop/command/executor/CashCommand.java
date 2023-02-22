package net.starly.cashshop.command.executor;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.command.STCashCommand;
import net.starly.cashshop.command.executor.sub.CashSubCommands;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.util.Replacer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CashCommand extends STCashCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 0 && sender instanceof Player)
            CashMessageContextImpl.getInstance()
                    .get(
                            MessageContext.Type.DEFAULT, "myInfo",
                            new Replacer.ReplacerBuilder()
                                    .append(sender)
                                    .append(CashShopMain.getPlugin().getPlayerCashRepository().getPlayerCash(((Player) sender).getUniqueId()).getCash(), false)
                                    .build()
                                    .getFunction()
                    ).send(sender);
        return super.onCommand(sender, command, s, args);
    }

    public CashCommand(JavaPlugin plugin, String command) {
        super(plugin, command, false);
        registerSubCommand(CashSubCommands.INFO, CashSubCommands.RESET, CashSubCommands.ADD, CashSubCommands.SUBTRACT, CashSubCommands.SET);
    }
    @Override protected boolean isPlayerTab() { return true; }

}

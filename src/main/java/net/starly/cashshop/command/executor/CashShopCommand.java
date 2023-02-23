package net.starly.cashshop.command.executor;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.command.STCashCommand;
import net.starly.cashshop.command.executor.sub.CashShopSubCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import java.util.*;

public class CashShopCommand extends STCashCommand {

    public CashShopCommand(JavaPlugin plugin, String command) {
        super(plugin, command, true);
        registerSubCommand(
                CashShopSubCommands.OPEN,
                CashShopSubCommands.CREATE,
                CashShopSubCommands.REMOVE,
                CashShopSubCommands.EDIT,
                CashShopSubCommands.LIST,
                CashShopSubCommands.RELOAD
        );
    }
    @Override protected boolean isPlayerTab() { return false; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 2) {
            if(!(args[0].equals("목록") || args[0].equals("list")) && !(args[0].equals("리로드") || args[0].equals("reload")))
                return StringUtil.copyPartialMatches(args[1], CashShopMain.getPlugin().getCashShopRepository().getShopNames(), new ArrayList<>());
            else return Collections.emptyList();
        }
        return super.onTabComplete(sender, cmd, label, args);
    }

}


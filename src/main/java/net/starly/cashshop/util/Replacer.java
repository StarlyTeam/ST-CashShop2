package net.starly.cashshop.util;

import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Replacer {

    private final Set<Function<String, String>> functions;

    private Replacer(Set<Function<String, String>> functions) { this.functions = functions; }

    public Function<String, String> getFunction() {
        return (it)-> {
            String result = it;
            for(Function<String, String> func: functions) {
                result = func.apply(result);
            }
            return result;
        };
    }

    public static class ReplacerBuilder {

        private final Set<Function<String, String>> functions = new HashSet<>();
        private final DecimalFormat format = new DecimalFormat("#,###");

        public ReplacerBuilder append(Player player) {
            functions.add((it)->it.replace("{player}", player.getName()));
            return this;
        }

        public ReplacerBuilder append(CommandSender sender) {
            if(sender instanceof ConsoleCommandSender) return append((ConsoleCommandSender) sender);
            else return append((Player) sender);
        }

        public ReplacerBuilder append(ConsoleCommandSender console) {
            functions.add((it)->it.replace("{player}", "콘솔"));
            return this;
        }

        public ReplacerBuilder append(OfflinePlayer player) {
            functions.add((it)->it.replace("{player}", player.getName()));
            return this;
        }

        public ReplacerBuilder append(Long cash, boolean shop) {
            MessageContext context;
            if(shop) context = CashShopMessageContextImpl.getInstance();
            else context = CashMessageContextImpl.getInstance();
            functions.add((it)->it.replace("{cash}", format.format(cash)+ context.getOnlyString(MessageContext.Type.DEFAULT, "suffix")) );
            return this;
        }

        public Replacer build() { return new Replacer(functions); }
    }

}

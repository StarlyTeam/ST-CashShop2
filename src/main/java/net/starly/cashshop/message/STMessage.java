package net.starly.cashshop.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.starly.cashshop.CashShopMain;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Data
@AllArgsConstructor
public class STMessage {

    private String prefix;
    private String message;

    public String getText() {
        return prefix + message;
    }

    public void send(CommandSender sender) {
        if (message.isEmpty()) return;
        sender.sendMessage(prefix + message);
    }

    public void send(Player player) {
        if (message.isEmpty()) return;
        player.sendMessage(prefix + message);
    }

    public void send(ConsoleCommandSender console) {
        if (message.isEmpty()) return;
        console.sendMessage(prefix + message);
    }

    public void broadcast() {
        if (message.isEmpty()) return;
        CashShopMain.getPlugin().getServer().broadcastMessage(message);
    }

}

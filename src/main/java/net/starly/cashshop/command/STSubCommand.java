package net.starly.cashshop.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class STSubCommand {

    @NonNull private String kor;
    @NonNull private String eng;
    @NonNull private String description;
    @Nullable private STSubCommand nextCommand;
    @Nullable private BiConsumer<CommandSender, String[]> consumer;

    public boolean hasNext() { return nextCommand != null; }
    public void execute(CommandSender sender, String[] args, boolean shop) {
        if(sender.isOp() || sender.hasPermission("starly.cashshop."+eng)) consumer.accept(sender, args);
        else if(shop) CashShopMessageContextImpl.getInstance().get(MessageContext.Type.ERROR, "permission").send(sender);
        else CashMessageContextImpl.getInstance().get(MessageContext.Type.ERROR, "permission").send(sender);
    }

}

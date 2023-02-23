package net.starly.cashshop.command.executor.sub;

import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.command.STSubCommand;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.shop.STCashShop;
import net.starly.cashshop.shop.container.impl.CashShopContainer;
import net.starly.cashshop.shop.container.impl.CashShopSettingContainer;
import net.starly.cashshop.shop.impl.CashShopImpl;
import net.starly.cashshop.shop.settings.GlobalShopSettings;
import net.starly.cashshop.util.Replacer;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CashShopSubCommands {

    private static final STSubCommand shopCommand = new STSubCommand("<상점>", "<name>", "");
    private static final STSubCommand lineCommand = new STSubCommand("<크기(줄)>", "<line>", "");
    private static final STSubCommand shopCommandAtLine = new STSubCommand("<상점>", "<name>", "", lineCommand ,(unused1, unused2)->{});
    private static final CashShopMessageContextImpl context = CashShopMessageContextImpl.getInstance();

    public static final STSubCommand OPEN =
            new STSubCommand("열기", "open", "해당 캐시상점을 오픈합니다.", shopCommand, (sender, args)->{
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("콘솔에서는 불가능합니다.");
                    return;
                }
                if(args.length == 0) context.get(MessageContext.Type.ERROR, "noShopName").send(sender);
                else {
                    CashShopImpl shop = CashShopMain.getPlugin().getCashShopRepository().getShop(args[0]);
                    if(shop == null) context.get(MessageContext.Type.ERROR, "shopNameNotFound").send(sender);
                    else new CashShopContainer(GlobalShopSettings.getInstance().isPrintNpcName() ? "§7명령어 강제실행" : shop.getName(), shop).open((Player) sender);
                }
    });

    public static final STSubCommand CREATE =
            new STSubCommand("생성", "create", "상점을 생성합니다. (이름의 띄어쓰기는 _로 구분)", shopCommandAtLine, (sender, args)->{
                try {
                    if(args.length == 0) context.get(MessageContext.Type.ERROR, "noShopName").send(sender);
                    else if(args.length == 1) context.get(MessageContext.Type.ERROR, "noLineValue").send(sender);
                    else {
                        int line = Integer.parseInt(args[1]);
                        if(line < 1 || line > 6) context.get(MessageContext.Type.ERROR, "wrongLineValue").send(sender);
                        else if(CashShopMain.getPlugin().getCashShopRepository().registerShop(args[0], Integer.parseInt(args[1])))
                            context.get(MessageContext.Type.DEFAULT, "createShop",(it)->it.replace("{shop}", ChatColor.translateAlternateColorCodes('&', args[0].replace("_", " ")))).send(sender);
                        else context.get(MessageContext.Type.ERROR, "containsShopName").send(sender);
                    }
                } catch (Exception e) { context.get(MessageContext.Type.ERROR, "noLineValue").send(sender); }
    });

    public static final STSubCommand REMOVE =
            new STSubCommand("삭제", "remove", "상점을 삭제합니다.", shopCommand, (sender, args)->{
                if(args.length == 0) context.get(MessageContext.Type.ERROR, "noShopName").send(sender);
                else {
                    CashShopImpl shop = CashShopMain.getPlugin().getCashShopRepository().getShop(args[0]);
                    if(shop == null) context.get(MessageContext.Type.ERROR, "shopNameNotFound").send(sender);
                    else {
                        CashShopMain.getPlugin().getCashShopRepository().unregisterShop(shop);
                        context.get(MessageContext.Type.DEFAULT, "removeShop", new Replacer.ReplacerBuilder().append(shop).build().getFunction()).send(sender);
                    }
                }
    });

    public static final STSubCommand EDIT =
            new STSubCommand("편집", "edit", "상점을 편집합니다.", shopCommand, (sender, args)->{
                if(sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("콘솔에서는 불가능합니다.");
                    return;
                }
                if(args.length == 0) context.get(MessageContext.Type.ERROR, "noShopName").send(sender);
                else {
                    CashShopImpl shop = CashShopMain.getPlugin().getCashShopRepository().getShop(args[0]);
                    if(shop == null) context.get(MessageContext.Type.ERROR, "shopNameNotFound").send(sender);
                    else new CashShopSettingContainer(shop).open((Player) sender);
                }
    });

    public static final STSubCommand RELOAD =
            new STSubCommand("리로드", "reload", "config.yml 파일을 새로 불러옵니다.", null, (sender, args)->{
                CashShopMain.getPlugin().loadConfiguration();
                context.get(MessageContext.Type.DEFAULT, "reloadComplete").send(sender);
    });

    public static final STSubCommand LIST =
            new STSubCommand("목록", "list", "생성 된 캐시상점 목록을 확인합니다.", new STSubCommand("페이지", "page", ""), (sender, args)->{
                List<STCashShop> shops = CashShopMain.getPlugin().getCashShopRepository().getShops();
                int page;
                try {
                    page = Integer.parseInt(args[0]) - 1;
                } catch (Exception e) {page = 0;}
                if(shops.isEmpty()) context.get(MessageContext.Type.ERROR, "emptyShopList").send(sender);
                else {
                    int maxPage = shops.size() / 10 - (shops.size() % 10 == 0 ? 1 : 0);
                    sender.sendMessage("§e§l:: §c§l캐시상점 목록 §e§l:: §7§l[ §a§l"+ page + "§7 / §6§l" + maxPage + "§7§l]");
                    sender.sendMessage("§8-------------------------------------------");
                    for(int i = 0; i < 10; i++) {
                        int index = i + (page * 10);
                        if(shops.size() <= index) break;
                        STCashShop shop = shops.get(index);
                        sender.sendMessage(" §f§l"+(index + 1) + ": §f" + shop.getName() + "§f " + (shop.isClosed()?"§c§[닫힘]":"§a§l[열림]") + " §e▸ §7[NPC:"+(shop.getNpc() == null?"설정되지않음":shop.getNpc())+"§7]");
                    }
                    sender.sendMessage("§8-------------------------------------------");
                }
    });
}

package net.starly.cashshop.command.executor.sub;

import lombok.AllArgsConstructor;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.command.STSubCommand;
import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import net.starly.cashshop.VersionController;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CashShopSubCommands {

    private static final STSubCommand shopCommand = new STSubCommand("상점", "name", "");

    @AllArgsConstructor
    static class TestClass {
        public int data;
    }

    public static final STSubCommand OPEN =
            new STSubCommand("열기", "open", "해당 캐시상점을 오픈합니다.", shopCommand, (sender, args)->{
                if(sender instanceof Player) {
                    Player player = ((Player) sender);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (args[1].equals("set")) {
                        item.setItemMeta(VersionController.getInstance().getItemStackUtility().addNbtTagCompound(new TestClass(100), item, TestClass.class).getItemMeta());
                    } else {
                        sender.sendMessage(VersionController.getInstance().getItemStackUtility().getNbtTagCompound(TestClass.class, item).data + " < -- ");
                    }
                }

    });

    public static final STSubCommand NPC =
            new STSubCommand("상인", "npc", "NPC를 상인으로 등록합니다.", shopCommand, (sender, args)->{

    });

    public static final STSubCommand CREATE =
            new STSubCommand("생성", "create", "상점을 생성합니다.", shopCommand, (sender, args)->{

    });

    public static final STSubCommand REMOVE =
            new STSubCommand("삭제", "remove", "상점을 생성합니다.", shopCommand, (sender, args)->{

    });

    public static final STSubCommand EDIT =
            new STSubCommand("편집", "edit", "상점을 편집합니다.", shopCommand, (sender, args)->{

    });

    public static final STSubCommand RELOAD =
            new STSubCommand("리로드", "reload", "config.yml 파일을 새로 불러옵니다.", null, (sender, args)->{
                CashShopMain.getPlugin().loadConfiguration();
                CashShopMessageContextImpl.getInstance().get(MessageContext.Type.DEFAULT, "reloadComplete").send(sender);
    });

    public static final STSubCommand LIST =
            new STSubCommand("목록", "list", "생성 된 캐시상점 목록을 확인합니다.", new STSubCommand("페이지", "page", ""), (sender, args)->{

    });
}

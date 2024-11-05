package net.starly.cashshop.command;

import net.starly.cashshop.message.MessageContext;
import net.starly.cashshop.message.impl.CashMessageContextImpl;
import net.starly.cashshop.message.impl.CashShopMessageContextImpl;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public abstract class STCashCommand implements CommandExecutor, TabCompleter {

    // 캐시상점 상인[npc] 상점[name] - n 초 이내로 우클릭 한 npc 이름 등록
    // 캐시상점 생성[create] 상점[name] - 캐시상점을 생성합니다.
    // 캐시상점 삭제[remove] 상점[name] - 캐시상점을 삭제합니다.
    // 캐시상점 편집[edit] 상점[name] - 캐시상점 설정 setting gui open
    // 캐시상점 목록[list] - 생성 된 캐시상점 목록을 확인합니다.
    // 캐시상점 열기[open] - 캐시상점 열기 shop gui open
    // 캐시상점 리로드[reload] - config.yml 파일을 새로 불러옵니다.

    // 캐시 - 캐시 확인
    // 캐시 확인[info] 닉네임[player] - 해당 유저의 캐시를 확인
    // 캐시 지급[add] 닉네임[player] 금액[amount] - 해당 유저에게 amount 캐시 지급
    // 캐시 차감[sub] 닉네임[player] 금액[amount] - 해당 유저의 캐시를 amount 만큼 차감
    // 캐시 설정[set] 닉네임[player] 금액[amount] - 해당 유저의 캐시를 amount 로 설정
    // 캐시 초기화[reset] 닉네임[player] - 해당 유저의 캐시를 초기화

    private final String command;
    private final List<STSubCommand> subCommands = new ArrayList<>();
    private final MessageContext context;

    public STCashCommand(JavaPlugin plugin, String command, boolean shop) {
        this.command = command;
        PluginCommand cmd = Objects.requireNonNull(plugin.getCommand(command));
        cmd.setExecutor(this);
        cmd.setTabCompleter(this);
        if (shop) context = CashShopMessageContextImpl.getInstance();
        else context = CashMessageContextImpl.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        execute(sender, s, !s.equals(this.command), args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        String first;
        boolean korean = !label.equals(command);
        try {
            first = args[0];
        } catch (ArrayIndexOutOfBoundsException exception) {
            first = "";
        }
        if (args.length <= 1) return StringUtil.copyPartialMatches(first, getSubCommands(korean)
                .stream()
                .filter((it) -> sender.isOp() || sender.hasPermission("starly.cashshop." + it))
                .collect(Collectors.toList()), new ArrayList<>()
        );
        else if (isPlayerTab()) {
            if (args.length == 2) return null;
            else return Collections.emptyList();
        } else return Collections.emptyList();
    }

    protected abstract boolean isPlayerTab();

    private void execute(CommandSender player, String label, boolean korean, String[] args) {
        if (args.length == 0) printHelpLine(player, label, korean);
        else {
            Optional<STSubCommand> optionalSTSubCommand = subCommands.stream().filter((it) -> it.getKor().equals(args[0]) || it.getEng().equals(args[0])).findFirst();
            if (optionalSTSubCommand.isPresent()) {
                STSubCommand sub = optionalSTSubCommand.get();
                if (sub.hasNext() && args.length == 1 && isPlayerTab())
                    context.get(MessageContext.Type.ERROR, "noPlayerName").send(player);
                else
                    sub.execute(player, args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length), label.equalsIgnoreCase("cashshop") || label.equals("캐시상점"));
            } else context.get(MessageContext.Type.ERROR, "wrongCommand").send(player);
        }
    }

    protected void printHelpLine(CommandSender sender, String label, boolean korean) {
        reformattedHelpline(korean, label, subCommands
                .stream()
                .filter((it) -> sender.isOp() || sender.hasPermission("starly.cashshop." + it.getEng()))
                .collect(Collectors.toList())
        ).forEach(sender::sendMessage);
    }

    protected List<String> reformattedHelpline(boolean korean, String label, List<STSubCommand> subCommandList) {
        return subCommandList.stream().map((it) -> {
            StringBuilder builder = new StringBuilder("§6/");
            builder.append(label);
            builder.append(" ");
            if (korean) {
                builder.append(it.getKor());
                STSubCommand pointer = it;
                while (pointer.hasNext()) {
                    pointer = pointer.getNextCommand();
                    builder.append(" ").append(pointer.getKor());
                }
            } else {
                builder.append(it.getEng());
                STSubCommand pointer = it;
                while (pointer.hasNext()) {
                    pointer = pointer.getNextCommand();
                    builder.append(" ").append(pointer.getEng());
                }
            }
            builder.append("§f : ").append(it.getDescription());
            return builder.toString();
        }).collect(Collectors.toList());
    }

    private List<String> getSubCommands(boolean korean) {
        return subCommands.stream().map((it) -> {
            if (korean) return it.getKor();
            else return it.getEng();
        }).collect(Collectors.toList());
    }

    protected void registerSubCommand(STSubCommand... subCommands) {
        this.subCommands.addAll(Arrays.asList(subCommands));
    }

}

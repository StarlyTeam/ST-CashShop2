package net.starly.cashshop.support.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.starly.cashshop.CashShopMain;
import net.starly.cashshop.cash.PlayerCash;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CashExpression extends SimpleExpression<Long> {

    static {
        Skript.registerExpression(CashExpression.class, Long.class, ExpressionType.PROPERTY, "%player%'s cash", "cash of %player%");
    }

    private Expression<Player> player;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult result) {
        player = (Expression<Player>) exprs[0];
        return true;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return player.toString(event, debug);
    }

    @Override
    public Class<Long> getReturnType() {
        return Long.class;
    }

    @Override
    public Long[] get(Event event) {
        Player player = this.player.getSingle(event);
        if (player != null)
            return new Long[]{CashShopMain.getPlugin().getPlayerCashRepository().getPlayerCash(player.getUniqueId()).getCash()};
        return null;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        Class<?>[] classes;
        if (mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD)
            classes = new Class[]{Long.class};
        else classes = null;
        return classes;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Player player = this.player.getSingle(event);
        if (player != null) {
            PlayerCash cash = CashShopMain.getPlugin().getPlayerCashRepository().getPlayerCash(player.getUniqueId());
            if (mode == Changer.ChangeMode.ADD) {
                cash.addCash("스크립트 추가", PlayerCash.Type.ADD, (long) delta[0]);
            } else if (mode == Changer.ChangeMode.REMOVE) {
                cash.subCash("스크립트 감소", PlayerCash.Type.SUB, (long) delta[0]);
            } else if (mode == Changer.ChangeMode.SET) {
                cash.setCash("스크립트 설정", PlayerCash.Type.SET, (long) delta[0]);
            }
        }
    }


}

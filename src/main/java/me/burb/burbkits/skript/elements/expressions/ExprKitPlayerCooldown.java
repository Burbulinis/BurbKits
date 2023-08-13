package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ExprKitPlayerCooldown extends SimpleExpression<Timespan> {

    static {
        Skript.registerExpression(ExprKitPlayerCooldown.class, Timespan.class, ExpressionType.COMBINED,
                "[the] kit cooldown (of|for) %offlineplayers% (of|from|for) [kit] %kit%",
                "%offlineplayers%'[s] kit cooldown (of|from) [kit] %kit%"
        );
    }

    private Expression<OfflinePlayer> players;
    private Expression<Kit> kit;
    private boolean isSingle;

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, @NotNull ChangeMode mode) {

        Kit kit = this.kit.getSingle(event);
        OfflinePlayer[] players = this.players.getArray(event);

        assert kit != null;

        for (OfflinePlayer player : players) {

            long cooldown = kit.getPlayerCooldown(player).getTime();
            long millis = 0;

            if (delta != null) {
                Timespan timespan = Timespan.parse(String.valueOf(delta[0]));
                if (timespan != null) millis = timespan.getMilliSeconds();
            }

            Timestamp timestamp = new Timestamp(0);

            switch (mode) {
                case ADD:
                    timestamp.setTime(cooldown + millis);
                    kit.setPlayerCooldown(timestamp, player);
                    break;
                case DELETE:
                case REMOVE:
                    timestamp.setTime(cooldown - millis);
                    if (millis > cooldown)
                        kit.setPlayerCooldown(null, player);
                    else
                        kit.setPlayerCooldown(timestamp, player);
                    break;
                case REMOVE_ALL:
                case RESET:
                    kit.resetCooldown(player);
                    break;
                case SET:
                    timestamp.setTime(System.currentTimeMillis() + millis);
                    kit.setPlayerCooldown(timestamp, player);
                    break;
                default:
                    assert false;
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(@NotNull ChangeMode mode) {
        return (mode != ChangeMode.DELETE && mode != ChangeMode.REMOVE_ALL) ? CollectionUtils.array(Timespan.class) :  null;
    }

    @Override
    protected Timespan @NotNull [] get(@NotNull Event event) {

        Kit kit = this.kit.getSingle(event);
        OfflinePlayer[] players = this.players.getArray(event);
        List<Timespan> timespans = new ArrayList<>();

        assert kit != null;

        for (OfflinePlayer player : players) {
            timespans.add(new Timespan(kit.getPlayerCooldownDifferenceBetweenNowAsMillis(player)));
        }

        return timespans.toArray(new Timespan[0]);
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "cooldown of players " + players.toString(event, b) + " of the kit " + kit.toString(event, b);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        players = (Expression<OfflinePlayer>) exprs[0];
        kit = (Expression<Kit>) exprs[1];
        isSingle = exprs[0].isSingle();
        return true;
    }
}

package me.burb.burbkits.skript.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class CondHasCooldown extends Condition {

    static {
        Skript.registerCondition(CondHasCooldown.class,
                "%offlineplayers% (has|have) [the] cooldown of [kit] %kit%",
                "%offlineplayers% (doesn't|does not|do not|don't) have [the] cooldown of [kit] %kit%"
        );
    }

    private Expression<Player> players;
    private Expression<Kit> kit;
    private boolean has;

    @Override
    public boolean check(@NotNull Event event) {

        Kit kit = this.kit.getSingle(event);
        Player[] players = this.players.getArray(event);

        if (kit == null) return false;

        for (Player player : players) {

            if (has)
                return kit.hasCooldown(player);
            else
                return !kit.hasCooldown(player);
        }

        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "players " + players.toString(event, b) + " have the cooldown of kit " + kit.toString(event, b);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        players = (Expression<Player>) exprs[0];
        kit = (Expression<Kit>) exprs[1];
        has = matchedPattern == 0;
        return true;
    }
}

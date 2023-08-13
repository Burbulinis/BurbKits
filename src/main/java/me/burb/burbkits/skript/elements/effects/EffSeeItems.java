package me.burb.burbkits.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EffSeeItems extends Effect {

    static {
        Skript.registerEffect(EffSeeItems.class,
                "open [the] kit(-| )inventory of [kit] %kit% to %players%"
        );
    }

    private Expression<Kit> kit;
    private Expression<Player> players;

    @Override
    protected void execute(@NotNull Event event) {

        Kit kit = this.kit.getSingle(event);
        Player[] players = this.players.getArray(event);

        assert kit != null;

        for (Player player : players) {
            kit.seeItems(player);
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "open kit inventory of  " + kit.toString(event, b) + " to the player" + (players.isSingle() ? " " : "s ") + players.toString(event, b);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        kit = (Expression<Kit>) exprs[0];
        players = (Expression<Player>) exprs[1];
        return true;
    }
}

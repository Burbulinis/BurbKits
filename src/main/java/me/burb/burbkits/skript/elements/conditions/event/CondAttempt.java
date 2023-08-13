package me.burb.burbkits.skript.elements.conditions.event;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.events.KitClaimAttemptEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondAttempt extends Condition {

    static {
        Skript.registerCondition(CondAttempt.class,
                "[the] attempt (was|is) (successful|1¦unsuccessful)"
        );
    }

    private boolean successful;

    @Override
    public boolean check(@NotNull Event event) {
        KitClaimAttemptEvent e = (KitClaimAttemptEvent) event;
        if (successful)
            return e.canClaim();
        else
            return !e.canClaim();
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "cond attempt was successful: " + successful;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        if (ParserInstance.get().isCurrentEvent(KitClaimAttemptEvent.class)) {
            successful = parseResult.mark != 1;
        } else {
            Skript.error("This condition is only used in the kit claim attempt event");
            return false;
        }
        return true;
    }
}

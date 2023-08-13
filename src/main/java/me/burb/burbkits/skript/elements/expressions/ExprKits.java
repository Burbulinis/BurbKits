package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.burb.burbkits.api.kits.Kit.ALL_KITS;

public class ExprKits extends SimpleExpression<Kit> {

    static {
        Skript.registerExpression(ExprKits.class, Kit.class, ExpressionType.SIMPLE,
                "[(all [[of] the]|the)] kits"
        );
    }

    @Override
    protected Kit @NotNull [] get(@NotNull Event event) {
        return ALL_KITS.values().toArray(new Kit[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends Kit> getReturnType() {
        return Kit.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "all of the kits";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        return true;
    }
}

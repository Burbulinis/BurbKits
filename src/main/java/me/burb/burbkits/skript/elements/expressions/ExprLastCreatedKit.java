package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.skript.elements.sections.EffSecCreateKit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprLastCreatedKit extends SimpleExpression<Kit> {

    static {
        Skript.registerExpression(ExprLastCreatedKit.class, Kit.class, ExpressionType.SIMPLE,
                "[the] last created kit"
        );
    }

    @Override
    protected Kit @NotNull [] get(@NotNull Event event) {
        return new Kit[]{EffSecCreateKit.lastCreated};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Kit> getReturnType() {
        return Kit.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "last created kit";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int i, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        return true;
    }
}

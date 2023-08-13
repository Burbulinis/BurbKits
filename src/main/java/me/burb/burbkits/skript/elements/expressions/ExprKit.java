package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ExprKit extends SimpleExpression<Kit> {

    static {
        Skript.registerExpression(ExprKit.class, Kit.class, ExpressionType.COMBINED,
                "[the] kit[s] (named|with [the] name) %strings%");
    }

    private Expression<String> names;
    private boolean isSingle;

    @Override
    protected Kit @NotNull [] get(@NotNull Event event) {

        String[] names = this.names.getArray(event);
        List<Kit> kits = new ArrayList<>();

        for (String name : names) {

            Kit kit = Kit.getKitFromName(name);

            if (name != null && kit != null) {
                kits.add(kit);
            }

        }

        return kits.toArray(new Kit[0]);
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public @NotNull Class<? extends Kit> getReturnType() {
        return Kit.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "kit" + (isSingle ? "" : "s") + " named " + names.toString(event, b);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        names = (Expression<String>) exprs[0];
        isSingle = exprs[0].isSingle();
        return true;
    }
}

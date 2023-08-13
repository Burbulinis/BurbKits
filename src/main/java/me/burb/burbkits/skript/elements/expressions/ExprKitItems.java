package me.burb.burbkits.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ExprKitItems extends PropertyExpression<Kit, ItemType> {

    static {
        Skript.registerExpression(ExprKitItems.class, ItemType.class, ExpressionType.PROPERTY,
                "[the] kit(-| )items of [kit] %kit%",
                "%kit%'[s] kit(-| )items"
        );
    }

    @Override
    protected ItemType[] get(@NotNull Event event, Kit[] kits) {
        Kit kit = kits[0];

        if (kit == null) return null;

        List<ItemType> items = new ArrayList<>();
        kit.getItems().values().forEach(value -> items.add(Converters.convert(value, ItemType.class)));

        return items.toArray(new ItemType[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ItemType> getReturnType() {
        return ItemType.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "items of kit " + getExpr().toString(event, b);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {
        setExpr((Expression<Kit>) exprs[0]);
        return true;
    }
}

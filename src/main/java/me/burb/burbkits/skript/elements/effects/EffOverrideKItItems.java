package me.burb.burbkits.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class EffOverrideKItItems extends Effect {

    static {
        Skript.registerEffect(EffOverrideKItItems.class,
                "override [the] kit(-| )items of [kit] %kit% (with|to) %itemtypes%"
        );
    }

    private Expression<Kit> kit;
    private Variable<ItemType> var;

    @Override
    protected void execute(@NotNull Event event) {

        Kit kit = this.kit.getSingle(event);
        TreeMap<Integer, ItemStack> items = new TreeMap<>();

        assert kit != null;

        if (var != null) {
            TreeMap<?, ?> map = (TreeMap<?, ?>) var.getRaw(event);
            if (map != null) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    String s = String.valueOf(entry.getKey());
                    if (s != null && Integer.parseInt(s) >= 0) {
                        int index = Integer.parseInt(s);
                        ItemStack item = Converters.convert(entry.getValue(), ItemStack.class);
                        if (item != null) items.put(index - 1, item);
                    }
                }
            } else return;
        } else return;

        kit.setItems(items);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "override items of kit " + kit.toString(event, b) + " with " + var.toString(event, b);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean kleenean, @NotNull ParseResult parseResult) {

        kit = (Expression<Kit>) exprs[0];
        Expression<?> expr  = exprs[1];

        if (expr instanceof Variable) {
            Variable<ItemType> varExpr = (Variable<ItemType>) exprs[1];
            if (varExpr.isList()) {
                var = varExpr;
            } else {
                Skript.error(expr + " is not a list variable");
                return false;
            }
        } else {
            Skript.error(expr + " is not a list variable");
            return false;
        }

        return true;
    }
}
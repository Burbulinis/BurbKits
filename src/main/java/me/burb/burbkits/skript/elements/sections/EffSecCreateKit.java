package me.burb.burbkits.skript.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import me.burb.burbkits.api.events.KitEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class EffSecCreateKit extends EffectSection {

    public static Kit lastCreated;

    public static class KitCreateEvent extends KitEvent {
        public KitCreateEvent(Kit kit) {
            super(kit);
            lastCreated = kit;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }

    static {
        Skript.registerSection(EffSecCreateKit.class,
                "create [a [new]] kit (named|with [the] name) %string% (with|containing) [the] kit items (in|of) %itemtypes%"
        );
        EventValues.registerEventValue(KitCreateEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitCreateEvent kitCreateEvent) {
                return kitCreateEvent.getKit();
            }
        }, EventValues.TIME_NOW);
    }

    private Trigger trigger;
    private Expression<String> name;
    private Variable<?> var;
    private Expression<PlayerInventory> inv;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult, @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> list) {

        name = (Expression<String>) exprs[0];
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

        if (sectionNode != null) {
            trigger = loadCode(sectionNode, "kit create", KitCreateEvent.class);
        }

        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {

        Object localVars = Variables.copyLocalVariables(event);
        String name = this.name.getSingle(event);
        Kit kit = null;

        if (name != null) {
            if (Kit.getKitFromName(name) == null) {
                kit = new Kit(name);
            } else {
                kit = Kit.getKitFromName(name);
            }

            assert kit != null;

            if (var != null) {
                TreeMap<?, ?> map = (TreeMap<?, ?>) var.getRaw(event);
                TreeMap<Integer, ItemStack> items = new TreeMap<>();
                if (map != null) {

                    for (Map.Entry<?, ?> entry : map.entrySet()) {

                        String s = String.valueOf(entry.getKey());

                        if (s != null && Integer.parseInt(s) >= 0) {

                            int index = Integer.parseInt(s);
                            ItemStack item = Converters.convert(entry.getValue(), ItemStack.class);
                            if (item != null) items.put(index - 1, item);

                        }
                    }

                    kit.setItems(items);
                }
            }
        }

        if (trigger != null) {
            KitCreateEvent kitCreateEvent = new KitCreateEvent(kit);
            Variables.setLocalVariables(kitCreateEvent, localVars);

            TriggerItem.walk(trigger, kitCreateEvent);

            Variables.setLocalVariables(event, Variables.copyLocalVariables(kitCreateEvent));
            Variables.removeLocals(kitCreateEvent);
        }

        return super.walk(event, false);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "kit named " + name.toString(event, b) + " with the items " + var.toString(event, b);
    }
}
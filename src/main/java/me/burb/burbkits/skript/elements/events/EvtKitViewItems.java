package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitItemsChangeEvent;
import me.burb.burbkits.api.events.KitViewItemsEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitViewItems extends SkriptEvent {

    static {
        Skript.registerEvent("Kit View Items", EvtKitViewItems.class, KitViewItemsEvent.class,
                "[on] kit view[ing] items [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitViewItemsEvent.class, Kit.class, new Getter<>() {
            @Override
            public @Nullable Kit get(KitViewItemsEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitViewItemsEvent.class, Player.class, new Getter<>() {
            @Override
            public @Nullable Player get(KitViewItemsEvent e) {
                return e.getPlayer();
            }
        }, EventValues.TIME_NOW);
        EventValues.registerEventValue(KitViewItemsEvent.class, Inventory.class, new Getter<>() {
            @Override
            public @Nullable Inventory get(KitViewItemsEvent e) {
                return e.getInventory();
            }
        }, EventValues.TIME_NOW);
    }

    private Literal<String> name;

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int matchedPattern, @NotNull ParseResult parseResult) {
        name = (Literal<String>) literals[0];
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (name != null) {

            Kit kit = Kit.getKitFromName(name.getSingle(event));
            Kit is = ((KitItemsChangeEvent) event).getKit();

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit view items" + s;
    }
}

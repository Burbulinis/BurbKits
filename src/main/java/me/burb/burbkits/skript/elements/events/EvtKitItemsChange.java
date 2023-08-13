package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitItemsChangeEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

@SuppressWarnings("unchecked")
public class EvtKitItemsChange extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Items Change", EvtKitItemsChange.class, KitItemsChangeEvent.class,
                "[on] kit(-| )items change [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitItemsChangeEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitItemsChangeEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitItemsChangeEvent.class, ItemType[].class, new Getter<>() {
            @Override
            public ItemType[] get(KitItemsChangeEvent e) {
                return treeToArray(e.getOldItems());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitItemsChangeEvent.class, ItemType[].class, new Getter<>() {
            @Override
            public ItemType[] get(KitItemsChangeEvent e) {
                return treeToArray(e.getNewItems());
            }
        }, EventValues.TIME_FUTURE);
    }

    private Literal<String> name;

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int matchedPattern, @NotNull ParseResult parseResult) {
        name = (Literal<String>) literals[0];
        return true;
    }

    public static ItemType[] treeToArray(TreeMap<Integer, ItemStack> items) {
        List<ItemType> itemtypes = new ArrayList<>();
        if (items != null) {
            Set<Entry<Integer, ItemStack>> entries = items.entrySet();
            for (Entry<Integer, ItemStack> entry : entries) {
                ItemType item = Converters.convert(entry.getValue(), ItemType.class);
                if (item != null) {
                    itemtypes.add(item);
                }
            }
        }
        return itemtypes.toArray(new ItemType[0]);
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
        return "kit items change" + s;
    }
}

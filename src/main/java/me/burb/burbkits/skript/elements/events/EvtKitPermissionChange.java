package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitPermissionChangeEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitPermissionChange extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Permission Change", EvtKitPermissionChange.class, KitPermissionChangeEvent.class,
                "[on] kit permission change [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitPermissionChangeEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitPermissionChangeEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitPermissionChangeEvent.class, String.class, new Getter<>() {
            @Override
            public String get(KitPermissionChangeEvent e) {
                return e.getOldPermission();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitPermissionChangeEvent.class, String.class, new Getter<>() {
            @Override
            public String get(KitPermissionChangeEvent e) {
                return e.getNewPermission();
            }
        }, EventValues.TIME_FUTURE);
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
            Kit is = ((KitPermissionChangeEvent) event).getKit();

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit permission change" + s;
    }
}

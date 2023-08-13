package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitCooldownBypassChangeEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitCooldownBypassChange extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Cooldown Bypass Change", EvtKitCooldownBypassChange.class, KitCooldownBypassChangeEvent.class,
                "[on] kit cooldown bypass change [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitCooldownBypassChangeEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitCooldownBypassChangeEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitCooldownBypassChangeEvent.class, String.class, new Getter<>() {
            @Override
            public String get(KitCooldownBypassChangeEvent e) {
                return e.getOldCooldownBypassPerm();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitCooldownBypassChangeEvent.class, String.class, new Getter<>() {
            @Override
            public String get(KitCooldownBypassChangeEvent e) {
                return e.getNewCooldownBypassPerm();
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
            Kit is = ((KitCooldownBypassChangeEvent) event).getKit();

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit cooldown bypass change" + s;
    }
}

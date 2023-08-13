package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import me.burb.burbkits.api.events.KitCooldownChangeEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitCooldownChange extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Cooldown Change", EvtKitCooldownChange.class, KitCooldownChangeEvent.class,
                "[on] kit cooldown change [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitCooldownChangeEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitCooldownChangeEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitCooldownChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public Timespan get(KitCooldownChangeEvent e) {
                return new Timespan(e.getOldCooldown());
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitCooldownChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public Timespan get(KitCooldownChangeEvent e) {
                return new Timespan(e.getNewCooldown());
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
            Kit is = ((KitCooldownChangeEvent) event).getKit();

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit cooldown change" + s;
    }
}

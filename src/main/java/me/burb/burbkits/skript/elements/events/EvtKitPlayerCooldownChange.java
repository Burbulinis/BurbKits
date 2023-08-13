package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import me.burb.burbkits.api.events.KitPlayerCooldownChangeEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitPlayerCooldownChange extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Player Cooldown Change", EvtKitPlayerCooldownChange.class, KitPlayerCooldownChangeEvent.class,
                "[on] kit player cooldown change [(of|for) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitPlayerCooldownChangeEvent.class, Kit.class, new Getter<>() {
            @Override
            @Nullable
            public Kit get(KitPlayerCooldownChangeEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitPlayerCooldownChangeEvent.class, OfflinePlayer.class, new Getter<>() {
            @Override
            @Nullable
            public OfflinePlayer get(KitPlayerCooldownChangeEvent e) {
                return e.getPlayer();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitPlayerCooldownChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public @NotNull Timespan get(KitPlayerCooldownChangeEvent e) {
                return new Timespan((e.getOldCooldown().getTime() - System.currentTimeMillis()));
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitPlayerCooldownChangeEvent.class, Timespan.class, new Getter<>() {
            @Override
            public @NotNull Timespan get(KitPlayerCooldownChangeEvent e) {
                return new Timespan((e.getNewCooldown().getTime() - System.currentTimeMillis()));
            }
        }, EventValues.TIME_FUTURE);
    }

    private Literal<String> name;

    @Override
    @NotNull
    public String toString(Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit player cooldown change" + s;
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, @NotNull ParseResult parseResult) {
        name = (Literal<String>) literals[0];
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (name != null) {

            Kit kit = Kit.getKitFromName(name.getSingle(event));
            Kit is = ((KitPlayerCooldownChangeEvent) event).getKit();

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }
        return true;
    }
}

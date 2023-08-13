package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitClaimAttemptEvent;
import me.burb.burbkits.api.events.KitClaimEvent;
import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class EvtKitClaim extends SkriptEvent {

    static {
        Skript.registerEvent("Kit Claim", EvtKitClaim.class, KitClaimEvent.class,
                "[on] kit claim [(for|of) [kit] %-string%]"
        );

        Skript.registerEvent("Kit Claim Attempt", EvtKitClaim.class, KitClaimAttemptEvent.class,
                "[on] kit claim attempt [(for|of) [kit] %-string%]"
        );

        EventValues.registerEventValue(KitClaimAttemptEvent.class, Kit.class, new Getter<>() {
            @Override
            public @Nullable Kit get(KitClaimAttemptEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitClaimAttemptEvent.class, Player.class, new Getter<>() {
            @Override
            public @Nullable Player get(KitClaimAttemptEvent e) {
                return e.getPlayer();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitClaimAttemptEvent.class, Boolean.class, new Getter<>() {
            @Override
            public Boolean get(KitClaimAttemptEvent e) {
                return e.canClaim();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitClaimEvent.class, Kit.class, new Getter<>() {
            @Override
            public Kit get(KitClaimEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitClaimEvent.class, Player.class, new Getter<>() {
            @Override
            public Player get(KitClaimEvent e) {
                return e.getPlayer();
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

            final Kit is;

            if (event instanceof KitClaimEvent) {
                is = ((KitClaimEvent) event).getKit();
            } else if (event instanceof KitClaimAttemptEvent) {
                is = ((KitClaimAttemptEvent) event).getKit();
            } else {
                assert false;
                return false;
            }

            if (kit == null) return false;

            return name.check(event, name -> kit.equals(is));

        }

        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        String s = name != null ? " for the kit '" + name.toString(event, b) + "'" : "";
        return "kit claim/attempt" + s;
    }
}

package me.burb.burbkits.skript.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import me.burb.burbkits.api.events.KitCreateEvent;
import me.burb.burbkits.api.events.KitDeleteEvent;
import me.burb.burbkits.api.kits.Kit;
import org.jetbrains.annotations.Nullable;

public class SimpleEvents {

    static {
        Skript.registerEvent("Kit Create", SimpleEvent.class, KitCreateEvent.class,
                "[on] kit create"
        );

        Skript.registerEvent("Kit Delete", SimpleEvent.class, KitDeleteEvent.class,
                "[on] kit delete"
        );

        EventValues.registerEventValue(KitCreateEvent.class, Kit.class, new Getter<>() {
            @Override
            @Nullable
            public Kit get(KitCreateEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);

        EventValues.registerEventValue(KitDeleteEvent.class, Kit.class, new Getter<>() {
            @Override
            @Nullable
            public Kit get(KitDeleteEvent e) {
                return e.getKit();
            }
        }, EventValues.TIME_NOW);
    }
}

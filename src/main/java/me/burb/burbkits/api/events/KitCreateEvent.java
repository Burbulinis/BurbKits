package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitCreateEvent extends KitEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public KitCreateEvent(Kit kit) {
        super(kit);
    }
}

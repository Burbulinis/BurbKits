package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitCooldownBypassChangeEvent extends KitEvent implements Cancellable {
    private boolean cancelled;
    private final String oldCooldownBypassPerm;
    private final String newCooldownBypassPerm;
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public KitCooldownBypassChangeEvent(Kit kit, String oldCooldownBypassPerm, String newCooldownBypassPerm) {
        super(kit);
        this.oldCooldownBypassPerm = oldCooldownBypassPerm;
        this.newCooldownBypassPerm = newCooldownBypassPerm;
    }

    public String getOldCooldownBypassPerm() {
        return oldCooldownBypassPerm;
    }
    public String getNewCooldownBypassPerm() {
        return newCooldownBypassPerm;
    }
}

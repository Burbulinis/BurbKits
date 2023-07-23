package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitCooldownChangeEvent extends KitEvent implements Cancellable {
    private boolean cancelled;
    private final long oldCooldown;
    private final long newCooldown;
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

    public KitCooldownChangeEvent(Kit kit, long oldCooldown, long newCooldown) {
        super(kit);
        this.oldCooldown = oldCooldown;
        this.newCooldown = newCooldown;
    }

    public long getOldCooldown() {
        return oldCooldown;
    }
    public long getNewCooldown() {
        return newCooldown;
    }
}

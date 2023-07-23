package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitClaimAttemptEvent extends KitEvent implements Cancellable {
    private final Player player;
    private final boolean success;
    private boolean cancelled;
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

    public KitClaimAttemptEvent(Kit kit, Player player, boolean success) {
        super(kit);
        this.player = player;
        this.success = success;
    }

    public Player getPlayer() {
        return player;
    }
    public boolean canClaim() {
        return success;
    }
}

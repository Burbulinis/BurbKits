package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class KitPlayerCooldownChangeEvent extends KitEvent implements Cancellable {
    private boolean cancelled;
    private final OfflinePlayer player;
    private final Timestamp oldCooldown;
    private final Timestamp newCooldown;
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

    public KitPlayerCooldownChangeEvent(Kit kit, OfflinePlayer player, Timestamp oldCooldown, Timestamp newCooldown) {
        super(kit);
        this.player = player;
        this.oldCooldown = oldCooldown;
        this.newCooldown = newCooldown;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }
    public Timestamp getOldCooldown() {
        return oldCooldown;
    }
    public Timestamp getNewCooldown() {
        return newCooldown;
    }
}

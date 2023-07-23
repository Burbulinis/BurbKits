package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitPermissionChangeEvent extends KitEvent implements Cancellable  {
    private final String oldPerm;
    private final String newPerm;
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

    public KitPermissionChangeEvent(Kit kit, String oldPerm, String newPerm) {
        super(kit);
        this.oldPerm = oldPerm;
        this.newPerm = newPerm;
    }

    public String getOldPermission() {
        return oldPerm;
    }
    public String getNewPermission() {
        return newPerm;
    }
}

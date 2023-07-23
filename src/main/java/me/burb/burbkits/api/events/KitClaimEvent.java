package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class KitClaimEvent extends KitEvent {
    private final Player player;
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public KitClaimEvent(Kit kit, Player player) {
        super(kit);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

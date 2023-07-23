package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class KitViewItemsEvent extends KitEvent implements Cancellable {
    private final Player player;
    private final Inventory inv;
    private boolean cancelled;
    private final TreeMap<Integer, ItemStack> items;
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

    public KitViewItemsEvent(Kit kit, Player player, Inventory inv, TreeMap<Integer, ItemStack> items) {
        super(kit);
        this.player = player;
        this.inv = inv;
        this.items = items;
    }

    public Player getPlayer() {
        return player;
    }
    public Inventory getInventory() {
        return inv;
    }
    public TreeMap<Integer, ItemStack> getItems() {
        return items;
    }
}

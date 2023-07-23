package me.burb.burbkits.api.events;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class KitItemsChangeEvent extends KitEvent implements Cancellable {
    private final TreeMap<Integer, ItemStack> oldItems;
    private final TreeMap<Integer, ItemStack> newItems;
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

    public KitItemsChangeEvent(Kit kit, TreeMap<Integer, ItemStack> oldItems, TreeMap<Integer, ItemStack> newItems) {
        super(kit);
        this.oldItems = oldItems;
        this.newItems = newItems;
    }
    public TreeMap<Integer, ItemStack> getOldItems() {
        return oldItems;
    }
    public TreeMap<Integer, ItemStack> getNewItems() {
        return newItems;
    }
}

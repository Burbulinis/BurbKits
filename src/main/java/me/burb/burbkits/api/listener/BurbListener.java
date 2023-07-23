package me.burb.burbkits.api.listener;

import me.burb.burbkits.api.kits.Kit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class BurbListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (Kit.getInventories() != null && Kit.getInventories().contains(e.getClickedInventory())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (Kit.getInventories() != null && Kit.getInventories().contains(e.getInventory())) {
            e.setCancelled(true);
        }
    }
}

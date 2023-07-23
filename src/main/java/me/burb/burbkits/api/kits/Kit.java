package me.burb.burbkits.api.kits;

import me.burb.burbkits.BurbKits;
import me.burb.burbkits.api.events.*;
import me.burb.burbkits.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN;

@SuppressWarnings("deprecation")
public class Kit {
    private static final HashMap<String, Kit> ALL_KITS = new HashMap<>();
    private static final List<Kit> KITS = new ArrayList<>();
    private static final List<String> KIT_NAMES = new ArrayList<>();
    private static final List<Inventory> KIT_INVENTORIES = new ArrayList<>();
    private TreeMap<Integer, ItemStack> items = new TreeMap<>();
    private Inventory inventory;
    private long cooldown;
    private String permission;
    private String name;

    /**
     * Claim the kit
     * @param player The player that gets the kit
     */
    public void claimKit(Player player) {
        boolean success = !hasCooldown(player);
        if (!success) { success = hasPermission(player); }
        PlayerInventory inv = player.getInventory();
        Set<Integer> keys = items.keySet();
        KitClaimAttemptEvent attemptEvent = new KitClaimAttemptEvent(this, player, success);
        BurbKits.getPluginManager().callEvent(attemptEvent);
        if (!attemptEvent.isCancelled()) {
            if (success) {
                for (int key : keys) {
                    if (inv.getItem(key) == null) {
                        inv.setItem(key, items.get(key));
                    } else {
                        HashMap<Integer, ItemStack> items = inv.addItem(this.items.get(key));
                        if (items.size() != 0) {
                            player.getWorld().dropItem(player.getLocation(), this.items.get(key));
                        }
                    }
                }
                player.sendMessage(Utils.color("&aSuccessfully claimed the Kit '" + name + "'"));
                player.playSound(player, ITEM_ARMOR_EQUIP_CHAIN, 1, 0);
                BurbKits.getPluginManager().callEvent(new KitClaimEvent(this, player));
            } else if (!hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou don't have permission!"));
            } else if (hasCooldown(player)) {
                long time = BurbKits.cooldownsConfig.getLong("cooldowns."+name+"."+player.getUniqueId()+".cooldown");
                Calendar calendar = Calendar.getInstance();
                Calendar otherCalendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                otherCalendar.setTimeInMillis(System.currentTimeMillis());
                long leTime = otherCalendar.compareTo(calendar);
                String timeString = "&cYou can only claim this kit in&e " + Utils.millisToString(leTime) + "&c!";
                player.sendMessage(Utils.color(timeString));
            }
        }
    }



    /**
     * Opens an inventory with the Kit items
     * @param player The player to open the inventory to
     */
    public void seeItems(Player player) {
        Utils.fillAllSlots(inventory, BLACK_STAINED_GLASS_PANE);
        Set<Integer> keys = items.keySet();
        for (int i : keys) {
            if (i <= 8) {
                inventory.setItem(i + 36, items.get(i));
            } else if (i <= 35) {
                inventory.setItem(i - 9, items.get(i));
            } else if (i <= 39) {
                inventory.setItem(i + 9, items.get(i));
            } else if (i == 40) {
                inventory.setItem(49, items.get(i));
            }
        }
        BurbKits.getPluginManager().callEvent(new KitViewItemsEvent(this, player, inventory, items));
        player.openInventory(inventory);
    }


    /**
     * Delete a kit
     */
    public void deleteKit() {
        BurbKits.getPluginManager().callEvent(new KitDeleteEvent(this));
        BurbKits.kitsConfig.set("kits."+name, null);
        BurbKits.cooldownsConfig.set("cooldowns."+name, null);
        KIT_NAMES.remove(name);
        ALL_KITS.remove(name);
        KITS.remove(this);
        KIT_INVENTORIES.remove(inventory);
        items.clear();
        name = null;
        inventory = null;
    }

    /**
     * Set the items of the Kit
     * @param items Items
     */
    public void setItems(TreeMap<Integer, ItemStack> items) {
        BurbKits.getPluginManager().callEvent(new KitItemsChangeEvent(this, this.items, items));
        for (int i = 0; i <= 40; i++) {
            if (items.get(i) == null) {
                items.remove(i);
            }
        }
        this.items = items;
        BurbKits.kitsConfig.set("kits." + getName() + ".items", this.items);
    }

    /**
     * Set the permission of the Kit
     * @param perm Permission
     */
    public void setPermission(String perm) {
        BurbKits.getPluginManager().callEvent(new KitPermissionChangeEvent(this, permission, perm));
        permission = perm;
        BurbKits.kitsConfig.set("kits." + getName() + ".permission", perm);
    }

    /**
     * Set the cooldown of the Kit
     * @param cooldown Cooldown
     */
    public void setCooldown(long cooldown) {
        BurbKits.getPluginManager().callEvent(new KitCooldownChangeEvent(this, this.cooldown, cooldown));
        this.cooldown = cooldown;
        BurbKits.kitsConfig.set("kits." + getName() + ".cooldown", this.cooldown);
    }

    /**
     * @return List of the existing kits
     */
    public static List<Kit> getKits() {
        return KITS;
    }

    /**
     * @return List of the existing kits' names, null if none exist
     */
    public static List<String> getNames() {
        return KIT_NAMES;
    }

    /**
     * @return List of all the kit inventories
     */
    public static List<Inventory> getInventories() {
        return KIT_INVENTORIES;
    }

    /**
     * Returns the kit from the given name
     * @param name Name of the kit
     * @return The kit from the name if it exists
     */
    public @Nullable static Kit getKitFromName(String name) {
        return ALL_KITS.get(name);
    }

    /**
     * @return The name of the kit
     */
    public String getName() {
        return name;
    }

    /**
     * @return Permission of the Kit
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return Cool down of the Kit
     */
    public long getCooldown() {
        return cooldown;
    }

    /**
     * @return Kit inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return The items of the Kit
     */
    public TreeMap<Integer, ItemStack> getItems() {
        return items;
    }

    public boolean hasCooldown(Player player) {
        long time = BurbKits.cooldownsConfig.getLong("cooldowns." + name + "." + player.getUniqueId() + ".cooldown");
        if (cooldown != 0) {
            if (time != 0) {
                Calendar calendar = Calendar.getInstance();
                Calendar otherCalendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                otherCalendar.setTimeInMillis(System.currentTimeMillis());
                if (calendar.after(otherCalendar)) {
                    return false;
                } else { return true; }
            }
        }
        return false;
    }

    public boolean hasPermission(Player player) {
        if (permission != null) { return player.hasPermission(permission); }
        else { return false; }
    }

    /**
     * Create a new Kit
     * @param name Name of the Kit
     */
    public Kit(String name) {
        if (ALL_KITS.get(name) != null) {
            throw new RuntimeException("Kit '" + name + "' already exists");
        }
        this.inventory = Bukkit.createInventory(null, 54, Utils.color("&0Kit '" + name + "'"));
        KIT_INVENTORIES.add(this.inventory);
        KIT_NAMES.add(name);
        this.name = name;
        ALL_KITS.put(name, this);
        cooldown = 0;
        BurbKits.kitsConfig.createSection("kits." + name);
        BurbKits.getPluginManager().callEvent(new KitCreateEvent(this));
    }
}

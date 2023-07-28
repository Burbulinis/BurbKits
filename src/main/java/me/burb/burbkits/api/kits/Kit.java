package me.burb.burbkits.api.kits;

import me.burb.burbkits.BurbKits;
import me.burb.burbkits.api.events.*;
import me.burb.burbkits.api.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.burb.burbkits.BurbKits.kitsConfig;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN;

@SuppressWarnings({"deprecation", "unused"})
public class Kit {

    private static final HashMap<String, Kit> ALL_KITS = new HashMap<>();
    private final HashMap<OfflinePlayer, Long> cooldowns = new HashMap<>();
    private static final List<Kit> KITS = new ArrayList<>();
    private static final List<String> KIT_NAMES = new ArrayList<>();
    private static final List<Inventory> KIT_INVENTORIES = new ArrayList<>();
    private final TreeMap<Integer, ItemStack> items = new TreeMap<>();
    private Inventory inventory;
    private long cooldown;
    private String permission;
    private String cooldownBypassPermission;
    private String name;

    /**
     * Claim the kit
     * @param player The player that gets the kit
     * @return The success, ie if they could claim the kit, or not
     */
    public boolean claimKit(Player player) {
        boolean success;
        success = hasPermission(player) && !hasCooldown(player) && player.getInventory().firstEmpty() != -1;
        KitClaimAttemptEvent attemptEvent = new KitClaimAttemptEvent(this, player, success);
        BurbKits.getPluginManager().callEvent(attemptEvent);
        if (!attemptEvent.isCancelled()) {
            if (success) {
                TreeMap<Integer, ItemStack> clonedItems = new TreeMap<>();
                items.keySet().forEach(key -> clonedItems.put(key, items.get(key).clone()));
                PlayerInventory inv = player.getInventory();
                for (int key : clonedItems.keySet()) {
                    if (inv.getItem(key) == null) {
                        inv.setItem(key, clonedItems.get(key));
                    } else {
                        HashMap<Integer, ItemStack> notAddedItems = inv.addItem(clonedItems.get(key));
                        if (!notAddedItems.isEmpty()) {
                            player.getWorld().dropItem(player.getLocation(), notAddedItems.get(0));
                            notAddedItems.clear();
                        }
                    }
                }
                player.sendMessage(Utils.color("&aSuccessfully claimed the Kit '" + name + "'"));
                player.playSound(player, ITEM_ARMOR_EQUIP_CHAIN, 1, 0);
                BurbKits.getPluginManager().callEvent(new KitClaimEvent(this, player));
                if (cooldown != 0) { setPlayerCooldown(System.currentTimeMillis() + cooldown, player); }
            } else if (!hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou don't have permission!"));
            } else if (hasCooldown(player)) {
                String timeString = "&cYou can only claim this kit in&e " + getPlayerCooldownDifferenceBetweenNowAsString(player) + "&c!";
                player.sendMessage(Utils.color(timeString));
            } else if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(Utils.color("&cYour inventory is full!"));
            }
        }
        return success;
    }



    /**
     * Opens an inventory with the Kit items
     * @param player The player to open the inventory to
     */
    public void seeItems(Player player) {
        KitViewItemsEvent event = new KitViewItemsEvent(this, player, inventory, items);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
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
            player.openInventory(inventory);
        }
    }


    /**
     * Delete a kit
     */
    public void deleteKit() {
        KitDeleteEvent event = new KitDeleteEvent(this);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            kitsConfig.set("kits."+name, null);
            BurbKits.cooldownsConfig.set("cooldowns."+name, null);
            KIT_NAMES.remove(name);
            ALL_KITS.remove(name);
            KITS.remove(this);
            KIT_INVENTORIES.remove(inventory);
            items.clear();
            name = null;
            inventory = null;
        }
    }

    /**
     * Set the items of the Kit
     * @param items Items
     */
    public void setItems(TreeMap<Integer, ItemStack> items) {
        KitItemsChangeEvent event = new KitItemsChangeEvent(this, this.items, items);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.items.clear();
            for (int i = 0; i <= 40; i++) {
                if (items.get(i) == null) {
                    items.remove(i);
                } else {
                    this.items.put(i, items.get(i).clone());
                }
            }
            kitsConfig.set("kits." + getName() + ".items", this.items);
        }
    }

    /**
     * Set the permission of the Kit
     * @param perm Permission
     */
    public void setPermission(String perm) {
        KitPermissionChangeEvent event = new KitPermissionChangeEvent(this, permission, perm);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            permission = perm;
            kitsConfig.set("kits." + getName() + ".permission", perm);
        }
    }

    /**
     * Set the cooldown of the Kit
     * @param cooldown Cooldown
     */
    public void setKitCooldown(long cooldown) {
        KitCooldownChangeEvent event = new KitCooldownChangeEvent(this, this.cooldown, cooldown);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.cooldown = cooldown;
            kitsConfig.set("kits." + getName() + ".cooldown", this.cooldown);
        }
    }

    /**
     * @param millis The millis have to be formatted as UTC milliseconds from the epoch. You can use the resetCooldown method to reset the cooldown.
     * @param player The player that should receive the cooldown
     */
    public void setPlayerCooldown(long millis, OfflinePlayer player) {
        KitPlayerCooldownChangeEvent event = new KitPlayerCooldownChangeEvent(this, player, cooldowns.get(player) == null ? 0 : cooldowns.get(player), millis);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldowns.put(player, millis);
            BurbKits.cooldownsConfig.set("cooldowns."+name+"."+player.getUniqueId()+".cooldown", millis);
        }
    }

    public void setCooldownBypassPermission(String perm) {
        cooldownBypassPermission = perm;
        BurbKits.kitsConfig.set("kits."+name+".cooldownBypass", perm);
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
     * @param player The player to check the cooldown of
     * @return Returns the time when the cooldown should be over for the specified Kit as UTC milliseconds from the epoch
     */
    public long getPlayerCooldownAsMillis(OfflinePlayer player) {
        return cooldowns.get(player);
    }

    /**
     * @param player The player to check the cooldown of
     * @return Returns the time when the cooldown should be over for the specified Kit as a date
     */
    public @Nullable Date getPlayerCooldownAsDate(OfflinePlayer player) {
        Calendar calendar = Calendar.getInstance();
        if (cooldowns.get(player) != null) { calendar.setTimeInMillis(cooldowns.get(player)); }
        return calendar.getTime();
    }

    /**
     * @param player The player to check for
     * @return The difference between now and the cooldown, ie how much time left till they are able to claim in a String
     */
    public @Nullable String getPlayerCooldownDifferenceBetweenNowAsString(OfflinePlayer player) {
        if (cooldown != 0 && cooldowns.get(player) != null) {
            long time = cooldowns.get(player);
            Calendar calendar = Calendar.getInstance();
            Calendar otherCalendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            otherCalendar.setTimeInMillis(System.currentTimeMillis());
            return (calendar.getTimeInMillis() <= otherCalendar.getTimeInMillis()) ? null : Utils.millisToString((calendar.getTimeInMillis() - System.currentTimeMillis()));
        }
        return null;
    }

    /**
     * @param player The player to check for
     * @return The difference between now and the cooldown, ie how much time left till they are able to claim in milliseconds, 0 if no cooldown
     */
    public long getPlayerCooldownDifferenceBetweenNowAsMillis(OfflinePlayer player) {
        if (cooldown != 0 && cooldowns.get(player) != null) {
            long time = cooldowns.get(player);
            return (time < System.currentTimeMillis()) ? 0 : (time - System.currentTimeMillis());
        }
        return 0;
    }

    /**
     * @param player Player to check for
     * @return true/false if they have the cooldown, true if permission doesn't exist
     */
    public boolean hasCooldown(Player player) {
        if (cooldown != 0 && cooldowns.get(player) != null) {
            long time = cooldowns.get(player);
            if (cooldownBypassPermission != null && player.hasPermission(cooldownBypassPermission)) { return false; }
            return !(time < System.currentTimeMillis());
        }
        return false;
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
    public long getKitCooldown() {
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

    /**
     * @param player Player to check for
     * @return true/false if player has permission
     */
    public boolean hasPermission(Player player) {
        if (permission != null) { return player.hasPermission(permission); }
        else { return true; }
    }

    public void resetCooldown(Player player) {
        cooldowns.put(player, 0L);
        BurbKits.cooldownsConfig.set("cooldowns."+name+"."+player.getUniqueId()+".cooldown", null);
    }

    /**
     * Create a new Kit
     * @param name Name of the Kit
     */
    public Kit(String name) {
        KitCreateEvent event = new KitCreateEvent(this);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (ALL_KITS.get(name) != null) {
                throw new RuntimeException("Kit '" + name + "' already exists");
            }
            this.inventory = Bukkit.createInventory(null, 54, Utils.color("&0Kit '" + name + "'"));
            KIT_INVENTORIES.add(this.inventory);
            KIT_NAMES.add(name);
            this.name = name;
            ALL_KITS.put(name, this);
            kitsConfig.createSection("kits." + name);
        }
    }
}

package me.burb.burbkits.api.kits;

import me.burb.burbkits.BurbKits;
import me.burb.burbkits.api.events.*;
import me.burb.burbkits.api.utils.Utils;
import me.burb.burbkits.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.util.*;

import static me.burb.burbkits.BurbKits.cooldownsConfig;
import static me.burb.burbkits.BurbKits.kitsConfig;
import static org.bukkit.Material.BLACK_STAINED_GLASS_PANE;
import static org.bukkit.Sound.ITEM_ARMOR_EQUIP_CHAIN;

@SuppressWarnings({"deprecation", "unused"})
public class Kit {

    public static final HashMap<String, Kit> ALL_KITS = new HashMap<>();
    private static final List<Inventory> KIT_INVENTORIES = new ArrayList<>();
    private final TreeMap<Integer, ItemStack> items = new TreeMap<>();
    private final HashMap<UUID, Timestamp> cooldowns = new HashMap<>();
    private Inventory inventory;
    private long cooldown;
    private String permission;
    private String cooldownBypassPermission;
    private String name;

    // Custom messages
    private final Config config = BurbKits.getBurbConfig();
    private final String PREFIX = "&7[&6Burb&eKits&7]&r ";
    private final String NO_COOLDOWN = config.NO_COOLDOWN == null ? config.DEFAULT_NO_COOLDOWN : config.NO_COOLDOWN;
    private final String FULL_INVENTORY = config.FULL_INVENTORY == null ? config.DEFAULT_FULL_INVENTORY : config.FULL_INVENTORY;
    private final String NO_PERMISSION = config.NO_PERMISSION == null ? config.DEFAULT_NO_PERMISSION : config.NO_PERMISSION;
    private final String CLAIM_KIT = config.CLAIM_KIT == null ? config.DEFAULT_CLAIM_KIT : config.CLAIM_KIT;

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
            this.name = name;
            ALL_KITS.put(name, this);
            if (!kitsConfig.contains("kits."+name)) { kitsConfig.createSection("kits." + name); }
        }
    }

    /**
     * Claim the kit
     * @param player The player that gets the kit
     * @param sendMessages true/false to send no permission messages, etc
     * @return The success, ie if they could claim the kit, or not
     */
    public boolean claimKit(Player player, boolean sendMessages) {
        boolean success = hasPermission(player) && !hasCooldown(player) && player.getInventory().firstEmpty() != -1;
        KitClaimAttemptEvent attemptEvent = new KitClaimAttemptEvent(this, player, success);
        BurbKits.getPluginManager().callEvent(attemptEvent);
        if (!attemptEvent.isCancelled()) {
            if (success) {
                TreeMap<Integer, ItemStack> items = getItems();
                items.keySet().forEach(key -> items.put(key, items.get(key).clone()));
                PlayerInventory inv = player.getInventory();
                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    Integer key = entry.getKey();
                    ItemStack value = entry.getValue();
                    if (inv.getItem(key) == null) {
                        inv.setItem(key, value);
                    } else {
                        HashMap<Integer, ItemStack> notAddedItems = inv.addItem(value);
                        if (!notAddedItems.isEmpty()) {
                            player.getWorld().dropItem(player.getLocation(), notAddedItems.get(0));
                        }
                    }
                }
                if (sendMessages) {
                    String message = CLAIM_KIT.replaceAll("%kit%", name);
                    player.sendMessage(Utils.color(PREFIX + message));
                    player.playSound(player, ITEM_ARMOR_EQUIP_CHAIN, 1, 0);
                }
                BurbKits.getPluginManager().callEvent(new KitClaimEvent(this, player));
                if (cooldown != 0) { setPlayerCooldown(new Timestamp(System.currentTimeMillis() + cooldown), player); }
            } else if (!hasPermission(player) && sendMessages) {
                player.sendMessage(Utils.color(PREFIX + NO_PERMISSION));
            } else if (hasCooldown(player) && sendMessages) {
                String message = NO_COOLDOWN.replaceAll("%cooldown%", getPlayerCooldownDifferenceBetweenNowAsString(player));
                player.sendMessage(Utils.color(PREFIX + message));
            } else if (player.getInventory().firstEmpty() == -1 && sendMessages) {
                player.sendMessage(Utils.color(PREFIX + FULL_INVENTORY));
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
            kitsConfig.set("kits." + name + ".items", this.items);
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
            cooldownsConfig.set("cooldowns."+name, null);
            ALL_KITS.remove(name);
            KIT_INVENTORIES.remove(inventory);
            items.clear();
            name = null;
            inventory = null;
            permission = null;
            cooldownBypassPermission = null;
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
            kitsConfig.set("kits." + name + ".permission", perm);
        }
    }

    /**
     * Set the cooldown of the Kit
     * @param cooldown Cooldown in millis
     */
    public void setKitCooldown(long cooldown) {
        KitCooldownChangeEvent event = new KitCooldownChangeEvent(this, this.cooldown, cooldown);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.cooldown = cooldown;
            kitsConfig.set("kits." + name + ".cooldown", cooldown);
        }
    }

    /**
     * @param timestamp The timestamp when the cooldown should end
     * @param player The player that should receive the cooldown
     */
    public void setPlayerCooldown(Timestamp timestamp, OfflinePlayer player) {
        KitPlayerCooldownChangeEvent event = new KitPlayerCooldownChangeEvent(this, player, cooldowns.get(player.getUniqueId()), timestamp);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldowns.put(player.getUniqueId(), timestamp);
            cooldownsConfig.set("cooldowns."+name+"."+player.getUniqueId()+".cooldown", timestamp);
        }
    }

    /**
     * Reset the cooldown of a player
     * @param player The player to reset the cooldown of
     */
    public void resetCooldown(OfflinePlayer player) {
        KitPlayerCooldownChangeEvent event = new KitPlayerCooldownChangeEvent(this, player, cooldowns.get(player.getUniqueId()), null);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldowns.put(player.getUniqueId(), null);
            cooldownsConfig.set("cooldowns."+name+"."+player.getUniqueId()+".cooldown", null);
        }
    }

    public void setCooldownBypassPermission(String perm) {
        KitCooldownBypassChangeEvent event = new KitCooldownBypassChangeEvent(this, this.cooldownBypassPermission, perm);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldownBypassPermission = perm;
            BurbKits.kitsConfig.set("kits."+name+".cooldownBypass", perm);
        }
    }

    public void setName(String s) {
        ALL_KITS.remove(name);
        ALL_KITS.put(s, this);
        kitsConfig.set("kits."+name, null);
        cooldownsConfig.set("cooldowns."+name, null);
        kitsConfig.createSection("kits."+s);
        cooldownsConfig.createSection("cooldowns."+s);
        kitsConfig.set("kits."+s+".items", items);
        kitsConfig.set("kits."+s+".permission", permission);
        kitsConfig.set("kits."+s+".cooldownBypass", cooldownBypassPermission);
        kitsConfig.set("kits."+s+".permission", permission);
        for (Map.Entry<UUID, Timestamp> entry : cooldowns.entrySet()) {
            cooldownsConfig.set("cooldowns."+s+"."+entry.getKey()+".cooldown", entry.getValue());
        }
        name = s;
    }

    /**
     * Removes the cooldown bypass permission of the kit if the permission exists
     */
    public void removeCooldownBypass() {
        KitCooldownBypassChangeEvent event = new KitCooldownBypassChangeEvent(this, this.cooldownBypassPermission, null);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldownBypassPermission = null;
            kitsConfig.set("kits."+name+".cooldownBypass", null);
        }
    }

    public void removeCooldown() {
        KitCooldownChangeEvent event = new KitCooldownChangeEvent(this, this.cooldown, 0);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cooldown = 0;
            kitsConfig.set("kits."+name+".cooldown", null);
        }
    }

    /**
     * Remove the permission of the kit
     */
    public void removePermission() {
        KitPermissionChangeEvent event = new KitPermissionChangeEvent(this, permission, null);
        BurbKits.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            permission = null;
            kitsConfig.set("kits."+name+".permission", null);
        }
    }

    /**
     * @param player Player to check for
     * @return true/false if player has permission
     */
    public boolean hasPermission(Player player) {
        if (permission != null) { return player.hasPermission(permission); }
        else { return true; }
    }

    /**
     * @param player Player to check for
     * @return true/false if player has cooldown bypass permission
     */
    public boolean hasCooldownBypassPermission(Player player) {
        if (cooldownBypassPermission != null) return player.hasPermission(cooldownBypassPermission);
        else return true;
    }

    /**
     * @param player Player to check for
     * @return true/false if they have the cooldown, true if permission doesn't exist
     */
    public boolean hasCooldown(Player player) {
        if (cooldown != 0 && cooldowns.get(player.getUniqueId()) != null) {
            Timestamp time = cooldowns.get(player.getUniqueId());
            if (cooldownBypassPermission != null && player.hasPermission(cooldownBypassPermission)) { return false; }
            return time.after(new Timestamp(System.currentTimeMillis()));
        }
        return false;
    }

    /**
     * @return true/false depending on if this kit has a cooldown bypass permission
     */
    public boolean cooldownBypassExists() {
        return cooldownBypassPermission != null;
    }

    /**
     * @return true/false depending on if this kit has a permission
     */
    public boolean permissionExists() {
        return permission != null;
    }

    /**
     * @return List of the existing kits
     */
    public static List<Kit> getKits() {
        return new ArrayList<>(ALL_KITS.values());
    }

    /**
     * @return List of the existing kits' names
     */
    public static List<String> getNames() {
        return new ArrayList<>(ALL_KITS.keySet());
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
     * @return Returns the timestamp of the cooldown
     */
    public Timestamp getPlayerCooldown(OfflinePlayer player) {
        return cooldowns.get(player.getUniqueId());
    }

    /**
     * @param player The player to check for
     * @return The difference between now and the cooldown, ie how much time left till they are able to claim in a String
     */
    public @Nullable String getPlayerCooldownDifferenceBetweenNowAsString(OfflinePlayer player) {
        if (cooldown != 0 && cooldowns.get(player.getUniqueId()) != null) {
            long millis = cooldowns.get(player.getUniqueId()).getTime();
            long now = System.currentTimeMillis();
            return (millis <= now) ? null : Utils.millisToString((millis - now));
        }
        return null;
    }

    /**
     * @param player The player to check for
     * @return The difference between now and the cooldown, ie how much time left till they are able to claim in milliseconds, 0 if no cooldown
     */
    public long getPlayerCooldownDifferenceBetweenNowAsMillis(OfflinePlayer player) {
        Timestamp time = cooldowns.get(player.getUniqueId());
        if (cooldown != 0 && time != null) {
            return (time.before(new Timestamp(System.currentTimeMillis()))) ? 0 : (time.getTime() - System.currentTimeMillis());
        }
        return 0;
    }

    public String toString() {
        return name;
    }

    /**
     * @return Permission of the Kit
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @return Cooldown of the Kit
     */
    public long getKitCooldown() {
        return cooldown;
    }

    /**
     * @return Cooldown bypass permission of the kit
     */
    public String getCooldownBypassPermission() {
        return cooldownBypassPermission;
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
        TreeMap<Integer, ItemStack> items = new TreeMap<>();
        this.items.forEach((key, value) -> items.put(key, value.clone()));
        return items;
    }
}
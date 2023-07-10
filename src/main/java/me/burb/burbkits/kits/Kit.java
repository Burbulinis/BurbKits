package me.burb.burbkits.kits;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Kit {
    private static HashMap<String, Kit> kitFromName = new HashMap<>();
    private static List<Kit> kits = new ArrayList<>();
    private List<ItemStack> kitHotbarItems = new ArrayList<>();
    private List<ItemStack> kitItems = new ArrayList<>();
    private List<ItemStack> kitArmorContents;
    private ItemStack kitOffHand;
    private String kitName;
    private PlayerInventory kitInventory;

    /**
     * Returns the kit from the given name
     * @param name Name of the kit
     * @return The kit from the name if it exists
     */
    public @Nullable static Kit getKitFromName(String name) {
        return kitFromName.get(name);
    }

    /**
     * @return All the existing kits
     */
    public static List<Kit> getKits() {
        return kits;
    }

    /**
     * @return Player Inventory of the kit
     */
    public PlayerInventory getKitInventory() {
        return kitInventory;
    }

    /**
     * @return The name of the kit
     */
    public String getKitName() {
        return kitName;
    }

    /**
     * @return Armor contents of the kit
     */
    public List<ItemStack> getKitArmorContents() {
        return kitArmorContents;
    }

    /**
     * @return Slots 1 to 9 of the inventory of the kit
     */
    public List<ItemStack> getKitHotbarItems() {
        return kitHotbarItems;
    }

    /**
     * @return Slots 9 to 35 of the inventory
     */
    public List<ItemStack> getKitItems() {
        return kitItems;
    }

    /**
     * @return OffHand of the inventory of the kit
     */
    public ItemStack getKitOffHand() {
        return kitOffHand;
    }
    public void overrideKitItems(PlayerInventory inv) {
        kitInventory = inv;
        kitArmorContents = List.of(kitInventory.getArmorContents());
        kitOffHand = kitInventory.getItemInOffHand();
        for (int i = 0; i < 35; i++) {
            if (i >= 0 && i < 8) {
                kitHotbarItems.add(kitInventory.getItem(i));
            } else {
                kitItems.add(kitInventory.getItem(i));
            }
        }
    }

    public Kit(PlayerInventory kitInventory, String kitName) {
        if (kitFromName.get(kitName) != null) {
            throw new RuntimeException("Kit " + kitName + " already exists");
        }
        this.kitName = kitName;
        this.kitInventory = kitInventory;
        kitArmorContents = List.of(kitInventory.getArmorContents());
        kitOffHand = kitInventory.getItemInOffHand();
        for (int i = 0; i < 35; i++) {
            if (i >= 0 && i < 8) {
                kitHotbarItems.add(kitInventory.getItem(i));
            } else {
                kitItems.add(kitInventory.getItem(i));
            }
        }
    }
}

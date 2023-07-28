package me.burb.burbkits;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.burb.burbkits.api.Metrics;
import me.burb.burbkits.api.commands.CommandKits;
import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.api.listener.BurbListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class BurbKits extends JavaPlugin {
    SkriptAddon addon;
    public static YamlConfiguration kitsConfig;
    public static YamlConfiguration cooldownsConfig;
    private static Plugin instance;
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getServer().getPluginManager();

    public static Plugin getInstance() {
        return instance;
    }

    public static PluginManager getPluginManager() {
        return PLUGIN_MANAGER;
    }

    @Override
    public void onEnable() {

        int pluginId = 19241;
        Metrics metrics = new Metrics(this, pluginId);

        File kitsFile = new File(getDataFolder(), "kits.yml");
        File cooldownsFile = new File(getDataFolder(), "cooldowns.yml");

        if (!cooldownsFile.exists()) {
            try {
                getDataFolder().mkdir();
                cooldownsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!kitsFile.exists()) {
            try {
                getDataFolder().mkdir();
                kitsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        kitsConfig = new YamlConfiguration().loadConfiguration(kitsFile);
        cooldownsConfig = new YamlConfiguration().loadConfiguration(cooldownsFile);

        if (kitsConfig.contains("kits")) {
            TreeMap<Integer, ItemStack> items = new TreeMap<>();
            Set<String> keys = kitsConfig.getConfigurationSection("kits").getKeys(false);
            for (String key : keys) {
                Set<String> slots = kitsConfig.getConfigurationSection("kits."+key+".items").getKeys(false);
                if (kitsConfig.contains("kits."+key+".items")) {
                    for (String slot : slots) {
                        try {
                            items.put(Integer.parseInt(slot), kitsConfig.getItemStack("kits."+key+".items."+slot));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String permission = null;
                String cooldownBypass = null;
                long cooldown = 0;
                if (kitsConfig.contains("kits."+key+".permission")) {
                    permission = kitsConfig.getString("kits."+key+".permission");
                } else if (kitsConfig.contains("kits."+key+".cooldown")) {
                    cooldown = kitsConfig.getLong("kits."+key+".cooldown");
                } else if (kitsConfig.contains("kits."+key+".cooldownBypass")) {
                    cooldownBypass = kitsConfig.getString("kits."+key+".cooldownBypass");
                }
                Kit kit = new Kit(key);
                kit.setItems(items);
                kit.setPermission(permission);
                kit.setCooldownBypassPermission(cooldownBypass);
                kit.setKitCooldown(cooldown);
                if (cooldownsConfig.contains("cooldowns."+key)) {
                    Set<String> UUIDs = cooldownsConfig.getConfigurationSection("cooldowns."+key).getKeys(false);
                    getLogger().info(UUIDs.toString());
                    for (String uuid : UUIDs) {
                        long millis = cooldownsConfig.getLong("cooldowns."+key+"."+uuid+".cooldown");
                        kit.setPlayerCooldown(millis, Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                    }
                }
            }
        }

        instance = this;
        getCommand("kits").setExecutor(new CommandKits());
        PLUGIN_MANAGER.registerEvents(new BurbListener(), this);
    }

    @Override
    public void onDisable() {
        File kitsFile = new File(getDataFolder(), "kits.yml");
        File cooldownsFile = new File(getDataFolder(), "cooldowns.yml");
        try {
            kitsConfig.save(kitsFile);
            cooldownsConfig.save(cooldownsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
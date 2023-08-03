package me.burb.burbkits;

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

    public static YamlConfiguration kitsConfig, cooldownsConfig;
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
            Set<String> kits = kitsConfig.getConfigurationSection("kits").getKeys(false);
            for (String kit : kits) {
                Set<String> slots = kitsConfig.getConfigurationSection("kits."+kit+".items").getKeys(false);
                if (kitsConfig.isSet("kits."+kit+".items")) {
                    for (String slot : slots) {
                        try {
                            items.put(Integer.parseInt(slot), kitsConfig.getItemStack("kits."+kit+".items."+slot));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Kit newKit = new Kit(kit);
                newKit.setItems(items);
                if (kitsConfig.isSet("kits."+kit+".permission")) {
                    String permission = kitsConfig.getString("kits."+kit+".permission");
                    newKit.setPermission(permission);
                } if (kitsConfig.isSet("kits."+kit+".cooldown")) {
                    long cooldown = kitsConfig.getLong("kits."+kit+".cooldown");
                    newKit.setKitCooldown(cooldown);
                } if (kitsConfig.isSet("kits."+kit+".cooldownBypass")) {
                    String cooldownBypass = kitsConfig.getString("kits."+kit+".cooldownBypass");
                    newKit.setCooldownBypassPermission(cooldownBypass);
                }
                if (cooldownsConfig.contains("cooldowns")) {
                    Set<String> UUIDs = cooldownsConfig.getConfigurationSection("cooldowns."+kit).getKeys(false);
                    for (String uuid : UUIDs) {
                        long millis = cooldownsConfig.getLong("cooldowns."+kit+"."+uuid+".cooldown");
                        newKit.setPlayerCooldown(millis, Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
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
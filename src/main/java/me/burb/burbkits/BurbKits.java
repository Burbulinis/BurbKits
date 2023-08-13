package me.burb.burbkits;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.burb.burbkits.api.Metrics;
import me.burb.burbkits.api.commands.CommandKits;
import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.api.listener.BurbListener;
import me.burb.burbkits.api.utils.Utils;
import me.burb.burbkits.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class BurbKits extends JavaPlugin {

    SkriptAddon addon;
    private static Config config;
    public static YamlConfiguration kitsConfig, cooldownsConfig;
    private static Plugin instance;
    private static final PluginManager PLUGIN_MANAGER = Bukkit.getServer().getPluginManager();

    public static Plugin getInstance() {
        return instance;
    }

    public static PluginManager getPluginManager() {
        return PLUGIN_MANAGER;
    }

    public static Config getBurbConfig() { return config; }

    @Override
    public void onEnable() {

        long start = System.currentTimeMillis();
        instance = this;

        int pluginId = 19241;
        new Metrics(this, pluginId);

        String version = getDescription().getVersion();
        if (version.contains("-")) {
            Utils.log("&eThis is a BETA build, this build may contain issues, please report any bugs on GitHub");
            Utils.log("&ehttps://github.com/Burbulinis/BurbKits/issues");
        }

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

        config = new Config(this);

        kitsConfig = new YamlConfiguration().loadConfiguration(kitsFile);
        cooldownsConfig = new YamlConfiguration().loadConfiguration(cooldownsFile);

        loadKits();
        loadCommands();

        if (getServer().getPluginManager().getPlugin("Skript") != null) {
            addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("me.burb.burbkits.skript", "elements");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Utils.log("&eSuccessfully enabled v%s&7 in &b%.2f seconds", version, (float) (System.currentTimeMillis() - start) / 1000);

    }

    private void loadCommands() {
        getCommand("kits").setExecutor(new CommandKits());
        PLUGIN_MANAGER.registerEvents(new BurbListener(), this);
    }

    private void loadKits() {
        long start = System.currentTimeMillis();
        int kitSize = kitsConfig.contains("kits") ? kitsConfig.getConfigurationSection("kits").getKeys(false).size() : 0;

        if (kitSize > 0) Utils.logLoading("&7Loading %o kits...", kitSize);

        if (kitsConfig.contains("kits")) {
            TreeMap<Integer, ItemStack> items = new TreeMap<>();
            Set<String> kits = kitsConfig.getConfigurationSection("kits").getKeys(false);

            for (String kit : kits) {
                Set<String> slots = kitsConfig.getConfigurationSection("kits." + kit + ".items").getKeys(false);

                for (String slot : slots) {
                    try {
                        int i = Integer.parseInt(slot);
                        ItemStack item = kitsConfig.getItemStack("kits." + kit + ".items." + slot);
                        items.put(i, item);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                Kit newKit = new Kit(kit);
                newKit.setItems(items);

                if (kitsConfig.isSet("kits." + kit + ".permission")) {
                    String permission = kitsConfig.getString("kits." + kit + ".permission");
                    newKit.setPermission(permission);
                }

                if (kitsConfig.isSet("kits." + kit + ".cooldown")) {
                    long cooldown = kitsConfig.getLong("kits." + kit + ".cooldown");
                    newKit.setKitCooldown(cooldown);
                }

                if (kitsConfig.isSet("kits." + kit + ".cooldownBypass")) {
                    String cooldownBypass = kitsConfig.getString("kits." + kit + ".cooldownBypass");
                    newKit.setCooldownBypassPermission(cooldownBypass);
                }

                if (cooldownsConfig.contains("cooldowns")) {
                    if (cooldownsConfig.contains("cooldowns." + kit)) {
                        Set<String> UUIDs = cooldownsConfig.getConfigurationSection("cooldowns." + kit).getKeys(false);
                        for (String uuid : UUIDs) {

                            Date date = (Date) cooldownsConfig.get("cooldowns." + kit + "." + uuid + ".cooldown");
                            Timestamp cooldown = new Timestamp(date.getTime());

                            newKit.setPlayerCooldown(cooldown, Bukkit.getOfflinePlayer(uuid));
                        }
                    }
                }
            }
        }

        if (kitSize > 0) Utils.log("&eSuccessfully loaded %o kits&7 in &b%.2f seconds", kitSize, (float) (System.currentTimeMillis() - start) / 1000);
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
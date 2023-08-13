package me.burb.burbkits.config;

import me.burb.burbkits.BurbKits;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private final BurbKits plugin;
    private FileConfiguration config;
    private File configFile;

    public String NO_PERMISSION;
    public String NO_COOLDOWN;
    public String FULL_INVENTORY;
    public String RESET_COOLDOWN;
    public String CLAIM_KIT;
    public String PERMISSION;
    public String NON_EXISTING_KIT;
    public String OPENED_INVENTORY;

    // Default messages

    public final String DEFAULT_NO_PERMISSION = "&cYou don't have permission!";
    public final String DEFAULT_NO_COOLDOWN = "&cYou can only claim this kit in&e %cooldown%&c!";
    public final String DEFAULT_FULL_INVENTORY = "&cYour inventory is full!";
    public final String DEFAULT_RESET_COOLDOWN = "&aYour cooldown of the kit '%kit%' has been reset!";
    public final String DEFAULT_CLAIM_KIT = "&aSuccessfully claimed the kit '%kit%'";
    public final String DEFAULT_PERMISSION = "burbkits.managekits";
    public final String DEFAULT_NON_EXISTING_KIT = "&cKit named '%name%' does not exist";
    public final String DEFAULT_OPENED_INVENTORY = "&aSuccessfully opened the kit inventory of kit '%kit%'";


    public Config(BurbKits plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    private void loadConfigFile() {

        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        updateConfig();
        loadConfig();
    }

    private String getSetting(String setting) {
        return config.getString("burbkits." + setting);
    }

    private String getMessage(String message) {
        return config.getString("burbkits.messages." + message);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateConfig() {
        try {
            boolean updated = false;
            InputStream stream = plugin.getResource(configFile.getName());

            assert stream != null;

            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(is);

            for (String key : defaultConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.getString(key));
                    updated = true;
                }
            }

            for (String key : config.getConfigurationSection("").getKeys(true)) {
                if (!defaultConfig.contains(key)) {
                    config.set(key, null);
                    updated = true;
                }
            }
            if (updated)
                config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        PERMISSION = getSetting("permission");

        OPENED_INVENTORY = getMessage("OPENED_INVENTORY");
        NON_EXISTING_KIT = getMessage("NON_EXISTING_KIT");
        NO_COOLDOWN = getMessage("NO_COOLDOWN");
        NO_PERMISSION = getMessage("NO_PERMISSION");
        CLAIM_KIT = getMessage("CLAIM_KIT");
        RESET_COOLDOWN = getMessage("RESET_COOLDOWN");
        FULL_INVENTORY = getMessage("FULL_INVENTORY");

    }

}

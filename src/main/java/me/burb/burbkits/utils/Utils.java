package me.burb.burbkits.utils;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class Utils {
    public static String color(String s) {
        return translateAlternateColorCodes('&', s);
    }
}

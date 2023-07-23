package me.burb.burbkits.api.utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings("deprecation")
public class Utils {
    public static String color(String s) {
        return translateAlternateColorCodes('&', s);
    }

    public static void fillAllSlots(Inventory GUI, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(" ");
        item.setItemMeta(itemMeta);
        for (int i = 0; i < 54; i++) {
            GUI.setItem(i, item);
        }
    }

    public static String millisToString(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        hours %= 24;
        minutes %= 60;
        seconds %= 60;

        StringBuilder timeString = new StringBuilder();
        if (days > 0) {
            timeString.append(days).append(" day").append(days > 1 ? "s" : "").append(" ");
        }
        if (hours > 0) {
            timeString.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(" ");
        }
        if (minutes > 0) {
            timeString.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(" ");
        }
        if (seconds > 0) {
            timeString.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }

        return timeString.toString().trim();
    }

    public static long stringToMillis(String s) {
        HashMap<String, Long> timeUnits = new HashMap<>();
        timeUnits.put("milliseconds", 1L);
        timeUnits.put("seconds", 1000L);
        timeUnits.put("minutes", 60000L);
        timeUnits.put("hours", 3600000L);
        timeUnits.put("days", 86400000L);

        String[] units = s.split(" ");
        long totalMilliseconds = 0;

        for (int i = 0; i < units.length; i += 2) {
            int value = Integer.parseInt(units[i]);
            String unit = units[i + 1].toLowerCase();

            totalMilliseconds += value * timeUnits.get(unit);
        }
        return totalMilliseconds;
    }
    public static String getWholeString(List<String> strList) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String str : strList) {
            joiner.add(str);
        }
        return joiner.toString();
    }
}
package me.burb.burbkits.api.commands;

import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.api.utils.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CommandKits implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length >= 2) {
            Player player = null;
            PlayerInventory playerInv = null;
            TreeMap<Integer, ItemStack> items = new TreeMap<>();
            if (sender instanceof Player) {
                player = (Player) sender;
                playerInv = player.getInventory();
                for (int i = 0; i <= 40; i++) {
                    items.put(i, playerInv.getItem(i));
                }
            }
            String arg2 = args[1], arg1 = args[0];
            Kit leKit = Kit.getKitFromName(arg2);
            if (arg1.equalsIgnoreCase("create")) {
                if (sender instanceof Player) {
                    if (player.hasPermission("burbkits.managekits")) {
                        if (!playerInv.isEmpty()) {
                            Kit kit = new Kit(arg2);
                            kit.setItems(items);
                            player.sendMessage(Utils.color("&aYou have successfully created a new Kit named '" + arg2 + "'"));
                        } else { player.sendMessage(Utils.color("&cYour inventory is empty!")); }
                    } else { player.sendMessage(Utils.color("&cYou don't have permission!")); }
                } else { sender.sendMessage(Utils.color("&cYou cannot use this command in console!")); }
            } else if (arg1.equalsIgnoreCase("override")) {
                if (sender instanceof Player) {
                    if (player.hasPermission("burbkits.managekits")) {
                        if (leKit != null) {
                            if (!playerInv.isEmpty()) {
                                leKit.setItems(items);
                                player.sendMessage(Utils.color("&aYou have successfully overrided the Kit '" + leKit.getName() + "'"));
                            } else { player.sendMessage(Utils.color("&cYour inventory is empty!")); }
                        } else { player.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist")); }
                    } else { player.sendMessage(Utils.color("&cYou don't have permission!")); }
                } else { sender.sendMessage(Utils.color("&cYou cannot use this command in console!")); }
            } else if (arg1.equalsIgnoreCase("info")) {
                if (sender instanceof Player) {
                    if (leKit != null) {
                        leKit.seeItems(player);
                        player.sendMessage(Utils.color("&aSuccessfully opened the Kit inventory of Kit '" + leKit.getName() + "'"));
                    } else { player.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist!")); }
                } else { sender.sendMessage(Utils.color("&cYou cannot use this command in console!")); }
            } else if (arg1.equalsIgnoreCase("delete")) {
                if (sender.hasPermission("burbkits.managekits")) {
                    if (leKit != null) {
                        leKit.deleteKit();
                        sender.sendMessage(Utils.color("&aSuccessfully deleted the Kit '" + arg2 + "'"));
                    } else { sender.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist")); }
                } else { sender.sendMessage(Utils.color("&cYou don't have permission!")); }
            } else if (arg1.equalsIgnoreCase("claim")) {
                if (sender instanceof Player) {
                    if (leKit != null) {
                        leKit.claimKit(player);
                    } else { player.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist!")); }
                } else { sender.sendMessage(Utils.color("&cYou cannot use this command in console!")); }
            } else if (arg1.equalsIgnoreCase("setCooldown")) {
                if (sender.hasPermission("burbkits.managekits")) {
                    if (args.length >= 4) {
                        if (leKit != null) {
                            List<String> argList = new ArrayList<>();
                            for (int i = 2; i < args.length; i++) {
                                argList.add(args[i]);
                            }
                            long timeInMillis = Utils.stringToMillis(Utils.getWholeString(argList));
                            leKit.setKitCooldown(timeInMillis);
                            sender.sendMessage(Utils.color("&aSuccessfully set the cooldown of the Kit '" + arg2 + "' to '" + Utils.millisToString(timeInMillis)) + "'");
                        } else { sender.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist!")); }
                    } else { sender.sendMessage(Utils.color("&cPlease enter the arguments!")); }
                } else { sender.sendMessage(Utils.color("&cYou don't have permission!")); }
            } else if (arg1.equalsIgnoreCase("setCooldownBypass")) {
                if (sender.hasPermission("burbkits.managekits")) {
                    if (args.length >= 3) {
                        if (leKit != null) {
                            leKit.setCooldownBypassPermission(args[2]);
                            sender.sendMessage(Utils.color("&aSuccessfully set the cooldown bypass permission of the Kit '" + leKit.getName() + "' to '" + args[2] +"'"));
                        } else { sender.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist!")); }
                    } else { sender.sendMessage(Utils.color("&cPlease enter the arguments!")); }
                } else { sender.sendMessage(Utils.color("&cYou don't have permission!")); }
            } else if (arg1.equalsIgnoreCase("setPermission")) {
                if (sender.hasPermission("burbkits.managekits")) {
                    if (args.length == 3) {
                        if (leKit != null) {
                            leKit.setPermission(args[2]);
                            sender.sendMessage(Utils.color("&aSuccessfully set the permission of Kit '" + arg2 + "' to '" + args[2] + "'"));
                        } else { sender.sendMessage(Utils.color("&cKit named '" + arg2 + "' does not exist!")); }
                    } else { sender.sendMessage(Utils.color("&cPlease enter the arguments!")); }
                } else { sender.sendMessage(Utils.color("&cYou don't have permission!")); }
            }
        } else {
            sender.sendMessage(Utils.color("&cPlease enter all the arguments"));
        }
        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player && sender.hasPermission("burbkits.managekits")) {
                return List.of("create", "delete", "info", "claim", "override", "setPermission", "setCooldown", "setCooldownBypass");
            } else if (sender instanceof Player && !sender.hasPermission("BurbKits.manageKits")) {
                return List.of("info", "claim");
            }
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("create")) {
                return Kit.getNames();
            }
        }
        return null;
    }
}

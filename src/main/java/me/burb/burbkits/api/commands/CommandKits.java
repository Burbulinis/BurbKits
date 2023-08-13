package me.burb.burbkits.api.commands;

import me.burb.burbkits.BurbKits;
import me.burb.burbkits.api.kits.Kit;
import me.burb.burbkits.api.utils.Utils;
import me.burb.burbkits.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class CommandKits implements TabExecutor {

    private final Config config = BurbKits.getBurbConfig();
    private final String PERMISSION = config.PERMISSION == null ? config.DEFAULT_PERMISSION : config.NO_PERMISSION;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        final String PREFIX = "&7[&6Burb&eKits&7]&r ";
        if (args.length >= 2) {

            final String NO_PERMISSION = config.NO_PERMISSION == null ? config.DEFAULT_NO_PERMISSION : config.NO_PERMISSION;
            final String NON_EXISTING_KIT = config.NON_EXISTING_KIT == null ? config.DEFAULT_NON_EXISTING_KIT : config.NON_EXISTING_KIT;
            final String OPENED_INVENTORY = config.OPENED_INVENTORY == null ? config.DEFAULT_OPENED_INVENTORY : config.OPENED_INVENTORY;
            final String RESET_COOLDOWN = config.RESET_COOLDOWN == null ? config.DEFAULT_RESET_COOLDOWN : config.RESET_COOLDOWN;

            Player player = null;
            PlayerInventory playerInv = null;
            TreeMap<Integer, ItemStack> items = new TreeMap<>();

            if (sender instanceof Player) {
                player = (Player) sender;
                playerInv = player.getInventory();
                items = Utils.itemsFromInventory(playerInv);
            }

            String arg2 = args[1], arg1 = args[0];
            Kit kit = Kit.getKitFromName(args[1]);

            if (arg1.equalsIgnoreCase("manage")) {

                if (!sender.hasPermission(PERMISSION)) {
                    sender.sendMessage(Utils.color(PREFIX + NO_PERMISSION));
                    return true;
                }

                kit = Kit.getKitFromName(args[2]);
                if (arg2.equalsIgnoreCase("edit")) {
                    if (args.length < 4) {
                        sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
                        return true;
                    }

                    if (kit == null) {
                        String message = NON_EXISTING_KIT.replaceAll("%name%", args[2]);
                        sender.sendMessage(Utils.color(PREFIX + message));
                        return true;
                    }

                    String name = kit.toString();
                    String arg5 = args[4], arg4 = args[3];

                    if (arg4.equalsIgnoreCase("cooldown")) {

                        if (arg5.equalsIgnoreCase("reset")) {
                            if (args.length < 6) {
                                sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
                                return true;
                            } if (Bukkit.getOfflinePlayerIfCached(args[5]) == null) {
                                sender.sendMessage(Utils.color(PREFIX + "&cThe player '" + args[5] + "' does not exist!"));
                                return true;
                            }

                            String arg6 = args[5];
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(arg6);

                            assert offlinePlayer != null;

                            if (offlinePlayer.isOnline()) {
                                Player onlinePlayer = Bukkit.getPlayer(arg6);
                                assert onlinePlayer != null;
                                if (kit.hasCooldown(onlinePlayer)) {
                                    String message = RESET_COOLDOWN.replaceAll("%kit%", name);
                                    onlinePlayer.sendMessage(Utils.color(PREFIX + message));
                                } else {
                                    sender.sendMessage(Utils.color(PREFIX + "&cThis player doesn't have a cooldown for the kit '" + name + "'"));
                                    return true;
                                }
                            }

                            kit.resetCooldown(offlinePlayer);
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully reset the cooldown of '" + offlinePlayer.getName() + "' for the kit '" + name + "'"));
                        } else if (arg5.equalsIgnoreCase("set")) {

                            if (args.length < 6) {
                                sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
                                return true;
                            }

                            List<String> argList = new ArrayList<>(Arrays.asList(args).subList(5, args.length));
                            long timeInMillis = Utils.stringToMillis(Utils.getWholeString(argList));
                            kit.setKitCooldown(timeInMillis);
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully set the cooldown of the kit '" + name + "' to '" + Utils.millisToString(timeInMillis)) + "'");

                        } else if (arg5.equalsIgnoreCase("remove")) {
                            kit.removeCooldown();
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully removed the cooldown of the kit '" + name + "'"));
                        }
                    } else if (arg4.equalsIgnoreCase("cooldownBypass")) {
                        if (arg5.equalsIgnoreCase("set")) {

                            if (args.length < 6) {
                                sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
                                return true;
                            }

                            kit.setCooldownBypassPermission(args[5]);
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully set the cooldown bypass permission of the kit '" + name + "' to '" + args[5] + "'"));

                        } else if (arg5.equalsIgnoreCase("remove")) {
                            kit.removeCooldownBypass();
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully removed the cooldown bypass permission of the kit '" + name + "'"));
                        }
                    } else if (arg4.equalsIgnoreCase("permission")) {
                        if (arg5.equalsIgnoreCase("set")) {

                            if (args.length < 6) {
                                sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
                                return true;
                            }

                            kit.setPermission(args[5]);
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully set the permission of the kit '" + name + "' to '" + args[5] + "'"));

                        } else if (arg5.equalsIgnoreCase("remove")) {

                            kit.removePermission();
                            sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully removed the permission of the kit '" + name + "'"));

                        }
                    } else if (arg4.equalsIgnoreCase("override")) {

                        if (sender instanceof ConsoleCommandSender) {
                            sender.sendMessage(Utils.color(PREFIX + "&cYou cannot use this command in console!"));
                            return true;
                        }
                        assert player != null;
                        if (playerInv.isEmpty()) {
                            player.sendMessage(Utils.color(PREFIX + "&cYour inventory is empty!"));
                            return true;
                        }

                        kit.setItems(items);
                        player.sendMessage(Utils.color(PREFIX + "&aYou have successfully overrided the kit '" + name + "'"));

                    } else if (arg4.equalsIgnoreCase("delete")) {
                        kit.deleteKit();
                        sender.sendMessage(Utils.color(PREFIX + "&aSuccessfully deleted the kit '" + name + "'"));
                    }
                } else if (arg2.equalsIgnoreCase("create")) {

                    if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage(Utils.color(PREFIX + "&cYou cannot use this command in console!"));
                        return true;
                    }
                    assert player != null;
                    if (playerInv.isEmpty()) {
                        player.sendMessage(Utils.color(PREFIX + "&cYour inventory is empty!"));
                        return true;
                    }

                    Kit leKit = new Kit(args[2]);
                    leKit.setItems(items);
                    player.sendMessage(Utils.color(PREFIX + "&aSuccessfully created a new kit named '" + args[2] + "'"));

                }


            } else if (arg1.equalsIgnoreCase("claim")) {
                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(Utils.color(PREFIX + "&cYou cannot use this command in console!"));
                    return true;
                } if (kit == null) {
                    String message = NON_EXISTING_KIT.replaceAll("%name%", args[1]);
                    sender.sendMessage(Utils.color(PREFIX + message));
                    return true;
                }

                kit.claimKit(player, true);

            } else if (arg1.equalsIgnoreCase("info")) {

                if (sender instanceof ConsoleCommandSender) {
                    sender.sendMessage(Utils.color(PREFIX + "&cYou cannot use this command in console!"));
                    return true;
                }
                assert player != null;
                if (kit == null) {
                    String message = NON_EXISTING_KIT.replaceAll("%name%", args[1]);
                    sender.sendMessage(Utils.color(PREFIX + message));
                    return true;
                }

                String message = OPENED_INVENTORY.replaceAll("%kit%", kit.toString());
                player.sendMessage(Utils.color(PREFIX + message));
                kit.seeItems(player);
            }
        } else {
            sender.sendMessage(Utils.color(PREFIX + "&cPlease enter the arguments!"));
        }
        return true;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player && sender.hasPermission(PERMISSION)) {
                return List.of("claim", "info", "manage");
            } else if (sender instanceof Player && !sender.hasPermission(PERMISSION)) {
                return List.of("info", "claim");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("manage") && sender.hasPermission(PERMISSION)) {
                return List.of("edit", "create");
            }
            return Kit.getNames();
        } else if (args.length == 3 && sender.hasPermission(PERMISSION)) {
            if (args[1].equalsIgnoreCase("edit")) {
                return Kit.getNames();
            }
        } else if (args.length == 4 && sender.hasPermission(PERMISSION)) {
            if (args[1].equalsIgnoreCase("edit")) {
                return List.of("cooldown", "permission", "delete", "override", "cooldownBypass");
            }
        } else if (args.length == 5 && sender.hasPermission(PERMISSION)) {
            if (args[3].equalsIgnoreCase("cooldown")) {
                return List.of("set", "remove", "reset");
            } else if (args[3].equalsIgnoreCase("cooldownBypass")) {
                return List.of("set", "remove");
            } else if (args[3].equalsIgnoreCase("delete")) {
                return Kit.getNames();
            } else if (args[3].equalsIgnoreCase("override")) {
                return Kit.getNames();
            } else if (args[3].equalsIgnoreCase("permission")) {
                return List.of("set", "remove");
            }
        }
        return null;
    }
}

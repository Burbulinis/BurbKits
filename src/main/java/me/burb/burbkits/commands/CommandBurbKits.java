package me.burb.burbkits.commands;

import me.burb.burbkits.kits.Kit;
import me.burb.burbkits.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandBurbKits implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            PlayerInventory playerInv = player.getInventory();
            if (args[0].equalsIgnoreCase("create") && args[1] != null) {
                new Kit(playerInv, args[1]);
                player.sendMessage(Utils.color("&aYou have successfully created a new Kit named " + args[1]));
            } else if (args[0].equalsIgnoreCase("override") && args[1] != null) {
                if (Kit.getKitFromName(args[1]) != null) {
                    Kit.getKitFromName(args[1]).overrideKitItems(playerInv);
                    player.sendMessage(Utils.color("&aYou have successfully overrided the Kit named " + args[1]));
                } else {
                    player.sendMessage(Utils.color("&cKit named " + args[1] + " does not exist"));
                }
            } else if (args[0].equalsIgnoreCase("info") && args[1] != null) {

            }
        }
        return true;
    }
}

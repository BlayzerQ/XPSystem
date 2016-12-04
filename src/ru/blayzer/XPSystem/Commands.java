package ru.blayzer.XPSystem;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
    private XPSystem plugin;

    public Commands(XPSystem instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("xpsystem")) {

            // Когда игрок пишет /xpsystem
            if (args.length == 0) {
                if (sender.hasPermission("xpsystem.*")) {
                    sender.sendMessage(ChatColor.GREEN + "-- XPSystem Help --");
                    sender.sendMessage(ChatColor.GOLD + "/xpsystem reload" + ChatColor.WHITE + " reloads the config");
                    sender.sendMessage(ChatColor.GOLD + "/xpsystem help" + ChatColor.WHITE + " commands");
                    return true;
                }
            }
            // Когда игрок пишет /xpsystem reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("xpsystem.reload")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "[XPSystem]" + ChatColor.WHITE + " Config reloaded.");
                    return true;
                }
            }

            // Когда игрок пишет /xpsystem help
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("xpsystem.help")) {
                    sender.sendMessage(ChatColor.GREEN + "-- XPSystem Help --");
                    sender.sendMessage(ChatColor.GOLD + "/xpsystem reload" + ChatColor.WHITE + " reloads the config");
                    sender.sendMessage(ChatColor.GOLD + "/xpsystem help" + ChatColor.WHITE + " commands");
                    return true;
                }
            }
        }
        return false;
    }
}

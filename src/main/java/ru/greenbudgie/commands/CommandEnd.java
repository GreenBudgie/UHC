package ru.greenbudgie.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.greenbudgie.UHC.UHC;

public class CommandEnd implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(!UHC.playing) {
			sender.sendMessage(ChatColor.RED + "Игра не идет");
		} else {
			UHC.endGame();
		}
		return true;
	}
}

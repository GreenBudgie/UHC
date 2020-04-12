package ru.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.WorldManager;

public class CommandArena implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(args.length == 1 && args[0].equalsIgnoreCase("2")) {
			((Player) sender).teleport(WorldManager.getSecondArena().getSpawnLocation());
		} else {
			((Player) sender).teleport(WorldManager.getMainArena().getSpawnLocation());
		}
		return true;
	}
}

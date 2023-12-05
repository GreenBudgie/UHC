package ru.greenbudgie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.greenbudgie.requester.ItemRequester;

public class CommandRequests implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player player) {
			ItemRequester.openRequesterInventory(player);
		}
		return true;
	}

}

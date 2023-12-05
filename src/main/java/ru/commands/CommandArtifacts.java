package ru.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.artifact.ArtifactManager;
import ru.requester.ItemRequester;

public class CommandArtifacts implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player player) {
			ArtifactManager.openArtifactInventory(player);
		}
		return true;
	}

}

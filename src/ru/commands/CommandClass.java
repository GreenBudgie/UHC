package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.GameState;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.classes.ClassManager;
import ru.classes.UHCClass;

public class CommandClass implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(PlayerManager.isPlaying(player)) {
			UHCClass playerClass = ClassManager.getInGameClass(player);
			if(playerClass == null) {
				player.sendMessage(
						ChatColor.BOLD + "" + ChatColor.GRAY + "> " +
						ChatColor.DARK_GREEN + "Класс " +
						ChatColor.BOLD + "не выбран!");
			} else {
				ClassManager.openClassInfoInventory(player, playerClass);
			}
		} else {
			ClassManager.openClassSelectInventory(player);
		}
		return true;
	}

}

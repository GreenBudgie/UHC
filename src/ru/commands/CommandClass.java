package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.classes.ClassManager;
import ru.classes.UHCClass;
import ru.util.ItemUtils;

public class CommandClass implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(UHC.state.isInGame() || UHC.state == GameState.DEATHMATCH) {
			UHCClass playerClass = ClassManager.getClass(player);
			if(playerClass == null) {
				player.sendMessage(
						ChatColor.BOLD + "" + ChatColor.GRAY + "> " +
						ChatColor.DARK_GREEN + "Класс " +
						ChatColor.BOLD + "не выбран!");
			} else {
				player.sendMessage(
						ChatColor.BOLD + "" + ChatColor.GRAY + "> " +
						ChatColor.DARK_GREEN + "Твой класс: " +
						playerClass.getName());
				for(String advantage : playerClass.getAdvantages()) {
					player.sendMessage(
							ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "+ " +
							ChatColor.RESET + ChatColor.GREEN + advantage);
				}
				for(String disadvantage : playerClass.getDisadvantages()) {
					player.sendMessage(
							ChatColor.DARK_RED + "" + ChatColor.BOLD + "- " +
							ChatColor.RESET + ChatColor.RED + disadvantage);
				}
			}
		} else {
			ClassManager.openClassInventory(player);
		}
		return true;
	}

}

package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.classes.ClassManager;
import ru.lobby.LobbyMapPreview;
import ru.main.UHCPlugin;

public class CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		char symbol = '\u2318';
		UHCPlugin.log(symbol, ChatColor.WHITE + "" + ChatColor.BOLD + symbol);
		return true;
	}

}

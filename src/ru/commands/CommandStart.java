package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.util.MathUtils;

import java.util.List;

public class CommandStart implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "Только админ может начать игру");
			return true;
		}
		if(UHC.playing) {
			sender.sendMessage(ChatColor.RED + "Игра уже идет");
		} else {
			if(args.length >= 1 && args[0].equalsIgnoreCase("fast")) UHC.fastStart = 1;
			UHC.startGame();
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, "fast");
		}
		return null;
	}
}

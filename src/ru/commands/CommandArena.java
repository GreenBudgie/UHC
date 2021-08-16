package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.ArenaManager;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandArena implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(args.length >= 1) {
			String worldName = args[0];
			for(ArenaManager.Arena arena : ArenaManager.getArenas()) {
				if(arena.world().getName().equals(worldName)) {
					((Player) sender).teleport(arena.world().getSpawnLocation());
					sender.sendMessage(ChatColor.WHITE + "Телепортирован на арену: " + ChatColor.DARK_GREEN + arena.name());
					break;
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, ArenaManager.getArenas().stream().map(arena -> arena.world().getName()).toList());
		}
		return new ArrayList<>();
	}
}

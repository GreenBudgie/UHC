package ru.greenbudgie.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CommandEditArena implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(sender instanceof Player player) {
			if(args.length >= 2) {
				ArenaManager.Arena selectedArena = null;
				for(ArenaManager.Arena currentArena : ArenaManager.getArenas()) {
					if(args[0].equals("current") && currentArena.getWorld() == player.getWorld()) {
						selectedArena = currentArena;
						break;
					} else if(currentArena.getWorld().getName().equals(args[0]) || currentArena.getSimpleName().equals(args[0])) {
						selectedArena = currentArena;
						break;
					}
				}
				if(selectedArena == null) {
					player.sendMessage(ChatColor.DARK_RED + "Unable to find the specified arena");
					return true;
				}
				ArenaManager.ArenaOptions option;
				try {
					option = ArenaManager.ArenaOptions.valueOf(args[1]);
				} catch(Exception ignored) {
					player.sendMessage(ChatColor.DARK_RED + "Unknown option");
					return true;
				}
				if(args.length == 2) {
					Object value = selectedArena.getByOption(option);
					if(value == null) value = "null";
					player.sendMessage(ChatColor.WHITE + option.name() + ChatColor.GRAY + " is now " + ChatColor.WHITE + value.toString());
					return true;
				}
				if(args.length == 3) {
					try {
						selectedArena.setByOption(option, args[2]);
						selectedArena.updateConfig();
						player.sendMessage(ChatColor.WHITE + option.name() +
								ChatColor.GRAY + " is now " +
								ChatColor.WHITE + selectedArena.getByOption(option));
					} catch(NumberFormatException exception) {
						player.sendMessage(ChatColor.DARK_RED + "Invalid value");
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			List<String> tips = new ArrayList<>(ArenaManager.getArenas().stream().map(ArenaManager.Arena::getSimpleName).toList());
			tips.add("current");
			return MathUtils.getListOfStringsMatchingLastWord(args, tips);
		}
		if(args.length == 2) {
			return MathUtils.getListOfStringsMatchingLastWord(args,
					Stream.of(ArenaManager.ArenaOptions.values()).map(ArenaManager.ArenaOptions::name).toList());
		}
		return new ArrayList<>();
	}
}

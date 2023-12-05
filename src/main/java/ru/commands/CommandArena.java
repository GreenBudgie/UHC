package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.ArenaManager;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandArena implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player player) {
			if(args.length >= 1) {
				String worldName = args[0];
				boolean found = false;
				for(ArenaManager.Arena arena : ArenaManager.getArenas()) {
					if(arena.getSimpleName().equals(worldName) || arena.getWorld().getName().equals(worldName)) {
						player.teleport(arena.getWorld().getSpawnLocation());
						player.sendMessage(ChatColor.WHITE + "Просмотр арены - " + ChatColor.DARK_GREEN + arena.getName());
						if(!arena.isOpen()) {
							player.sendMessage(ChatColor.WHITE + "Это " +
									ChatColor.GRAY + ChatColor.BOLD + " закрытая " +
									ChatColor.WHITE + "арена - нельзя выйти за ее пределы");
						}
						player.sendMessage(ChatColor.GRAY + "Напиши " + ChatColor.WHITE + "/lobby" + ChatColor.GRAY + ", чтобы вернуться");
						if(!arena.isEnabled()) player.sendMessage(ChatColor.RED + "Эта арена сейчас не используется!");
						found = true;
						break;
					}
				}
				if(!found) {
					player.sendMessage(ChatColor.DARK_RED + "Неверное название арены!");
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, ArenaManager.getArenas().stream().map(ArenaManager.Arena::getSimpleName).toList());
		}
		return new ArrayList<>();
	}
}

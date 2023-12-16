package ru.greenbudgie.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class CommandArena implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
		if (args.length == 0) {
            if (PlayerManager.isInGame(player)) {
                player.sendMessage(DARK_GRAY + "" + BOLD + "> " +
                        GRAY + "Дезматч будет проходить на арене" +
                        DARK_GRAY + ": " +
                        DARK_GREEN + ArenaManager.getCurrentArena().getName() +
                        DARK_GRAY + "" + BOLD + " <");
                return true;
            }
            ArenaManager.openArenaPreviewInventory(player);
			return true;
		}
        String worldName = args[0];
        boolean found = false;
        for(ArenaManager.Arena arena : ArenaManager.getArenas()) {
            if(arena.getSimpleName().equals(worldName) || arena.getWorld().getName().equals(worldName)) {
                ArenaManager.previewArena(arena, player);
                found = true;
                break;
            }
        }
        if(!found) {
            player.sendMessage(ChatColor.DARK_RED + "Неверное название арены!");
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

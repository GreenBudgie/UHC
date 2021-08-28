package ru.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.WorldManager;
import ru.lobby.sign.SignManager;
import ru.util.MathUtils;

import java.util.List;

public class CommandWorldTime implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(sender instanceof Player player) {
			World world = player.getWorld();
			if(world.getEnvironment() == World.Environment.NORMAL) {
				if(args.length == 1) {
					switch(args[0]) {
						case "day" -> world.setTime(1000);
						case "night" -> world.setTime(13000);
						case "noon" -> world.setTime(6000);
						case "midnight" -> world.setTime(18000);
						default -> {
							try {
								int time = Integer.parseInt(args[0]);
								world.setTime(time);
							} catch(NumberFormatException ex) {
								player.sendMessage("Illegal number format");
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("day", "night", "noon", "midnight"));
		}
		return null;
	}
}

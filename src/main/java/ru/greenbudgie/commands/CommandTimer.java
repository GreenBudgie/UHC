package ru.greenbudgie.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.GameState;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.MathUtils;

import java.util.List;

public class CommandTimer implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		if(!UHC.state.isBeforeDeathmatch() && UHC.state != GameState.DEATHMATCH) {
			sender.sendMessage(ChatColor.RED + "Возможно выполнение только во время OUTBREAK, GAME и DEATHMATCH");
			return true;
		}
		if(args.length == 1) {
			if(args[0].equalsIgnoreCase("reset")) {
				switch(UHC.state) {
					case OUTBREAK -> UHC.outbreakTimer = UHC.getNoPVPDuration() * 60;
					case GAME -> UHC.deathmatchTimer = UHC.getGameDuration() * 60;
					case DEATHMATCH -> UHC.arenaTimer = UHC.DEATHMATCH_START_TIMER;
				}
			}
		}
		if(args.length == 2) {
			String time = args[1];
			boolean mins = time.endsWith("m") || time.endsWith("M");
			if(mins) {
				time = time.substring(0, time.length() - 1);
			}
			int timer;
			try {
				timer = Integer.valueOf(time) * (mins ? 60 : 1);
			} catch(NumberFormatException ex) {
				sender.sendMessage(ChatColor.RED + "Illegal input string: " + time);
				return true;
			}
			if(args[0].equalsIgnoreCase("set")) {
				switch(UHC.state) {
					case OUTBREAK -> UHC.outbreakTimer = timer;
					case GAME -> UHC.deathmatchTimer = timer;
					case DEATHMATCH -> UHC.arenaTimer = timer;
				}
			}
			if(args[0].equalsIgnoreCase("add")) {
				switch(UHC.state) {
					case OUTBREAK -> UHC.outbreakTimer = UHC.outbreakTimer + timer;
					case GAME -> UHC.deathmatchTimer = UHC.deathmatchTimer + timer;
					case DEATHMATCH -> UHC.arenaTimer = UHC.arenaTimer + timer;
				}
			}

		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("reset", "set", "add"));
		}
		return null;
	}
}

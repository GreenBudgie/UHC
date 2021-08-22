package ru.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.drop.Drop;
import ru.drop.Drops;
import ru.util.MathUtils;

import java.util.List;

public class CommandDrop implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp() || !UHC.playing) return true;
		Player p = (Player) sender;
		if(args.length == 2) {
			Drop drop = switch(args[0]) {
				case "air" -> Drops.AIRDROP;
				case "cave" -> Drops.CAVEDROP;
				case "nether" -> Drops.NETHERDROP;
				default -> null;
			};
			if(drop != null) {
				if(args[1].equalsIgnoreCase("reset")) {
					drop.setup();
				}
				if(args[1].equalsIgnoreCase("drop")) {
					drop.setTimer(5);
				}
				if(args[1].equalsIgnoreCase("changeloc")) {
					drop.setLocation(drop.getRandomLocation());
				}
				if(args[1].equalsIgnoreCase("currentloc")) {
					drop.setLocation(p.getLocation());
				}
				if(args[1].equalsIgnoreCase("tp")) {
					p.teleport(drop.getLocation());
				}
			} else {
				p.sendMessage(ChatColor.RED + "Такого дропа не существует");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("air", "cave", "nether"));
		}
		if(args.length == 2) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList(
					"reset", "drop", "changeloc", "currentloc", "tp"));
		}
		return null;
	}
}

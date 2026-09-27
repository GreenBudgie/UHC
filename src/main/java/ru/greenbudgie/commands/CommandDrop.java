package ru.greenbudgie.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.drop.Drop;
import ru.greenbudgie.drop.Drops;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.weighted.WeightedItem;

import java.util.List;

public class CommandDrop implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp() || !UHC.playing) return true;
		if (!(sender instanceof Player player)) {
			return true;
		}
		if(args.length >= 2) {
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
					drop.setTimer(1);
				}
				if(args[1].equalsIgnoreCase("changeloc")) {
					drop.setLocation(drop.getRandomLocation());
				}
				if(args[1].equalsIgnoreCase("currentloc")) {
					drop.setLocation(player.getLocation());
				}
				if(args[1].equalsIgnoreCase("tp")) {
					player.teleport(drop.getLocation());
				}
				if (args[1].equals("giveRandom")) {
					int amount = 1;
					if (args.length >= 3) {
						amount = Integer.parseInt(args[2]);
					}
					boolean repeat = args.length >= 4 && args[3].equals("repeat");
					List<WeightedItem> items = drop.getWeightedItemList().getRandomElementsWeighted(amount, repeat);
					for (WeightedItem item : items) {
						player.getWorld().dropItem(player.getLocation(), item.getItem().clone());
					}
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Такого дропа не существует");
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
					"reset", "drop", "changeloc", "currentloc", "tp", "giveRandom"));
		}
		if(args.length == 3 && args[1].equals("giveRandom")) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("repeat"));
		}
		return null;
	}
}

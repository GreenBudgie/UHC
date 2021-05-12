package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.UHC.PlayerStat;
import ru.util.MathUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRating implements CommandExecutor, TabCompleter {

	private double getWinrate(String name) {
		return PlayerStat.ratio(name, PlayerStat.WINS, PlayerStat.GAMES);
	}

	private Map<Integer, String> getRevLadder() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		List<String> list = PlayerStat.getRegisteredPlayerNames();
		int size = list.size();
		for(int i = 0; i < size; i++) {
			double max = Integer.MIN_VALUE;
			String name = "error";
			for(String s : list) {
				double points = PlayerStat.POINTS.getValue(s);
				if(points > max) {
					max = points;
					name = s;
				}
			}
			map.put(i + 1, name);
			list.remove(name);
		}
		return map;
	}

	private Map<String, Integer> getLadder() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		List<String> list = PlayerStat.getRegisteredPlayerNames();
		int size = list.size();
		for(int i = 0; i < size; i++) {
			double max = Integer.MIN_VALUE;
			String name = "error";
			for(String s : list) {
				double points = PlayerStat.POINTS.getValue(s);
				if(points > max) {
					max = points;
					name = s;
				}
			}
			map.put(name, i + 1);
			list.remove(name);
		}
		return map;
	}

	private String getColoredPos(int pos) {
		switch(pos) {
		case 1:
			return ChatColor.YELLOW + "" + pos;
		case 2:
			return ChatColor.GRAY + "" + pos;
		case 3:
			return ChatColor.GOLD + "" + pos;
		}
		return ChatColor.DARK_GRAY + "" + pos;
	}

	private void showRating(CommandSender receiver, String playerName) {
		receiver.sendMessage(ChatColor.YELLOW + "Статистика " + ChatColor.GOLD + playerName + ChatColor.YELLOW + ":");
		for(PlayerStat stat : PlayerStat.values()) {
			receiver.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + stat.getName() + ChatColor.DARK_AQUA + ": " + ChatColor.WHITE + stat.getValue(playerName));
		}
		receiver.sendMessage(
				ChatColor.GOLD + "Рейтинговая позиция: " + getColoredPos(getLadder().get(playerName)) + ChatColor.WHITE + ", " + ChatColor.AQUA + "винрейт: " + ChatColor.DARK_AQUA
						+ MathUtils.decimal(getWinrate(playerName), 2));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.YELLOW + "Рейтинг игроков:");
			Map<Integer, String> ladder = getRevLadder();
			for(int i = 1; i <= ladder.size(); i++) {
				sender.sendMessage(
						getColoredPos(i) + ChatColor.WHITE + ". " + ChatColor.GOLD + ladder.get(i) + ChatColor.WHITE + ", " + ChatColor.AQUA + "очки: " + ChatColor.DARK_AQUA
								+ PlayerStat.POINTS.getValue(ladder.get(i)) + ChatColor.WHITE + ", " + ChatColor.AQUA + "винрейт: " + ChatColor.DARK_AQUA + MathUtils
								.decimal(getWinrate(ladder.get(i)), 2));
			}
		} else {
			if(PlayerStat.getRegisteredPlayerNames().contains(args[0])) {
				showRating(sender, args[0]);
			} else {
				sender.sendMessage(ChatColor.RED + "Игрока " + ChatColor.GOLD + args[0] + ChatColor.RED + " не существует");
			}
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, PlayerStat.getRegisteredPlayerNames());
		}
		return null;
	}

}

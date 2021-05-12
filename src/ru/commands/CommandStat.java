package ru.commands;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.UHC.PlayerStat;
import ru.util.MathUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandStat implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isOp()) {
			if(args.length == 1 && args[0].equals("cleanup")) {
				for(String p : PlayerStat.getRegisteredPlayerNames()) {
					if(Lists.newArrayList(PlayerStat.values()).stream().allMatch(stat -> stat.getValue(p) == 0)) {
						PlayerStat.deleteStats(p);
						sender.sendMessage(ChatColor.GOLD + p + ChatColor.YELLOW + " удален из статистики");
					}
				}
				sender.sendMessage(ChatColor.AQUA + "Очистка окончена");
				PlayerStat.save();
				return true;
			}
			if(args.length >= 2) {
				String player = args[1];
				if(PlayerStat.getRegisteredPlayerNames().contains(player) || player.equals("all")) {
					List<String> players = player.equals("all") ? PlayerStat.getRegisteredPlayerNames() : Lists.newArrayList(player);
					if(args[0].equalsIgnoreCase("delete")) {
						for(String p : players) {
							PlayerStat.deleteStats(p);
							sender.sendMessage(ChatColor.GOLD + p + ChatColor.YELLOW + " удален из статистики");
						}
						PlayerStat.save();
						return true;
					} else {
						if(args.length >= 3) {
							List<PlayerStat> stats;
							try {
								stats = args[2].equals("all") ? Lists.newArrayList(PlayerStat.values()) : Lists.newArrayList(PlayerStat.valueOf(args[2]));
							} catch(Exception e) {
								sender.sendMessage(ChatColor.RED + "Нет статы " + ChatColor.GOLD + args[2]);
								return true;
							}
							if(args[0].equalsIgnoreCase("zero")) {
								for(String p : players) {
									for(PlayerStat stat : stats) {
										PlayerStat.zeroStats(p);
										sender.sendMessage(ChatColor.AQUA + stat.getName() + ChatColor.YELLOW + " сброшен для " + ChatColor.GOLD + p);
									}
								}
								PlayerStat.save();
								return true;
							}
							if(args.length >= 4) {
								int c;
								try {
									c = Integer.valueOf(args[3]);
								} catch(NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + "Это не число: " + ChatColor.GOLD + args[3]);
									return true;
								}
								if(args[0].equalsIgnoreCase("set")) {
									for(String p : players) {
										for(PlayerStat stat : stats) {
											stat.setValue(p, c);
											sender.sendMessage(
													ChatColor.AQUA + stat.getName() + ChatColor.YELLOW + " установлены на " + ChatColor.DARK_AQUA + c + ChatColor.YELLOW + " для "
															+ ChatColor.GOLD + p);
										}
									}
									PlayerStat.save();
									return true;
								}
								if(args[0].equalsIgnoreCase("add")) {
									for(String p : players) {
										for(PlayerStat stat : stats) {
											int newVal = stat.getValue(p) + c;
											stat.setValue(p, newVal);
											sender.sendMessage(
													ChatColor.AQUA + stat.getName() + ChatColor.YELLOW + " теперь равны " + ChatColor.DARK_AQUA + newVal + ChatColor.YELLOW + " для "
															+ ChatColor.GOLD + p);
										}
									}
									PlayerStat.save();
									return true;
								}
							}
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Игрока " + ChatColor.GOLD + args[1] + " не существует");
					return true;
				}
			}
		}
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, "zero", "delete", "set", "add", "cleanup");
		}
		if(args.length == 2) {
			List<String> list = Lists.newArrayList(PlayerStat.getRegisteredPlayerNames());
			list.add("all");
			return MathUtils.getListOfStringsMatchingLastWord(args, list);
		}
		if(args.length == 3) {
			if(args[0].equals("zero") || args[0].equals("set") || args[0].equals("add")) {
				List<String> list = Lists.newArrayList(PlayerStat.values()).stream().map(PlayerStat::name).collect(Collectors.toList());
				list.add("all");
				return MathUtils.getListOfStringsMatchingLastWord(args, list);
			}
		}
		return null;
	}

}

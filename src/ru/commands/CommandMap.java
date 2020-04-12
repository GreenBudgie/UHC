package ru.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.SignManager;
import ru.UHC.WorldManager;
import ru.util.MathUtils;

import java.util.List;

public class CommandMap implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(args.length >= 1) {
			if(args[0].equalsIgnoreCase("regen")) {
				WorldManager.regenMap();
			}
			if(args[0].equalsIgnoreCase("create")) {
				if(WorldManager.hasMap()) {
					sender.sendMessage(ChatColor.DARK_RED + "����� ��� �������");
				} else {
					WorldManager.createMap();
				}
				SignManager.updateSigns();
			}
			if(args[0].equalsIgnoreCase("remove")) {
				if(!WorldManager.hasMap()) {
					sender.sendMessage(ChatColor.DARK_RED + "����� � ��� �������");
				} else {
					WorldManager.removeMap();
					sender.sendMessage(ChatColor.GREEN + "����� �������");
				}
				SignManager.updateSigns();
			}
			if(args[0].equalsIgnoreCase("status")) {
				if(WorldManager.hasMap()) sender.sendMessage(ChatColor.GREEN + "����� �������"); else sender.sendMessage(ChatColor.RED + "����� �� �������");
			}
			if(args[0].equalsIgnoreCase("keep")) {
				boolean flag = true;
				if(args.length >= 2) {
					flag = args[1].equalsIgnoreCase("true");
				}
				WorldManager.keepMap = flag;
				sender.sendMessage(flag ? (ChatColor.YELLOW + "������ ����� ���� ����� �����������") : (ChatColor.YELLOW + "������ ����� ���� ����� ���������"));
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("regen", "create", "remove", "status", "keep"));
		}
		if(args.length == 2) {
			return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("true", "false"));
		}
		return null;
	}
}

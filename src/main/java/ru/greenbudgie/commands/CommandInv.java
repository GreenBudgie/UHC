package ru.greenbudgie.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerInventoryView;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandInv implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(MutatorManager.omniscient.isActive()) {
			if(args.length >= 1) {
				Player observer = (Player) sender;
				if(!PlayerManager.isPlaying(observer)) return true;
				Player target = Bukkit.getPlayer(args[0]);
				if(target != null && PlayerManager.isPlaying(target)) {
					PlayerInventoryView.viewInventory(observer, target);
				} else {
					sender.sendMessage(ChatColor.RED + "Нет такого игрока!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Нужно указать игрока!");
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, PlayerManager.getAliveOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
		}
		return new ArrayList<>();
	}
}

package ru.commands;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.mutator.InventoryBuilderMutator;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CommandOptMutator implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		if(UHC.playing) {
			if(args.length == 0) {
				Player p = (Player) sender;
				InventoryBuilderMutator builder = InventoryBuilderMutator.getBuilder(p);
				builder.setOP(true);
				builder.openInventory();
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("clear")) {
					MutatorManager.deactivateMutators();
					sender.sendMessage(ChatColor.GOLD + "Все мутаторы деактивированы");
				}
				if(args[0].equalsIgnoreCase("new")) {
					MutatorManager.activateRandomMutator(false, true);
				}
			}
			if(args.length == 2) {
				Mutator mutator = MutatorManager.byClassName(args[1]);
				if(mutator != null) {
					if(args[0].equalsIgnoreCase("activate")) {
						if(MutatorManager.isActive(mutator)) {
							sender.sendMessage(ChatColor.RED + "Данный мутатор уже активен");
						} else {
							mutator.activate(false, null);
						}
					}
					if(args[0].equalsIgnoreCase("deactivate")) {
						if(!MutatorManager.isActive(mutator)) {
							sender.sendMessage(ChatColor.RED + "Данный мутатор уже неактивен");
						} else {
							sender.sendMessage(ChatColor.GOLD + "Деактивирован мутатор: " + ChatColor.LIGHT_PURPLE + mutator.getName());
							mutator.deactivate();
						}
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Нет такого мутатора: " + args[1]);
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Игра не идет");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
		if(commandSender.isOp()) {
			if(args.length == 1) {
				return MathUtils.getListOfStringsMatchingLastWord(args, Lists.newArrayList("activate", "deactivate", "clear", "new"));
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("activate")) {
					return MathUtils.getListOfStringsMatchingLastWord(args, MutatorManager.mutators.stream().filter(mutator -> !MutatorManager.isActive(mutator))
							.map(mutator -> mutator.getClass().getSimpleName().replaceAll("Mutator", "")).collect(Collectors.toList()));
				}
				if(args[0].equalsIgnoreCase("deactivate")) {
					return MathUtils.getListOfStringsMatchingLastWord(args, MutatorManager.mutators.stream().filter(MutatorManager::isActive)
							.map(mutator -> mutator.getClass().getSimpleName().replaceAll("Mutator", "")).collect(Collectors.toList()));
				}
			}
		}
		return null;
	}
}

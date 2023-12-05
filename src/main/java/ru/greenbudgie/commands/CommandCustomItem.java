package ru.greenbudgie.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCustomItem implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(p.isOp()) {
			if(args.length == 0) {
				p.openInventory(getCustomItemsInventory(p));
				return true;
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("give")) {
					CustomItem item = CustomItems.getByName(args[1]);
					if(item != null) {
						ItemStack stack = item.getItemStack();
						p.sendMessage(ChatColor.GREEN + "Выдан " + item.getName());
						p.getInventory().addItem(stack);
					} else {
						p.sendMessage(ChatColor.RED + "Такого предмета не существует.");
					}
					return true;
				}
			}
			if(args.length == 3) {
				CustomItem item = null;
				ItemStack stack = null;
				if(CustomItems.isCustomItem(p.getInventory().getItemInMainHand())) {
					stack = p.getInventory().getItemInMainHand();
					item = CustomItems.getCustomItem(stack);
				} else {
					if(CustomItems.isCustomItem(p.getInventory().getItemInOffHand())) {
						stack = p.getInventory().getItemInOffHand();
						item = CustomItems.getCustomItem(stack);
					}
				}
				if(item != null && stack != null) {
					if(InventoryHelper.hasValue(stack, args[1])) {
						if(InventoryHelper.changeValue(stack, args[1], args[2])) {
							p.sendMessage(ChatColor.YELLOW + "Установлено значение " + ChatColor.GOLD + args[2] + ChatColor.YELLOW + " для " + ChatColor.GOLD
									+ args[1]);
						} else {
							p.sendMessage(ChatColor.DARK_RED + "Неизвестная ошибка");
						}
					} else {
						p.sendMessage(ChatColor.RED + "Предмет не имеет параметра " + ChatColor.YELLOW + args[1]);
					}
				} else {
					p.sendMessage(ChatColor.RED + "Нужно держать предмет в руке");
				}
			}
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(args.length == 1) {
			return MathUtils.getListOfStringsMatchingLastWord(args, "give", "value");
		}
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("give")) {
				List<String> list = new ArrayList<String>();
				for(String s : CustomItems.getAllNames()) {
					list.add(ChatColor.stripColor(s).replaceAll(" ", "_"));
				}
				return MathUtils.getListOfStringsMatchingLastWord(args, list);
			}
			if(args[0].equalsIgnoreCase("value")) {
				Player p = (Player) sender;
				CustomItem item = null;
				ItemStack stack = null;
				if(CustomItems.isCustomItem(p.getInventory().getItemInMainHand())) {
					stack = p.getInventory().getItemInMainHand();
					item = CustomItems.getCustomItem(stack);
				} else {
					if(CustomItems.isCustomItem(p.getInventory().getItemInOffHand())) {
						stack = p.getInventory().getItemInOffHand();
						item = CustomItems.getCustomItem(stack);
					}
				}
				if(item != null && stack != null) {
					return MathUtils.getListOfStringsMatchingLastWord(args, InventoryHelper.getValues(stack).keySet());
				}
			}
		}
		return null;
	}

	public static Inventory getCustomItemsInventory(Player p) {
		Inventory inv = Bukkit.createInventory(p, (int) Math.floor(CustomItems.getItems().size() / 9.0) * 9 + 9, ChatColor.GREEN + "Custom Items");
		InventoryHelper.addItems(inv, 0, CustomItems.getItems().stream().map(CustomItem::getItemStack).collect(Collectors.toList()));
		return inv;
	}

}

package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;
import ru.util.ItemUtils;

public class  CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		ItemStack item = p.getInventory().getItemInMainHand();
		UHCPlugin.log(CustomItems.highlighter.isEquals(item));
		UHCPlugin.log(ItemUtils.getCustomValue(item, "customitem"));
		return true;
	}
}

package ru.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
		TextComponent text = new TextComponent("Test");
		text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teammate request Forest_engine2"));
		p.spigot().sendMessage(text, new TextComponent(" test2"));
		return true;
	}
}

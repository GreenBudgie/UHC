package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;

public class CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		UHCPlugin.log("Adding effect: 60");
		p.getInventory().addItem(InventoryHelper.generatePotion(ChatColor.WHITE + "Potion of Life", Color.fromRGB(252, 119, 255), true, false, new PotionEffect(PotionEffectType.HEAL, 1, 1)));
		return true;
	}

}

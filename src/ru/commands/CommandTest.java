package ru.commands;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
import ru.UHC.UHCPlayer;
import ru.classes.ClassDemon;
import ru.classes.ClassManager;
import ru.main.UHCPlugin;
import ru.util.InventoryHelper;
import ru.util.WorldHelper;

public class CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(p);
		ClassManager.DEMON.setSoulFlame(uhcPlayer, 1);
		ClassManager.DEMON.updateSoulFlame(uhcPlayer, 0);
		return true;
	}

}

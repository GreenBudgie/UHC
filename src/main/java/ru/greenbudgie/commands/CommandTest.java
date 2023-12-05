package ru.greenbudgie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.classes.ClassManager;

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

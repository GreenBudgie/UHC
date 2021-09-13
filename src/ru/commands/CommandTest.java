package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.main.UHCPlugin;

public class CommandTest implements CommandExecutor {

	//Test command. Write here anything you want.
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.isOp()) return true;
		Player p = (Player) sender;
		Block block = p.getLocation().getBlock();
		block.setType(Material.CRIMSON_SIGN);
		Sign sign = (Sign) block.getState();
		sign.setLine(0, ChatColor.WHITE + "Трагически");
		sign.setLine(1, ChatColor.WHITE + "погиб");
		sign.setLine(2, p.getName());
		sign.setLine(3, ChatColor.WHITE + "" + ChatColor.BOLD + "RIP");
		sign.update(true, false);
		return true;
	}

}

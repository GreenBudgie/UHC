package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
		Block signBlock = getSignBlockBelow(p.getLocation());
		if(signBlock != null) {
			signBlock.setType(Material.CRIMSON_SIGN);
			Sign sign = (Sign) signBlock.getState();
			sign.setLine(0, ChatColor.WHITE + "Трагически");
			sign.setLine(1, ChatColor.WHITE + "погиб");
			sign.setLine(2, p.getName());
			sign.setLine(3, ChatColor.WHITE + "" + ChatColor.BOLD + "RIP");
			sign.update(true, false);
			UHCPlugin.log("found");
		} else {
			UHCPlugin.log("Not found");
		}
		return true;
	}

	private Block getSignBlockBelow(Location location) {
		if(!location.getBlock().getType().isAir()) return null;
		for(int i = 0;; i++) {
			Location currentLocation = location.clone().add(0, -i, 0);
			if(currentLocation.getBlockY() <= 0) return null;
			Block currentBlock = currentLocation.getBlock();
			if(!currentBlock.getType().isAir()) {
				Block blockAbove = location.clone().add(0, -i + 1, 0).getBlock();
				if(blockAbove.getType().isAir() && currentBlock.getType().isSolid()) {
					return blockAbove;
				} else {
					return null;
				}
			}
		}
	}

}

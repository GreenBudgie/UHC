package ru.greenbudgie.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.greenbudgie.rating.InventoryBuilderRating;

public class CommandRating implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player player) {
			InventoryBuilderRating builder = InventoryBuilderRating.getBuilder(player);
			builder.setOp(false);
			if(player.isOp() && args.length == 1 && args[0].equals("op")) {
				builder.setOp(true);
			}
			builder.openInventory();
		}
		return true;
	}

}

package ru.greenbudgie.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.GameState;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.mutator.InventoryBuilderMutator;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.MutatorManager;

public class CommandMutator implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(UHC.state.isInGame() || UHC.state == GameState.DEATHMATCH) {
			player.sendMessage(ChatColor.GOLD + "Активные мутаторы " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + MutatorManager.activeMutators.size()
					+ ChatColor.DARK_GRAY + ")" + ChatColor.GOLD + ":");
			for(Mutator mutator : MutatorManager.activeMutators) {
				player.sendMessage(mutator.getInfo());
			}
		} else {
			InventoryBuilderMutator builder = InventoryBuilderMutator.getBuilder(player);
			builder.setOP(false);
			builder.openInventory();
		}
		return true;
	}

}

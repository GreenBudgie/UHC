package ru.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;

public class CommandMutator implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(UHC.state.isInGame() || UHC.state == GameState.DEATHMATCH) {
			p.sendMessage(ChatColor.GOLD + "Активные мутаторы " + ChatColor.DARK_GRAY + "(" + ChatColor.DARK_AQUA + MutatorManager.activeMutators.size()
					+ ChatColor.DARK_GRAY + ")" + ChatColor.GOLD + ":");
			for(Mutator mutator : MutatorManager.activeMutators) {
				p.sendMessage(mutator.getInfo());
			}
		} else {
			p.openInventory(MutatorManager.getMutatorInventory(p, false));
		}
		return true;
	}

}

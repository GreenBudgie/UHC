package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.pvparena.PvpArena;

public class CommandLobby implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if(UHC.isPlaying(p)) {
			p.sendMessage(ChatColor.RED + "Нельзя выйти в лобби во время игры");
		} else {
			p.getInventory().remove(Material.TRIDENT);
			if(UHC.isInLobby(p)) {
				PvpArena.onArenaLeave(p);
			}
			boolean inGame = UHC.isInGame(p);
			p.teleport(WorldManager.getLobby().getSpawnLocation());
			UHC.inGameLeave(p, false);
			if(inGame) {
				UHC.refreshScoreboards();
			}
		}
		return true;
	}
}

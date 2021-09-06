package ru.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.lobby.Lobby;
import ru.lobby.LobbyGameManager;

public class CommandLobby implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if(PlayerManager.isPlaying(player)) {
			player.sendMessage(ChatColor.RED + "Нельзя выйти в лобби во время игры");
		} else {
			if(Lobby.isInLobbyOrWatchingArena(player)) {
				LobbyGameManager.PVP_ARENA.onArenaLeave(player);
			}
			boolean inGame = PlayerManager.isInGame(player);
			player.teleport(Lobby.getLobby().getSpawnLocation());
			if(inGame) {
				PlayerManager.removeSpectator(player);
				UHC.refreshScoreboards();
			}
		}
		return true;
	}
}

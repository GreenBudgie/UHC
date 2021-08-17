package ru.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.UHC.ArenaManager;
import ru.lobby.Lobby;
import ru.lobby.LobbyTeamBuilder;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandTeammate implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 0) {
				LobbyTeamBuilder.openRequestSendInventory(player);
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("leave")) {
					if(LobbyTeamBuilder.hasTeammate(player)) {
						LobbyTeamBuilder.disbandTeam(player);
					} else {
						player.sendMessage(LobbyTeamBuilder.PREFIX + ChatColor.RED + "Сейчас ты не в команде");
					}
				}
			}
			if(args.length == 2) {
				Player commandPlayer = Bukkit.getPlayer(args[1]);
				if(commandPlayer != null) {
					if(args[0].equalsIgnoreCase("accept")) {
						if(LobbyTeamBuilder.hasActiveRequestTo(commandPlayer, player)) {
							LobbyTeamBuilder.acceptIncomingRequest(player, commandPlayer);
						} else {
							player.sendMessage(LobbyTeamBuilder.PREFIX +
									ChatColor.GOLD + commandPlayer.getName() +
									ChatColor.RED + " не отправлял тебе запрос");
						}
					}
					if(args[0].equalsIgnoreCase("decline")) {
						if(LobbyTeamBuilder.hasActiveRequestTo(commandPlayer, player)) {
							LobbyTeamBuilder.declineIncomingRequest(player, commandPlayer);
						} else {
							player.sendMessage(LobbyTeamBuilder.PREFIX +
									ChatColor.GOLD + commandPlayer.getName() +
									ChatColor.RED + " не отправлял тебе запрос");
						}
					}
					if(args[0].equalsIgnoreCase("request")) {
						if(!LobbyTeamBuilder.hasActiveRequestTo(player, commandPlayer)) {
							LobbyTeamBuilder.makeRequest(player, commandPlayer);
						} else {
							player.sendMessage(LobbyTeamBuilder.PREFIX +
									ChatColor.RED + "Ты уже отправил запрос " +
									ChatColor.GOLD + commandPlayer.getName());
						}
					}
				} else {
					player.sendMessage(LobbyTeamBuilder.PREFIX + ChatColor.RED + "Такого игрока не существует");
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 1) {
				return MathUtils.getListOfStringsMatchingLastWord(args, "leave", "accept", "decline", "request");
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("accept") ||
						args[0].equalsIgnoreCase("decline")) {
					return MathUtils.getListOfStringsMatchingLastWord(args,
							LobbyTeamBuilder.getIncomingRequests(player).stream().map(Player::getName).toList());
				}
				if(args[0].equalsIgnoreCase("request")) {
					return MathUtils.getListOfStringsMatchingLastWord(args,
							Lobby.getLobby().getPlayers().stream().
							filter(currentPlayer -> !LobbyTeamBuilder.hasActiveRequestTo(player, currentPlayer) && player != currentPlayer).
							map(Player::getName).
							toList());
				}
			}
		}
		return new ArrayList<>();
	}
}

package ru.UHC;

import com.google.common.collect.Streams;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerHandler {

	private static List<Player> players = new ArrayList<>();
	private static List<Player> spectators = new ArrayList<>();

	public static boolean isPlaying(Player player) {
		return players.contains(player);
	}

	public static boolean isSpectating(Player player) {
		return spectators.contains(player);
	}

	public static boolean isInGame(Player player) {
		return isPlaying(player) || isSpectating(player);
	}

	public static List<Player> getPlayers() {
		return players;
	}

	public static List<Player> getSpectators() {
		return spectators;
	}

	public static List<Player> getInGamePlayers() {
		return Streams.concat(players.stream(), spectators.stream()).collect(Collectors.toList());
	}

}

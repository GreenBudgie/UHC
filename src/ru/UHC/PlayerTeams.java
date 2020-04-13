package ru.UHC;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTeams {

	private static List<List<Player>> teams = new ArrayList<>();

	public static List<List<Player>> getTeams() {
		return teams;
	}

	public static List<Player> formTeam(Player... players) {
		List<Player> playerList = Lists.newArrayList(players);
		if(playerList.stream().anyMatch(PlayerTeams::isTeamed)) throw new IllegalArgumentException("Cannot form a team with member(s) who already teamed");
		teams.add(playerList);
		return playerList;
	}

	public static boolean isTeamed(Player player) {
		return getTeam(player) != null;
	}

	public static List<Player> getAlivePlayers(List<Player> team) {
		List<Player> aliveMembers = Lists.newArrayList(team);
		return aliveMembers.stream().filter(PlayerHandler::isPlaying).collect(Collectors.toList());
	}

	public static List<Player> getDeadPlayers(List<Player> team) {
		List<Player> deadMembers = Lists.newArrayList(team);
		return deadMembers.stream().filter(player -> !PlayerHandler.isPlaying(player)).collect(Collectors.toList());
	}

	/**
	 * Gets the team of players in which this player is present
	 * @param player Player to search team for
	 * @return A <b>COPY</b> of list of players in the team; list containing one player if it's solo; null if player is not teamed
	 */
	public static List<Player> getTeam(Player player) {
		for(List<Player> team : teams) {
			if(team.contains(player)) {
				return Lists.newArrayList(team);
			}
		}
		return null;
	}

	/**
	 * Gets a List of player's teammates. In theory, this method returns the List of a team's members without the given player
	 * @param player A player to search teammates for
	 * @return List of player's teammates, empty List if player has no teammates, or null if player is not teamed
	 */
	public static List<Player> getTeammates(Player player) {
		List<Player> team = getTeam(player);
		if(team == null) return null;
		team.remove(player);
		return team;
	}

	/**
	 * Checks if the given players belongs to the same team
	 * @param players Players to check
	 * @return Whether the given players belongs to the same team
	 */
	public static boolean isTeammates(Player... players) {
		if(players.length == 0) throw new IllegalArgumentException("No players to check");
		if(players.length == 1) throw new IllegalArgumentException("Too few players to check");
		Player player1 = players[0];
		List<Player> team = getTeam(player1);
		if(team == null) return false;
		return team.containsAll(getTeammates(player1));
	}

}

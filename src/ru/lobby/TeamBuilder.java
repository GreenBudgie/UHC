package ru.lobby;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pre-game team builder. Handles invites and team-ups
 */
public class TeamBuilder {

	/**
	 * A Map that holds an information about invites and team-ups. If a player made an invitation to other player it marks as an INVITE for the player. If the player
	 * accepts the invite they are now considered as TEAMMATES. In other words, if both players has an invite to each other so they are an accepted team.
	 */
	private static Map<Player, List<Player>> teammateChoices = new HashMap<>();

	private static List<Player> getChoices(Player player) {
		return teammateChoices.getOrDefault(player, new ArrayList<>());
	}

	/**
	 * Gets the list of pending invitations that has been done by this player. NOTE: This method does not include accepted invites!
	 * @return List of players that has an invitation from this player
	 */
	public static List<Player> getInvitedPlayers(Player player) {

	}

	/**
	 * Gets all teams made by players
	 */
	public static List<List<Player>> getTeams() {
		
	}

	/**
	 * Checks if the given player is invited, but NOT currently in team with the invitor
	 */
	public static boolean isInvited(Player player, Player invitor) {
		for(Player choice : getChoices(invitor)) {
			if(choice == player && !getChoices(player).contains(invitor)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given players are in team
	 */
	public static boolean isTeamed(Player... players) {
		if(players.length < 2) throw new IllegalArgumentException("Cannot check less that 2 players");
		for(Player choice : getChoices(players[0])) {
			for(int i = 1; i < players.length; i++) {
				Player currentPlayer = players[i];
				if(currentPlayer == choice) {
					if(getChoices(currentPlayer).contains(players[0])) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean isTeamed() {

	}

	public static void invite(Player invitor, Player invited) {

	}

}

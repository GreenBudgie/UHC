package ru.greenbudgie.UHC;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerTeam {

	private final UHCPlayer player1;
	private UHCPlayer player2;

	public PlayerTeam(UHCPlayer player1) {
		this.player1 = player1;
		this.player2 = null;
	}

	public PlayerTeam(UHCPlayer player1, UHCPlayer player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public void addTeammate(UHCPlayer player2) {
		if(this.player2 == null) {
			this.player2 = player2;
		}
	}

	public UHCPlayer getPlayer1() {
		return player1;
	}

	public UHCPlayer getPlayer2() {
		return player2;
	}

	public boolean contains(UHCPlayer p) {
		return player1 == p || player2 == p;
	}

	public boolean contains(Player p) {
		return player1.getPlayer() == p || (player2 != null && player2.getPlayer() == p);
	}

	public boolean isDual() {
		return player2 != null;
	}

	/**
	 * Checks whether the current team has at least one member alive, even if he is not on server
	 */
	public boolean isAlive() {
		return player1.isAlive() || (player2 != null && player2.isAlive());
	}

	public int size() {
		return isDual() ? 2 : 1;
	}

	public List<UHCPlayer> getPlayers() {
		return isDual() ? Lists.newArrayList(player1, player2) : Lists.newArrayList(player1);
	}

	public void sendMessage(String str) {
		for(UHCPlayer p : getPlayers()) {
			p.sendMessage(str);
		}
	}

}

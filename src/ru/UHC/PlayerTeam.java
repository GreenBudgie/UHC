package ru.UHC;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerTeam {

	private Player player1;
	private Player player2;

	public PlayerTeam(Player player1) {
		this.player1 = player1;
		this.player2 = null;
	}

	public PlayerTeam(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public void remove(Player p) {
		if(p == player1) {
			player1 = player2;
			player2 = null;
		}
		if(p == player2) player2 = null;
	}

	public boolean isEmpty() {
		return player1 == null && player2 == null;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public boolean contains(Player p) {
		return player1 == p || player2 == p;
	}

	public boolean isDual() {
		return player2 != null && player1 != null;
	}

	public int size() {
		return isDual() ? 2 : (isEmpty() ? 0 : 1);
	}

	public List<Player> getPlayers() {
		return isDual() ? Lists.newArrayList(player1, player2) : (isEmpty() ? Lists.newArrayList() : Lists.newArrayList(player1));
	}

	public void sendMessage(String str) {
		for(Player p : getPlayers()) {
			p.sendMessage(str);
		}
	}

}

package ru.UHC;

import org.bukkit.ChatColor;

public enum GameMode {

	SOLO(ChatColor.AQUA + "Solo"), DUO(ChatColor.LIGHT_PURPLE + "Duo"), TRIPLES(ChatColor.DARK_PURPLE + "Triples"), TEAMS(ChatColor.DARK_GREEN + "Teams");

	private static GameMode mode = SOLO;

	private String name;

	GameMode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return mode == this;
	}

	public static void setMode(GameMode mode) {
		GameMode.mode = mode;
	}

	public static GameMode switchMode() {
		for(int i = 0; i < GameMode.values().length; i++) {
			GameMode current = GameMode.values()[i];
			if(current == mode) {
				if(i == GameMode.values().length - 1) {
					mode = GameMode.values()[0];
				} else {
					mode = GameMode.values()[i + 1];
				}
				break;
			}
		}
		return mode;
	}

	public static GameMode getMode() {
		return mode;
	}

}

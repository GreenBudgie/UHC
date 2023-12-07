package ru.greenbudgie.UHC;

public enum GameState {

	STOPPED, VOTE, PREPARING, OUTBREAK, GAME, DEATHMATCH, ENDING;

	public boolean isPreGame() {
		return this == VOTE || this == PREPARING;
	}

	public boolean isBeforeDeathmatch() {
		return this == OUTBREAK || this == GAME;
	}

	public boolean isDeathmatch() {
		return this == DEATHMATCH;
	}

	public boolean isGameActive() {
		return isBeforeDeathmatch() || isDeathmatch();
	}

}

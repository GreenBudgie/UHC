package ru.greenbudgie.UHC;

public enum GameState {

	STOPPED, VOTE, PREPARING, OUTBREAK, GAME, DEATHMATCH, ENDING;

	public boolean isPreGame() {
		return this == VOTE || this == PREPARING;
	}

	public boolean isInGame() {
		return this == OUTBREAK || this == GAME;
	}

}

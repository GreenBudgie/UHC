package ru.UHC;

import java.util.stream.Stream;

public enum GameState {

	STOPPED, VOTE, PREPARING, OUTBREAK, GAME, DEATHMATCH, ENDING;

	private static GameState state = STOPPED;

	public void set() {
		setState(this);
	}

	public boolean isActive() {
		return isState(this);
	}

	public static boolean isState(GameState state) {
		return GameState.state == state;
	}

	public static boolean isState(GameState... states) {
		return Stream.of(states).anyMatch(GameState::isState);
	}

	public static void setState(GameState state) {
		GameState.state = state;
	}

	public static GameState getState() {
		return state;
	}

	public static boolean isPreGame() {
		return state == VOTE || state == PREPARING;
	}

	public static boolean isInGame() {
		return state == OUTBREAK || state == GAME;
	}

	public static boolean isPlaying() {
		return state != STOPPED;
	}

}

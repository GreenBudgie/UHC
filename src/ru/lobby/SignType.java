package ru.lobby;

public enum SignType {

	GAME_START, GAME_DUO, GAME_MAP_SIZE, GAME_DURATION, GAME_RATING, GAME_FAST_START, GAME_ARENA,
	SPECTATE, TEAMMATE_SELECT, MAP_GENERATE, ARENA_NEXT_KIT;

	public boolean canAnyoneUse() {
		return this == SPECTATE || this == TEAMMATE_SELECT;
	}

	public boolean canUseWhilePlaying() {
		return this == SPECTATE || this == ARENA_NEXT_KIT;
	}

}

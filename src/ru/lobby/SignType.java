package ru.lobby;

public enum SignType {

	GAME_START, GAME_DUO, GAME_MAP_SIZE, GAME_DURATION, GAME_STATS, GAME_FAST_START, GAME_ARENA, SPECTATE, TEAMMATE_SELECT, MAP_GENERATE, RETURN_LOBBY, ARENA_TP, ARENA_NEXT_KIT;

	public boolean canAnyoneUse() {
		return this == RETURN_LOBBY || this == ARENA_TP || this == SPECTATE || this == TEAMMATE_SELECT;
	}

	public boolean canUseWhilePlaying() {
		return this == SPECTATE || this == RETURN_LOBBY || this == ARENA_TP || this == ARENA_NEXT_KIT;
	}

}

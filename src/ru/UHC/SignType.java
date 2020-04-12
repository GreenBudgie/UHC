package ru.UHC;

public enum SignType {

	GAME_START, REGEN, SIZE, DURATION, STATS, DUO, TRIDENT_TP, RETURN_LOBBY, ARENA_TP, NEXT_KIT,
	FAST_START;

	public boolean canAnyoneUse() {
		return this == RETURN_LOBBY || this == TRIDENT_TP || this == ARENA_TP;
	}

}

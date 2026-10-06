package ru.greenbudgie.configuration;

public enum GameDuration {

    DEFAULT(10, 30),
    OLD(15, 55);

    private final int noPvpDurationMinutes;
    private final int gameDurationMinutes;

    GameDuration(int noPvpDurationMinutes, int gameDurationMinutes) {
        this.noPvpDurationMinutes = noPvpDurationMinutes;
        this.gameDurationMinutes = gameDurationMinutes;
    }

    public int getNoPvpDurationMinutes() {
        return noPvpDurationMinutes;
    }

    public int getGameDurationMinutes() {
        return gameDurationMinutes;
    }

}

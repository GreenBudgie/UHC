package ru.greenbudgie.configuration;

public enum GameDuration {

    SHORT(10, 35),
    DEFAULT(15, 55),
    LONG(20, 70);

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

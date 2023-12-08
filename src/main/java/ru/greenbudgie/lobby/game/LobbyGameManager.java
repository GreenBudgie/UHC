package ru.greenbudgie.lobby.game;

import ru.greenbudgie.lobby.game.arena.LobbyGamePvpArena;
import ru.greenbudgie.lobby.game.parkour.LobbyGameParkour;

import java.util.ArrayList;
import java.util.List;

public class LobbyGameManager {

    protected static List<LobbyGame> lobbyGames = new ArrayList<>();

    public static final LobbyGamePvpArena PVP_ARENA = new LobbyGamePvpArena();

    public static final LobbyGameParkour PARKOUR = new LobbyGameParkour();

    public static void init() {
        for(LobbyGame game : lobbyGames) {
            game.parseConfig();
            game.postSetup();
        }
    }

    public static void updateGames() {
        for(LobbyGame game : lobbyGames) {
            game.update();
        }
    }

}

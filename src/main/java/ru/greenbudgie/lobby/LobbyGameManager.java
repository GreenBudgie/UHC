package ru.greenbudgie.lobby;

import java.util.ArrayList;
import java.util.List;

public class LobbyGameManager {

    protected static List<LobbyGame> lobbyGames = new ArrayList<>();

    public static final PvpArena PVP_ARENA = new PvpArena();

    protected static void init() {
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

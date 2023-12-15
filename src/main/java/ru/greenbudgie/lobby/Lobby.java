package ru.greenbudgie.lobby;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.SafeTeleport;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.game.LobbyGameManager;
import ru.greenbudgie.lobby.sign.LobbySign;
import ru.greenbudgie.lobby.sign.SignManager;
import ru.greenbudgie.main.UHCPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class Lobby {

    private static YamlConfiguration lobbyConfig;
    private static World lobby;

    public static void init() {
        lobby = Bukkit.createWorld(new WorldCreator("Lobby"));
        lobby.setDifficulty(Difficulty.NORMAL);
        lobby.setPVP(true);
        lobby.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        lobby.setGameRule(GameRule.NATURAL_REGENERATION, false);
        lobby.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        lobby.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        removeMapDatFiles();

        boolean toUpdate = false;
        File file = new File(getLobby().getWorldFolder() + File.separator + "lobby.yml");
        try {
            if(!file.exists()) file.createNewFile();
        } catch(Exception e) {
            UHCPlugin.error("Cannot create lobby.yml file");
        }
        lobbyConfig = YamlConfiguration.loadConfiguration(file);
        if(!lobbyConfig.contains("signs")) {
            UHCPlugin.warning("No lobby signs are declared in the config");
            lobbyConfig.createSection("signs");
            toUpdate = true;
        }
        ConfigurationSection signsSection = lobbyConfig.getConfigurationSection("signs");
        for(LobbySign lobbySign : SignManager.getSigns()) {
            if(!signsSection.contains(lobbySign.getConfigName()) ||
                    signsSection.getStringList(lobbySign.getConfigName()).isEmpty()) {
                UHCPlugin.warning("There are no locations declared for sign type " + lobbySign.getConfigName());
                signsSection.set(lobbySign.getConfigName(), new ArrayList<>());
                toUpdate = true;
            }
        }
        try {
            if (toUpdate) lobbyConfig.save(file);
        } catch(Exception e) {
            UHCPlugin.error("Unable to save lobby.yml");
        }
        LobbyTeamBuilder.init();
        LobbyGameManager.init();
        LobbyMapPreview.init();
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), UHCPlugin.instance);
        Bukkit.getPluginManager().registerEvents(new LobbyTeamBuilder(), UHCPlugin.instance);
    }

    private static void removeMapDatFiles() {
        File dataFolder = new File(lobby.getWorldFolder().getAbsolutePath() + File.separator + "data");
        for(int i = 0;; i++) {
            File mapDat = new File(dataFolder.getAbsolutePath() + File.separator + "map_" + i + ".dat");
            try {
                if(!mapDat.delete()) {
                    break;
                }
            } catch(Exception ignored) {
                break;
            }
        }
        File idcounts = new File(dataFolder.getAbsolutePath() + File.separator + "idcounts.dat");
        try {
            idcounts.delete();
        } catch(Exception ignored) {}
    }

    public static YamlConfiguration getLobbyConfig() {
        return lobbyConfig;
    }

    public static World getLobby() {
        return lobby;
    }

    public static boolean isInLobby(Player player) {
        return player.getWorld() == getLobby();
    }

    public static boolean isWatchingArena(Player player) {
        for(ArenaManager.Arena arena : ArenaManager.getArenas()) {
            if(player.getWorld() == arena.getWorld()) return true;
        }
        return false;
    }

    public static List<Player> getPlayersInLobbyAndArenas() {
        List<Player> lobbyPlayers = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(isInLobbyOrWatchingArena(player)) lobbyPlayers.add(player);
        }
        return lobbyPlayers;
    }

    public static boolean isInLobbyOrWatchingArena(Player player) {
        return isInLobby(player) || isWatchingArena(player);
    }

    /**
     * Returns player to lobby spawn location if it is possible and does some additional work if needed.
     *
     * @param player The player to teleport to lobby
     */
    public static void returnPlayerToLobby(Player player) {
        if (PlayerManager.isPlaying(player)) {
            player.sendMessage(DARK_RED + "" + BOLD + "- Нельзя выйти в лобби во время игры! -");
            return;
        }
        if (PlayerManager.isSpectator(player)) {
            returnSpectatorToLobby(player);
            return;
        }
        if (isInLobbyOrWatchingArena(player)) {
            player.teleport(Lobby.getLobby().getSpawnLocation());
        }
    }

    private static void returnSpectatorToLobby(Player spectator) {
        UHC.resetPlayer(spectator);
        spectator.setGameMode(GameMode.ADVENTURE);
        SafeTeleport.performSafeTeleport(spectator, Lobby.getLobby().getSpawnLocation());
        PlayerManager.unregisterSpectator(spectator);
        for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
            inGamePlayer.sendMessage(
                    DARK_AQUA + "" + BOLD + "- " + GOLD + spectator.getName() + AQUA + " перестал наблюдать за игрой"
            );
        }
        UHC.refreshScoreboards();
        Bukkit.getPluginManager().callEvent(new SpectatorReturnToLobbyEvent(spectator));
    }

}

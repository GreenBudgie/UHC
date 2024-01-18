package ru.greenbudgie.UHC;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import ru.greenbudgie.classes.ClassManager;
import ru.greenbudgie.configuration.GameType;
import ru.greenbudgie.event.SpectatorJoinEvent;
import ru.greenbudgie.event.SpectatorLeaveEvent;
import ru.greenbudgie.lobby.LobbyTeamBuilder;
import ru.greenbudgie.rating.GameSummary;
import ru.greenbudgie.rating.PlayerSummary;
import ru.greenbudgie.rating.Rating;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerManager {

    private static final List<UHCPlayer> players = new ArrayList<>();
    private static final List<PlayerTeam> teams = new ArrayList<>();
    private static final List<Player> spectators = new ArrayList<>();

    public static UHCPlayer registerPlayer(Player player) {
        UHCPlayer uhcPlayer = new UHCPlayer(player);
        players.add(uhcPlayer);
        GameSummary gameSummary = Rating.getCurrentGameSummary();
        PlayerSummary playerSummary = gameSummary.addPlayerSummary(player.getName());
        uhcPlayer.setSummary(playerSummary);
        if(GameType.getType().allowsClasses()) {
            uhcPlayer.setUHCClass(ClassManager.getClassInLobby(player));
        }
        if (!UHC.isDuo) {
            teams.add(new PlayerTeam(uhcPlayer));
            return uhcPlayer;
        }
        Player teammate = LobbyTeamBuilder.getTeammate(player);
        if (teammate == null) {
            teams.add(new PlayerTeam(uhcPlayer));
            return uhcPlayer;
        }
        UHCPlayer uhcTeammate = asUHCPlayer(teammate);
        if(uhcTeammate != null) {
            uhcPlayer.setTeammate(uhcTeammate);
            uhcTeammate.setTeammate(uhcPlayer);
        }
        PlayerTeam teammateTeam = getTeamWithMember(teammate);
        if(teammateTeam == null) {
            teams.add(new PlayerTeam(uhcPlayer));
        } else {
            teammateTeam.addTeammate(uhcPlayer);
        }
        return uhcPlayer;
    }

    public static PlayerTeam getTeamWithMember(UHCPlayer player) {
        for(PlayerTeam team : teams) {
            if(team.contains(player)) return team;
        }
        return null;
    }

    public static PlayerTeam getTeamWithMember(Player player) {
        for(PlayerTeam team : teams) {
            if(team.contains(player)) return team;
        }
        return null;
    }

    @Nullable
    public static UHCPlayer asUHCPlayer(Player player) {
        if(player == null) return null;
        for(UHCPlayer uplayer : players) {
            if(uplayer.getPlayer() == player || uplayer.getNickname().equals(player.getName())) return uplayer;
        }
        return null;
    }

    public static UHCPlayer asUHCPlayer(String nickname) {
        for(UHCPlayer uplayer : players) {
            if(uplayer.getNickname().equals(nickname)) return uplayer;
        }
        return null;
    }

    /**
     * Unregisters the spectator. It does not update the player in any way, e.g. it does not clear effects or
     * teleport him to lobby. This method just removes the player from spectator list and calls an event.
     */
    public static void unregisterSpectator(Player spectator) {
        if (!spectators.remove(spectator)) {
            throw new IllegalStateException(spectator.getName() + " is not a spectator!");
        }
        Bukkit.getPluginManager().callEvent(new SpectatorLeaveEvent(spectator));
    }

    /**
     * Registers the spectator. It does not update the player in any way, e.g. it does not clear effects or
     * teleport him to lobby. This method just removes the player from spectator list and calls an event.
     */
    public static void registerSpectator(Player player) {
        if (spectators.contains(player)) {
            throw new IllegalStateException(player.getName() + " is already a spectator!");
        }
        spectators.add(player);
        Bukkit.getPluginManager().callEvent(new SpectatorJoinEvent(player));
    }

    /**
     * Gets the list of UHC players.
     * This list will never change throughout the game, even if a player dies or leaves.
     */
    public static List<UHCPlayer> getPlayers() {
        return players;
    }

    @Nullable
    public static UHCPlayer getPlayerByNickname(String nickname) {
        return getPlayers().stream().filter(player -> player.getNickname().equals(nickname)).findAny().orElse(null);
    }

    /**
     * Gets the list of players that are currently alive.
     * This method does not include spectators or dead players.
     * This method INCLUDES offline players.
     */
    public static List<UHCPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(UHCPlayer::isAlive).toList();
    }

    /**
     * Gets the list of players that are currently online and alive.
     * This method does not include spectators or dead players.
     */
    public static List<Player> getAliveOnlinePlayers() {
        return asPlayerList(getPlayers().stream().filter(UHCPlayer::isAliveAndOnline).toList());
    }

    /**
     * Gets the list of players that are currently online and not in lobby.
     * This method INCLUDES spectators and dead players.
     * --- Literally anyone, who is not in lobby ---
     */
    public static List<Player> getInGamePlayersAndSpectators() {
        List<Player> allPlayers = Lists.newArrayList(getAliveOnlinePlayers());
        allPlayers.addAll(getSpectators());
        return allPlayers;
    }

    /**
     * Gets the list of player teams.
     * This list will never change throughout the game, even if a player dies or leaves.
     */
    public static List<PlayerTeam> getTeams() {
        return teams;
    }

    /**
     * Gets the list of spectators.
     * This list may be changed as some players become spectators after dying, or if one joins from lobby.
     */
    public static List<Player> getSpectators() {
        return spectators;
    }

    public static UHCPlayer getPlayerFromGhost(ArmorStand ghost) {
        for(UHCPlayer uplayer : getPlayers()) {
            if(uplayer.getGhost() == ghost) return uplayer;
        }
        return null;
    }

    public static Player getTeammate(Player player) {
        UHCPlayer uplayer = asUHCPlayer(player);
        if(uplayer == null || uplayer.getTeammate() == null) return null;
        return uplayer.getTeammate().getPlayer();
    }

    public static UHCPlayer getUHCTeammate(Player player) {
        UHCPlayer uplayer = asUHCPlayer(player);
        if(uplayer == null) return null;
        return uplayer.getTeammate();
    }

    public static boolean isTeammates(Player player1, Player player2) {
        UHCPlayer uplayer1 = asUHCPlayer(player1);
        UHCPlayer uplayer2 = asUHCPlayer(player2);
        if(uplayer1 == null || uplayer2 == null) return false;
        return isTeammates(uplayer1, uplayer2);
    }

    public static boolean isTeammates(UHCPlayer player1, UHCPlayer player2) {
        if(player1 == null || player2 == null) return false;
        return player1.getTeammate() == player2;
    }

    /**
     * Checks whether the given player is playing the game right now.
     * The player in the game must be alive for this method to return true,
     * so as the dead player is a spectator.
     */
    public static boolean isPlaying(Player player) {
        UHCPlayer uplayer = asUHCPlayer(player);
        if(uplayer == null) return false;
        return uplayer.isAlive();
    }

    /**
     * Checks whether the given player is in game as a player or a spectator
     */
    public static boolean isInGame(Player player) {
        return isPlaying(player) || isSpectator(player);
    }

    /**
     * Checks whether the given player is a spectator.
     */
    public static boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    /**
     * Gets the list of teams where at least one player is still alive, even if he is not on server.
     */
    public static List<PlayerTeam> getAliveTeams() {
        return teams.stream().filter(PlayerTeam::isAlive).toList();
    }

    public static void clear() {
        players.clear();
        teams.clear();
        spectators.clear();
    }

    public static List<Player> asPlayerList(List<UHCPlayer> uhcPlayerList) {
        return uhcPlayerList.stream().map(UHCPlayer::getPlayer).filter(Objects::nonNull).toList();
    }

}

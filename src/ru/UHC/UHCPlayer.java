package ru.UHC;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class UHCPlayer {

    private final UUID uuid;
    private String nickname;
    private Player player;
    private State state = State.PLAYING;
    private ArmorStand ghost = null;
    private PlayerInventory savedInventory = null;
    private UHCPlayer teammate = null;

    private UHCPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.nickname = player.getName();
    }

    public static UHCPlayer asPlayer(Player player) {
        return new UHCPlayer(player);
    }

    public static UHCPlayer asSpectator(Player player) {
        UHCPlayer uplayer = new UHCPlayer(player);
        uplayer.state = State.SPECTATOR_IN_GAME;
        return uplayer;
    }

    public void leave() {
        state = State.LEFT_AND_ALIVE;
    }

    public void rejoin() {

    }

    private void createGhost() {

    }

    private void saveInventory() {

    }

    public void deathInGame() {

    }

    public void deathWhileLeft() {

    }

    public void moveToSpectators() {

    }

    public boolean isOnServer() {
        return state == State.PLAYING;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public Player getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }

    public ArmorStand getGhost() {
        return ghost;
    }

    public PlayerInventory getSavedInventory() {
        return savedInventory;
    }

    public boolean hasTeammate() {
        return teammate != null;
    }

    public UHCPlayer getTeammate() {
        return teammate;
    }

    public enum State {
        PLAYING, LEFT_AND_ALIVE, LEFT_AND_DEAD, SPECTATOR_IN_GAME, SPECTATOR_IN_LOBBY, SPECTATOR_LEFT
    }

}

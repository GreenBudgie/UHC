package ru.UHC;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import ru.lobby.Lobby;
import ru.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UHCPlayer {

    private final String nickname;
    private Player player;
    private State state = State.PLAYING;
    private ArmorStand ghost = null;
    private ItemStack[] savedInventory = null;
    private UHCPlayer teammate = null;

    private Player ghostKiller;
    private final int maxTimeToRejoin = 3 * 60;
    private int timeToRejoin = 0;
    private boolean deadByTime = false;
    private int leavesRemaining = 3;

    private double offlineHealth;

    public UHCPlayer(Player player) {
        this.player = player;
        this.nickname = player.getName();
    }

    public void leave() {
        if(state == State.PLAYING) {
            if(UHC.state.isPreGame()) {
                //The player cannot leave while the game is preparing
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 2, 1);
                for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                    player.sendMessage(
                            FightHelper.padCrosses(ChatColor.GOLD + nickname +
                                    ChatColor.RED + " вылетел с серва"));
                }
                moveToSpectators();
                if(PlayerManager.getAlivePlayers().size() <= 0) {
                    UHC.endGame();
                }
            } else if(UHC.state == GameState.ENDING) {
                moveToSpectators();
            } else {
                for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                    player.sendMessage(ChatColor.GOLD + nickname + ChatColor.RED + " вышел из игры");
                }
                if(!player.isOnGround()) {
                    int playerY = player.getLocation().getBlockY();
                    int highestY = player.getWorld().getHighestBlockYAt(player.getLocation());
                    int differenceY = (playerY - highestY) + (int) player.getFallDistance();
                    if(differenceY >= 7) {
                        player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.FALL, 100));
                        kill();
                        state = State.LEFT_AND_DEAD;
                    }
                }
                if(leavesRemaining <= 0) {
                    kill();
                    state = State.LEFT_AND_DEAD;
                } else {
                    if(state == State.PLAYING) {
                        leavesRemaining--;
                        timeToRejoin = maxTimeToRejoin;
                        state = State.LEFT_AND_ALIVE;
                        offlineHealth = player.getHealth();
                        createGhost();
                        saveInventory();
                    }
                }
            }
        }
        player = null;
    }

    public void rejoin(Player joinedPlayer) {
        this.player = joinedPlayer;
        if(ghost != null) {
            player.teleport(ghost.getLocation());
            ghost.remove();
            ghost = null;
        }
        if(state == State.LEFT_AND_ALIVE) {
            state = State.PLAYING;
            for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                player.sendMessage(ChatColor.GOLD + nickname + ChatColor.DARK_GREEN + " вернулся в игру");
            }
            player.setHealth(offlineHealth);
            String timesLeft = new NumericalCases("раз", "раза", "раз").byNumber(leavesRemaining);
            player.sendMessage(ChatColor.GRAY + "- " +
                            ChatColor.DARK_RED + "Ты можешь перезайти еще " +
                            ChatColor.DARK_AQUA + ChatColor.BOLD + leavesRemaining + " " +
                            ChatColor.RESET + ChatColor.DARK_RED + timesLeft +
                            ChatColor.GRAY + "!");
        } else if(state == State.LEFT_AND_DEAD) {
            moveToSpectators();
            for(Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                player.sendMessage(ChatColor.GOLD + nickname + ChatColor.DARK_GREEN + " вернулся в игру, к сожалению, мертвым");
            }
        }
    }

    public void update() {
        if(TaskManager.isSecUpdated()) {
            if(state == State.LEFT_AND_ALIVE) {
                timeToRejoin--;
                if(timeToRejoin == 60 && ghost != null) {
                    ghost.setCustomNameVisible(true);
                }
                if(timeToRejoin <= 0) {
                    deadByTime = true;
                    kill();
                }
            }
        }
    }

    public void damageGhost(Player damager) {
        if(state == State.LEFT_AND_ALIVE) {
            ghostKiller = damager;
            kill();
        }
    }

    private void createGhost() {
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        stand.setCustomName(ChatColor.AQUA + this.nickname);
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setArms(true);
        stand.getEquipment().setHelmet(ItemUtils.getHead(player));
        stand.getEquipment().setChestplate(player.getInventory().getChestplate());
        stand.getEquipment().setLeggings(player.getInventory().getLeggings());
        stand.getEquipment().setBoots(player.getInventory().getBoots());
        stand.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
        stand.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());
        this.ghost = stand;
    }

    private void saveInventory() {
        savedInventory = player.getInventory().getContents();
    }

    public void kill() {
        if(state == State.SPECTATING) return;
        if(player != null) {
            deathInGame();
        } else {
            deathWhileLeft();
        }
    }

    /**
     * Called when a player or its ghost dies
     */
    public void initiateDeath() {
        dropBonusItemOnDeath();
        showDeathMessage();

        //Announce the place to players
        UHCPlayer teammate = getTeammate();
        PlayerTeam team = PlayerManager.getTeamWithMember(this);
        int remainingTeams = PlayerManager.getAliveTeams().size();
        if(remainingTeams <= 2 && remainingTeams >= 1 && !team.isAlive()) {
            String place = remainingTeams == 2 ?
                    ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "третье" :
                    ChatColor.AQUA + "" + ChatColor.BOLD + "второе";
            String info = ChatColor.YELLOW + "Ты занял " + place + ChatColor.RESET + ChatColor.YELLOW + " место!";
            sendMessage(info);
            if(teammate != null) {
                teammate.sendMessage(info);
            }
        }

        //Death effects
        Location location = getLocation();
        if(location != null) {
            location.getWorld().strikeLightningEffect(location);
            location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, Float.MAX_VALUE, 0.8F);
        }

        UHC.recalculateTimeOnPlayerDeath();
        UHC.tryWin();
    }

    private void dropInventory() {
        Location location = getLocation();
        if(location == null) return;
        List<ItemStack> itemsToDrop = new ArrayList<>();
        if(player != null) {
            itemsToDrop.addAll(Arrays.asList(player.getInventory().getContents()));
        } else {
            if(savedInventory != null) {
                itemsToDrop.addAll(Arrays.asList(savedInventory));
            }
        }
        for(ItemStack item : itemsToDrop) {
            if(item != null && item.getType() != Material.AIR) {
                location.getWorld().dropItemNaturally(location, item);
            }
        }
    }

    private void dropBonusItemOnDeath() {
        Location location = getLocation();
        Player killer = getKiller();
        if(killer != null) {
            location.getWorld().dropItem(location, UHC.getBonusShell());
            return;
        }
        boolean golden = UHC.state == GameState.OUTBREAK;
        ItemStack apple = ItemUtils.
                builder(golden ? Material.GOLDEN_APPLE : Material.APPLE).
                withName(golden ? (ChatColor.DARK_GREEN + "Золотое бонусное яблоко") : (ChatColor.GREEN + "Бонусное яблоко")).
                withGlow(!golden).
                build();
        apple = ItemUtils.setCustomValue(apple, "owner", nickname);
        location.getWorld().dropItem(location, apple);
    }

    private void showDeathMessage() {
        if(deadByTime) {
            for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                inGamePlayer.sendMessage(FightHelper.padCrosses(ChatColor.GOLD + nickname + ChatColor.RED + " не успел вернуться в игру"));
            }
            return;
        }
        if(leavesRemaining == 0) {
            for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                inGamePlayer.sendMessage(FightHelper.padCrosses(ChatColor.GOLD + nickname + ChatColor.RED + " слишком часто ливал"));
            }
            return;
        }
        Player killer = getKiller();
        if(killer != null) {
            if(player != null) {
                for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                    inGamePlayer.sendMessage(FightHelper.getDeathMessage(player));
                }
                String taunt = ChatColor.DARK_AQUA + " " + MathUtils.choose("тебя унизил", "умнее тебя", "не такой тупой, как кажется",
                        "иногда проявляет себя", "играет в кубики лучше тебя", "обосрал тебя", "убил тебя", "просто повезло");
                player.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Тебя замачили!", ChatColor.GOLD + killer.getName() + taunt, 10, 60, 20);
            } else {
                for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                    String deathMessage = FightHelper.padCrosses(
                            ChatColor.GOLD + killer.getName() +
                            ChatColor.RED + " замачил отлучившегося " +
                            ChatColor.GOLD + this.nickname);
                    inGamePlayer.sendMessage(deathMessage);
                }
            }
        } else {
            if(player != null) {
                if(player.getLastDamageCause() != null) {
                    for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                        inGamePlayer.sendMessage(UHC.getDeathMessage(player, player.getLastDamageCause().getCause()));
                    }
                }
                player.sendTitle(ChatColor.DARK_RED + "Ты погиб!", "", 10, 60, 20);
            } else {
                for(Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                    String deathMessage = FightHelper.padCrosses(
                            ChatColor.GOLD + this.nickname +
                                    ChatColor.RED + " погиб, не находясь на сервере");
                    inGamePlayer.sendMessage(deathMessage);
                }
            }
        }
    }

    public Player getKiller() {
        if(player != null) {
            return FightHelper.getKiller(player);
        }
        return ghostKiller;
    }

    /**
     * Gets the location taking the online state into account.
     * That means, if player is not on server, this will return ghost's location.
     * Returns null if the player is spectator and not on server.
     */
    public Location getLocation() {
        if(player != null) return player.getLocation();
        if(ghost != null) return ghost.getLocation();
        return null;
    }

    /**
     * Changes the offline health level of the player.
     * Changed offline health will be given to rejoined player.
     * If offline health reaches 0, player dies immediately.
     */
    public void setOfflineHealth(double value) {
        if(state == State.LEFT_AND_ALIVE) {
            offlineHealth = value;
            if(offlineHealth >= 20) offlineHealth = 20;
            if(offlineHealth <= 0) kill();
        }
    }

    /**
     * Changes the offline health level of the player.
     * Changed offline health will be given to rejoined player.
     * If offline health reaches 0, player dies immediately.
     * @param value May be negative to reduce health
     */
    public void addOfflineHealth(double value) {
       setOfflineHealth(offlineHealth + value);
    }

    /**
     * Gets the offline health value.
     * This value is set when the player leaves the game.
     * Can be changed via game events: artifacts, mutators, etc.
     */
    public double getOfflineHealth() {
        return offlineHealth;
    }

    /**
     * Teleports the current player or its ghost to specified location
     */
    public void teleport(Location location) {
        if(player != null) {
            player.teleport(location);
        } else if(ghost != null) {
            ghost.teleport(location);
        }
    }

    /**
     * Called when a player dies while playing
     */
    public void deathInGame() {
        moveToSpectators();
        initiateDeath();
    }

    /**
     * Called when a player's ghost 'dies' while player is not on server
     */
    public void deathWhileLeft() {
        state = State.LEFT_AND_DEAD;
        if(ghost != null) {
            ParticleUtils.createParticlesAround(ghost, Particle.REDSTONE, Color.fromRGB(100, 0, 0), 20);
            ghost.getWorld().playSound(ghost.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1, 1);
            ghost.remove();
        }
        dropInventory();
        initiateDeath();
    }

    public void moveToSpectators() {
        state = State.SPECTATING;
        if(player != null) {
            if(player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
            PlayerManager.addSpectator(player);
        }
    }

    public void sendMessage(String message) {
        if(player != null) {
            player.sendMessage(message);
        }
    }

    /**
     * Checks whether the current player is alive.
     * Player can be alive and not on server, so do not use this method to check if player is present.
     * Also, a player can be dead on not on server.
     */
    public boolean isAlive() {
        return state == State.PLAYING || state == State.LEFT_AND_ALIVE;
    }

    /**
     * Checks whether the current player is alive and currently on server.
     */
    public boolean isAliveAndOnline() {
        return player != null && state == State.PLAYING;
    }

    /**
     * Checks whether the current player is registered as "uhc player" and currently somewhere on server.
     */
    public boolean isOnline() {
        return player != null;
    }

    /**
     * Checks whether the current player is registered as "uhc player" and currently not in lobby.
     */
    public boolean isInGame() {
        return player != null && !Lobby.isInLobby(player);
    }

    /**
     * Checks whether the current player is now a spectator.
     * This method can only check if a player became a spectator after dying in a game.
     * Spectators who connected from lobby do not count.
     */
    public boolean isSpectator() {
        return state == State.SPECTATING;
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

    public boolean hasTeammate() {
        return teammate != null;
    }

    public UHCPlayer getTeammate() {
        return teammate;
    }

    public void setTeammate(UHCPlayer teammate) {
        this.teammate = teammate;
    }

    public enum State {
        PLAYING, LEFT_AND_ALIVE, LEFT_AND_DEAD, SPECTATING
    }

}

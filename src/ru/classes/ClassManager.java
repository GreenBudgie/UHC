package ru.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.UHC.PlayerManager;
import ru.UHC.PlayerOptionHolder;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.lobby.Lobby;
import ru.main.UHCPlugin;
import ru.util.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassManager implements Listener {

    private static final String INV_NAME = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Классы";
    public static final List<UHCClass> classes = new ArrayList<>();
    public static final Map<String, UHCClass> lobbyPlayerClasses = new HashMap<>();

    public static final ClassArcher ARCHER = new ClassArcher();
    public static final ClassDemon DEMON = new ClassDemon();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ClassManager(), UHCPlugin.instance);
        for(Player player : Lobby.getLobby().getPlayers()) {
            UHCClass uhcClass = PlayerOptionHolder.getSelectedClass(player);
            if(uhcClass != null) {
                lobbyPlayerClasses.put(player.getName(), uhcClass);
                UHC.updateLobbyScoreboard(player);
            }
        }
    }

    public static UHCClass getClassByConfigName(String configName) {
        for(UHCClass uhcClass : classes) {
            if(uhcClass.getConfigName().equals(configName)) return uhcClass;
        }
        return null;
    }

    /**
     * Gets the class that the current player uses in game
     * @see UHCPlayer#getUHCClass()
     * @return Player's class, or null if player is not playing or has no selected class
     */
    public static UHCClass getInGameClass(Player player) {
        UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
        if(uhcPlayer == null) return null;
        return uhcPlayer.getUHCClass();
    }

    /**
     * Gets the player class that he has chosen in lobby
     * @param player The player
     * @return The player class, or null if he hasn't chosen a class
     */
    @Nullable
    public static UHCClass getClassInLobby(Player player) {
        return lobbyPlayerClasses.get(player.getName());
    }

    /**
     * Selects the class while the player is in lobby
     */
    public static void selectClassInLobby(Player player, UHCClass uhcClass) {
        if(!Lobby.isInLobby(player)) return;
        lobbyPlayerClasses.put(player.getName(), uhcClass);
        player.sendMessage(
                ChatColor.GRAY + "" + ChatColor.BOLD + "> " +
                ChatColor.RESET + ChatColor.GREEN + "Установлен класс: " +
                uhcClass.getName());
        PlayerOptionHolder.setSelectedClass(player, uhcClass);
        UHC.updateLobbyScoreboard(player);
    }

    /**
     * Removes the selected class from the player while he is in lobby
     */
    public static void removeClassInLobby(Player player) {
        if(!Lobby.isInLobby(player)) return;
        lobbyPlayerClasses.remove(player.getName());
        player.sendMessage(
                ChatColor.GRAY + "" + ChatColor.BOLD + "> " +
                ChatColor.RESET + ChatColor.DARK_GREEN + "Класс сброшен");
        PlayerOptionHolder.setSelectedClass(player, null);
        UHC.updateLobbyScoreboard(player);
    }

    public static void openClassInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, INV_NAME);
        inventory.addItem(
                classes.stream().map(uhcClass -> uhcClass.makeItemToShow(player)).toArray(ItemStack[]::new)
        );
        inventory.setItem(8,
                ItemUtils.builder(Material.BARRIER).withName(ChatColor.RED + "Сбросить класс").build());
        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void setClassOnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UHCClass uhcClass = PlayerOptionHolder.getSelectedClass(player);
        if(uhcClass != null) {
            lobbyPlayerClasses.put(player.getName(), uhcClass);
            UHC.updateLobbyScoreboard(player);
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals(INV_NAME)) {
            Player player = (Player) e.getWhoClicked();
            if(!Lobby.isInLobby(player)) return;
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if(item == null) return;
            if(item.getType() == Material.BARRIER) {
                removeClassInLobby(player);
                e.getWhoClicked().closeInventory();
                return;
            }
            UHCClass uhcClass = null;
            for(UHCClass currentClass : classes) {
                if(currentClass.getItemToShow() == item.getType()) {
                    uhcClass = currentClass;
                    break;
                }
            }
            if(uhcClass != null) {
                e.getWhoClicked().closeInventory();
                selectClassInLobby(player, uhcClass);
            }
        }
    }

}

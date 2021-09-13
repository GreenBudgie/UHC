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

    private static final String INVENTORY_CLASS_SELECT_NAME = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Классы";
    private static final String INVENTORY_CLASS_INFO_NAME = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "О классе: ";
    public static final List<UHCClass> classes = new ArrayList<>();
    public static final Map<String, UHCClass> lobbyPlayerClasses = new HashMap<>();

    public static final ClassDemon DEMON = new ClassDemon();
    public static final ClassNecromancer NECROMANCER = new ClassNecromancer();
    public static final ClassMiner MINER = new ClassMiner();
    public static final ClassBerserk BERSERK = new ClassBerserk();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ClassManager(), UHCPlugin.instance);
        for(Player player : Lobby.getPlayersInLobbyAndArenas()) {
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
        if(!Lobby.isInLobbyOrWatchingArena(player)) return;
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
        if(!Lobby.isInLobbyOrWatchingArena(player)) return;
        lobbyPlayerClasses.remove(player.getName());
        player.sendMessage(
                ChatColor.GRAY + "" + ChatColor.BOLD + "> " +
                ChatColor.RESET + ChatColor.DARK_GREEN + "Класс сброшен");
        PlayerOptionHolder.setSelectedClass(player, null);
        UHC.updateLobbyScoreboard(player);
    }

    public static void openClassSelectInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, INVENTORY_CLASS_SELECT_NAME);
        inventory.addItem(
                classes.stream().map(uhcClass -> uhcClass.makeItemToShow(player)).toArray(ItemStack[]::new)
        );
        inventory.setItem(8,
                ItemUtils.builder(Material.BARRIER).withName(ChatColor.RED + "Сбросить класс").build());
        player.openInventory(inventory);
    }

    public static void openClassInfoInventory(Player player, UHCClass uhcClass) {
        int invSize = 5 * 9;
        Inventory inventory = Bukkit.createInventory(player, invSize, INVENTORY_CLASS_INFO_NAME + uhcClass.getName());

        inventory.setItem(0, ItemUtils.builder(Material.LIME_DYE).withName(ChatColor.GREEN + "" + ChatColor.BOLD + "Преимущества").build());
        for(int i = 0; i < uhcClass.getAdvantageItems().length; i++) {
            inventory.setItem(i + 1, uhcClass.getAdvantageItems()[i]);
        }

        inventory.setItem(9, ItemUtils.builder(Material.RED_DYE).withName(ChatColor.RED + "" + ChatColor.BOLD + "Недостатки").build());
        for(int i = 0; i < uhcClass.getDisadvantageItems().length; i++) {
            inventory.setItem(i + 10, uhcClass.getDisadvantageItems()[i]);
        }

        ItemStack[] startItems = uhcClass.getStartItems();
        int startItemCount = startItems.length;
        if(startItemCount > 0) {
            inventory.setItem(18,
                    ItemUtils.builder(Material.CYAN_DYE).
                            withName(ChatColor.AQUA + "" + ChatColor.BOLD + (startItemCount == 1 ? "Предмет" : "Предметы")).
                            withLore(ChatColor.DARK_AQUA + (startItemCount == 1 ? "Будет выдан на старте игры" : "Будут выданы на старте игры")).
                            build());
            for(int i = 0; i < startItemCount; i++) {
                inventory.setItem(i + 19, startItems[i]);
            }
        }

        ItemStack blackPanel = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
        for(int slot = 27; slot < 36; slot++) {
            inventory.setItem(slot, blackPanel);
        }

        inventory.setItem(36, ItemUtils.builder(Material.BARRIER).withName(ChatColor.RED + "К выбору классов").build());
        inventory.setItem(40,
                ItemUtils.builder(Material.NAME_TAG).
                        withName(" ").
                        withSplittedLore(ChatColor.GRAY + "Напиши " + ChatColor.WHITE + "/class" + ChatColor.GRAY + " во время игры, чтобы открыть это меню").
                        build());
        inventory.setItem(44, ItemUtils.builder(Material.GREEN_DYE).
                withName(ChatColor.GREEN + "Установить класс").
                withValue("class", uhcClass.getConfigName()).
                build());

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
    public void invClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getView().getTitle().equals(INVENTORY_CLASS_SELECT_NAME)) {
            if(!Lobby.isInLobbyOrWatchingArena(player)) return;
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if(item == null) return;
            if(item.getType() == Material.BARRIER) {
                removeClassInLobby(player);
                event.getWhoClicked().closeInventory();
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
                if(event.isRightClick()) {
                    event.getWhoClicked().closeInventory();
                    selectClassInLobby(player, uhcClass);
                } else {
                    openClassInfoInventory(player, uhcClass);
                }
            }
        }
        if(event.getView().getTitle().startsWith(INVENTORY_CLASS_INFO_NAME)) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if(item == null) return;
            if(item.getType() == Material.BARRIER) {
                openClassSelectInventory(player);
                return;
            }
            if(item.getType() == Material.GREEN_DYE) {
                String rawClass = ItemUtils.getCustomValue(item, "class");
                if(rawClass != null) {
                    UHCClass currentClass = getClassByConfigName(rawClass);
                    if(currentClass != null) {
                        event.getWhoClicked().closeInventory();
                        selectClassInLobby(player, currentClass);
                    }
                }
            }
        }
    }

}

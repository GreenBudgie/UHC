package ru.classes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
    public static final Map<String, UHCClass> playerClasses = new HashMap<>();

    public static final ClassArcher ARCHER = new ClassArcher();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ClassManager(), UHCPlugin.instance);
    }

    /**
     * Gets the player class
     * @param player The player
     * @return The player class, or null if he haven't chosen a class
     */
    @Nullable
    public static UHCClass getClass(Player player) {
        return playerClasses.get(player.getName());
    }

    public static void setClass(Player player, UHCClass uhcClass) {
        playerClasses.put(player.getName(), uhcClass);
        player.sendMessage(
                ChatColor.GRAY + "" + ChatColor.BOLD + "> " +
                ChatColor.RESET + ChatColor.GREEN + "Установлен класс: " +
                uhcClass.getName());
    }

    public static void removeClass(Player player) {
        playerClasses.remove(player.getName());
        player.sendMessage(
                ChatColor.GRAY + "" + ChatColor.BOLD + "> " +
                ChatColor.RESET + ChatColor.DARK_GREEN + "Класс сброшен");
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

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equals(INV_NAME)) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();
            if(item == null) return;
            if(item.getType() == Material.BARRIER) {
                removeClass((Player) e.getWhoClicked());
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
                setClass((Player) e.getWhoClicked(), uhcClass);
            }
        }
    }

}

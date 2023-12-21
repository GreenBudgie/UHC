package ru.greenbudgie.tutorial;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.item.ItemUtils;

import java.util.List;

import static org.bukkit.ChatColor.*;
import static ru.greenbudgie.tutorial.TutorialMessages.*;

public class TutorialInventory implements Listener {

    private static final String INVENTORY_HEADER = YELLOW + "" + BOLD + "Об игре";

    private static final List<ItemStack> tutorialItems = List.of(
            getItem(Material.GOLDEN_APPLE, "Общая информация", GENERAL_INFO),
            getItem(Material.DIAMOND_SWORD, "Дезматч", DEATHMATCH_INFO),
            getItem(Material.PHANTOM_MEMBRANE, "Дропы", DROP_INFO),
            getItem(Material.SHULKER_SHELL, "Мутаторы", MUTATOR_INFO),
            getItem(Material.BLACK_DYE, "Артефакты", ARTIFACT_INFO),
            getItem(Material.REDSTONE, "Запросы", REQUEST_INFO),
            getItem(Material.OBSIDIAN, "Ад", NETHER_INFO)
    );

    public static void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 9, INVENTORY_HEADER);
        inventory.addItem(tutorialItems.toArray(new ItemStack[0]));
        player.openInventory(inventory);
    }

    private static ItemStack getItem(Material type, String name, String info) {
        return ItemUtils.builder(type)
                .withName(YELLOW + "" + BOLD + name)
                .withSplittedLore(GRAY + info, 40)
                .build();
    }

    @EventHandler
    public void noClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(INVENTORY_HEADER)) {
            event.setCancelled(true);
        }
    }

}

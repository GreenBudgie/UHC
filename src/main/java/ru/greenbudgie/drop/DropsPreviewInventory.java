package ru.greenbudgie.drop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.InventoryHelper;

import static org.bukkit.ChatColor.*;

public class DropsPreviewInventory implements Listener {

    private static final String INVENTORY_HEADER = AQUA + "" + BOLD + "Дропы";

    public static void openDropsPreviewInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(
                player,
                InventoryHelper.getInventorySizeFittingItemAmount(Drops.getWeightedDropsList().getElements().size()),
                INVENTORY_HEADER
        );
        inventory.addItem(Drops.getWeightedDropsList().getPreviewItems().toArray(new ItemStack[0]));
        player.openInventory(inventory);
    }

    @EventHandler
    public void noClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(INVENTORY_HEADER)) {
            event.setCancelled(true);
        }
    }

}

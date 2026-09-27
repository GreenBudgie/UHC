package ru.greenbudgie.drop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ru.greenbudgie.util.item.ItemUtils;

import static org.bukkit.ChatColor.*;

public class DropsPreviewInventory implements Listener {

    private static final String INVENTORY_HEADER = AQUA + "" + BOLD + "Дропы: ";

    public static void openDropsPreviewInventory(Player player, Drop drop) {
        Inventory inventory = Bukkit.createInventory(
                player,
                54,
                INVENTORY_HEADER + drop.getName()
        );

        var blackGlass = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
        for (int slot = 0; slot < 54; slot++) {
            if (slot < 9 ||
                    slot % 9 == 0 ||
                    slot % 9 == 8 ||
                    (slot >= 36 && slot < 45)
            ) {
                inventory.setItem(slot, blackGlass);
            }
        }

        var drops = Drops.DROPS;
        var startSlot = 49 - drops.size() / 2;
        for (int i = 0; i < drops.size(); i++) {
            var currentDrop = drops.get(i);
            var representingItem = ItemUtils.builder(currentDrop.getRepresentingItem())
                    .withName(currentDrop.getName())
                    .withGlow(currentDrop == drop)
                    .build();

            inventory.setItem(startSlot + i, representingItem);
        }

        var currentItemIndex = 0;
        var previewItems = drop.getWeightedItemList().getPreviewItems();
        for (int slot = 9; slot < 36; slot++) {
            if (previewItems.size() <= currentItemIndex) {
                break;
            }

            if (slot % 9 == 0 || slot % 9 == 8) {
                continue;
            }

            inventory.setItem(slot, previewItems.get(currentItemIndex));
            currentItemIndex++;
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(INVENTORY_HEADER)) {
            return;
        }

        event.setCancelled(true);

        if (event.getRawSlot() < 45) {
            return;
        }

        if (!event.isLeftClick()) {
            return;
        }

        var clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        for (var drop : Drops.DROPS) {
            if (drop.getRepresentingItem() == clickedItem.getType()) {
                openDropsPreviewInventory((Player)event.getWhoClicked(), drop);
                return;
            }
        }
    }

}

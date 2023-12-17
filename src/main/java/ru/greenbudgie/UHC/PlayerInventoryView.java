package ru.greenbudgie.UHC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ru.greenbudgie.util.item.ItemUtils;

import static org.bukkit.ChatColor.*;

public class PlayerInventoryView implements Listener {

    private static final String INVENTORY_VIEW_PREFIX = DARK_GRAY + "Инвентарь";

    public static void viewInventory(Player observer, Player target) {
        final int size = 9 * 6;
        PlayerInventory targetInventory = target.getInventory();
        Inventory currentInventory = Bukkit.createInventory(
                observer,
                size,
                INVENTORY_VIEW_PREFIX + DARK_AQUA + BOLD + " " + target.getName()
        );
        for(int i = 0; i < targetInventory.getStorageContents().length; i++) {
            ItemStack item = targetInventory.getStorageContents()[i];
            currentInventory.setItem(i, item);
        }
        ItemStack blackPanel = ItemUtils.builder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
        for(int slot = size - 18; slot < size - 9; slot++) {
            currentInventory.setItem(slot, blackPanel);
        }
        for(int slot = size - 9 + 4; slot < size - 1; slot++) {
            currentInventory.setItem(slot, blackPanel);
        }
        currentInventory.setItem(size - 9, targetInventory.getHelmet());
        currentInventory.setItem(size - 9 + 1, targetInventory.getChestplate());
        currentInventory.setItem(size - 9 + 2, targetInventory.getLeggings());
        currentInventory.setItem(size - 9 + 3, targetInventory.getBoots());
        currentInventory.setItem(size - 1, targetInventory.getItemInOffHand());
        observer.openInventory(currentInventory);
    }

    @EventHandler
    public void noInteractWithPlayerInventoryView(InventoryClickEvent event) {
        if(event.getView().getTitle().startsWith(INVENTORY_VIEW_PREFIX)) {
            event.setCancelled(true);
        }
    }

}

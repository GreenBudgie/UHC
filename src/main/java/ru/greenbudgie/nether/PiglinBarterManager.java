package ru.greenbudgie.nether;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.ItemUtils;
import ru.greenbudgie.util.WeightedItem;
import ru.greenbudgie.util.WeightedItemList;

import java.util.List;

import static org.bukkit.ChatColor.*;

public class PiglinBarterManager implements Listener {

    private static final String INVENTORY_HEADER = GOLD + "" + BOLD + "Торговля с Пиглинами";

    private static final ItemStack fireResistancePotion = ItemUtils.potionBuilder()
            .withType(PotionType.LONG_FIRE_RESISTANCE)
            .build();

    private static final ItemStack waterBottle = ItemUtils.potionBuilder()
            .withType(PotionType.WATER)
            .build();

    public static final WeightedItemList barters = new WeightedItemList(
            new WeightedItem(waterBottle, 1, 1, 4),
            new WeightedItem(Material.SPECTRAL_ARROW, 6, 12, 4),
            new WeightedItem(Material.BOOK, 1, 3, 4),
            new WeightedItem(Material.STRING, 3, 6, 4),
            new WeightedItem(Material.IRON_INGOT, 1, 4, 4),
            new WeightedItem(Material.GUNPOWDER, 3, 5, 3),
            new WeightedItem(Material.NETHER_WART, 1, 2, 3),
            new WeightedItem(Material.GOLDEN_CARROT, 4, 6, 3),
            new WeightedItem(Material.OBSIDIAN, 10, 10, 3),
            new WeightedItem(Material.LAVA_BUCKET, 1, 1, 2),
            new WeightedItem(Material.REDSTONE, 6, 12, 2),
            new WeightedItem(Material.LAPIS_LAZULI, 4, 8, 2),
            new WeightedItem(fireResistancePotion, 1, 1, 1),
            new WeightedItem(Material.DIAMOND, 1, 2, 1),
            new WeightedItem(Material.APPLE, 1, 1, 1)
    );

    public static void openBartersPreviewInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(
                player,
                InventoryHelper.getInventorySizeFittingItemAmount(barters.getItems().length),
                INVENTORY_HEADER
        );
        inventory.addItem(barters.getPreviewItems().toArray(new ItemStack[0]));
        player.openInventory(inventory);
    }

    @EventHandler
    public void customPiglinBarter(PiglinBarterEvent event) {
        if (!UHC.playing) {
            return;
        }
        List<ItemStack> outcome = event.getOutcome();
        outcome.clear();
        outcome.add(barters.getRandomItemWeighted().getItem());
    }

    @EventHandler
    public void noClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(INVENTORY_HEADER)) {
            event.setCancelled(true);
        }
    }

}

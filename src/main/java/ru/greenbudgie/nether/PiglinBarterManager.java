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
import ru.greenbudgie.util.item.ItemUtils;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import java.util.List;

import static org.bukkit.ChatColor.*;

public class PiglinBarterManager implements Listener {

    private static final String INVENTORY_HEADER = GOLD + "" + BOLD + "Торговля с Пиглинами";

    private static final ItemStack fireResistancePotion = ItemUtils.potionBuilder()
            .withType(PotionType.LONG_FIRE_RESISTANCE)
            .build();

    public static final WeightedItemList barters = new WeightedItemList(
            WeightedItem.builder(Material.SPECTRAL_ARROW).amount(6, 12).weight(4).build(),
            WeightedItem.builder(Material.BOOK).amount(1, 3).weight(4).build(),
            WeightedItem.builder(Material.STRING).amount(3, 6).weight(4).build(),
            WeightedItem.builder(Material.IRON_INGOT).amount(1, 4).weight(3).build(),
            WeightedItem.builder(Material.GUNPOWDER).amount(3, 5).weight(3).build(),
            WeightedItem.builder(Material.NETHER_WART).amount(1, 2).weight(3).build(),
            WeightedItem.builder(Material.GOLDEN_CARROT).amount(3, 6).weight(3).build(),
            WeightedItem.builder(Material.OBSIDIAN).amount(10).weight(3).build(),
            WeightedItem.builder(Material.LAVA_BUCKET).weight(2).build(),
            WeightedItem.builder(Material.REDSTONE).amount(6, 12).weight(2).build(),
            WeightedItem.builder(Material.LAPIS_LAZULI).amount(4, 8).weight(2).build(),
            WeightedItem.builder(fireResistancePotion).weight(1).build(),
            WeightedItem.builder(Material.DIAMOND).amount(1, 2).weight(1).build(),
            WeightedItem.builder(Material.APPLE).weight(1).build()
    );

    public static void openBartersPreviewInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(
                player,
                InventoryHelper.getInventorySizeFittingItemAmount(barters.getElements().size()),
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
        outcome.add(barters.getRandomElementWeighted().getItem().clone());
    }

    @EventHandler
    public void noClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(INVENTORY_HEADER)) {
            event.setCancelled(true);
        }
    }

}

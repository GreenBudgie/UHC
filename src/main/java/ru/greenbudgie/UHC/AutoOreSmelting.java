package ru.greenbudgie.UHC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.MathUtils;

import java.util.*;

public class AutoOreSmelting implements Listener {

    private static final Set<Material> blocksToSmelt = Set.of(
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.ANCIENT_DEBRIS
    );

    private static final Map<Material, ExperienceAmount> experienceToDrop = Map.of(
            Material.IRON_ORE, new ExperienceAmount(1, 3),
            Material.DEEPSLATE_IRON_ORE, new ExperienceAmount(1, 3),
            Material.GOLD_ORE, new ExperienceAmount(2, 5),
            Material.DEEPSLATE_GOLD_ORE, new ExperienceAmount(2, 5),
            Material.COPPER_ORE, new ExperienceAmount(0, 2),
            Material.DEEPSLATE_COPPER_ORE, new ExperienceAmount(0, 2),
            Material.ANCIENT_DEBRIS, new ExperienceAmount(4, 10)
    );

    private static final Map<Material, Material> smeltingResults = Map.of(
            Material.RAW_IRON, Material.IRON_INGOT,
            Material.RAW_GOLD, Material.GOLD_INGOT,
            Material.RAW_COPPER, Material.COPPER_INGOT,
            Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP
    );

    @EventHandler(priority = EventPriority.HIGH)
    public void autoSmeltOre(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (!PlayerManager.isPlaying(player)) {
            return;
        }
        ItemStack usedItem = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        if (!blocksToSmelt.contains(block.getType())) {
            return;
        }
        Collection<ItemStack> drops = block.getDrops(usedItem, player);
        List<ItemStack> smeltedDrops = new ArrayList<>();
        for(ItemStack initialDrop : drops) {
            Material smeltingResult = smeltingResults.get(initialDrop.getType());
            if (smeltingResult == null) {
                continue;
            }
            ItemStack smeltedDrop = new ItemStack(smeltingResult, initialDrop.getAmount());
            smeltedDrops.add(smeltedDrop);
        }
        if (smeltedDrops.isEmpty()) {
            return;
        }
        ExperienceAmount experienceAmount = experienceToDrop.get(block.getType());
        if (experienceAmount == null) {
            UHCPlugin.error("No experience amount is configured for the specified smeltable block, 0 will be used instead");
            experienceAmount = new ExperienceAmount(0, 0);
        }
        event.setDropItems(false);
        event.setExpToDrop(experienceAmount.randomExperience());

        Location blockLocation = block.getLocation();
        for (ItemStack smeltedDrop : smeltedDrops) {
            block.getWorld().dropItemNaturally(blockLocation, smeltedDrop);
        }
    }

    private record ExperienceAmount(int min, int max) {

        public int randomExperience() {
            return MathUtils.randomRange(min, max);
        }

    }

}

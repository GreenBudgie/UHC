package ru.greenbudgie.drop;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.item.ItemUtils;
import ru.greenbudgie.util.weighted.WeightedEnchantedItem;
import ru.greenbudgie.util.weighted.WeightedEnchantment;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import static org.bukkit.ChatColor.*;

public class NetherDrop extends ChestBasedDrop {

    private static final ItemStack regenerationPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Hyper Potion of Regeneration")
            .withColor(Color.fromRGB(255, 182, 243))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.REGENERATION).seconds(30).amplifier(1).build()
            ).build();

    private static final WeightedItemList weightedDrops = new WeightedItemList(
            WeightedItem.builder(regenerationPotion).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_BOOTS)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(2, 3).build()
                    ).weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.FEATHER_FALLING).level(3, 4).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_LEGGINGS)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(2, 3).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.FIRE_PROTECTION).level(2, 3).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_CHESTPLATE)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(2, 3).build()
                    ).weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.BLAST_PROTECTION).level(2, 3).build(),
                            WeightedEnchantment.builder(Enchantment.THORNS).level(2).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_HELMET)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(2, 3).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.PROJECTILE_PROTECTION).level(2, 3).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.BOW).alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.POWER).level(2, 3).build(),
                    WeightedEnchantment.builder(Enchantment.INFINITY).build(),
                    WeightedEnchantment.builder(Enchantment.FLAME).build()
            ).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_SWORD).alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.SHARPNESS).level(3, 5).build(),
                    WeightedEnchantment.builder(Enchantment.FIRE_ASPECT).level(1).build()
            ).build()
    );

    private static final WeightedItemList FILLERS = new WeightedItemList(
            WeightedItem.builder(Material.NETHER_WART).amount(3, 6).weight(3).build(),
            WeightedItem.builder(Material.PORKCHOP).amount(4, 6).weight(3).build(),
            WeightedItem.builder(Material.GOLDEN_CARROT).amount(4, 6).weight(3).build(),
            WeightedItem.builder(Material.FERMENTED_SPIDER_EYE).amount(1, 2).weight(3).build(),
            WeightedItem.builder(Material.BLAZE_POWDER).weight(3).build(),
            WeightedItem.builder(Material.GOLD_INGOT).amount(5, 10).weight(3).build(),
            WeightedItem.builder(Material.MAGMA_CREAM).weight(3).build(),
            WeightedItem.builder(Material.GUNPOWDER).amount(1, 3).weight(3).build(),
            WeightedItem.builder(Material.ANCIENT_DEBRIS).amount(1, 2).weight(1).build()
    );

    @Override
    public String getName() {
        return DARK_RED + "" + BOLD + "Незердроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 7 * 60;
    }

    @Override
    public int getFirstDropDelay() {
        return 3 * 60 + 40;
    }

    @Override
    protected Material getCasing() {
        return Material.TINTED_GLASS;
    }

    @Override
    public WeightedItemList getFillers() {
        return FILLERS;
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getGameMapNether().getWorldBorder().getSize()) / 4 - 10;
        Location center = WorldManager.getGameMapNether().getWorldBorder().getCenter();
        int x = MathUtils.randomRange(
                center.getBlockX() - size,
                center.getBlockX() + size);
        int z = MathUtils.randomRange(
                center.getBlockZ() - size,
                center.getBlockZ() + size);
        int y = MathUtils.randomRange(34, 120);
        return new Location(WorldManager.getGameMapNether(), x, y, z);
    }

    @Override
    protected int getMinFillers() {
        return 3;
    }

    @Override
    protected int getMaxFillers() {
        return 6;
    }

    @Override
    public World.Environment getSpawnEnvironment() {
        return World.Environment.NETHER;
    }

    @Override
    public ChatColor getMarkerColor() {
        return DARK_RED;
    }

    @Override
    public WeightedItemList getWeightedItemList() {
        return weightedDrops;
    }

    @Override
    public Material getRepresentingItem() {
        return Material.NETHERRACK;
    }

}

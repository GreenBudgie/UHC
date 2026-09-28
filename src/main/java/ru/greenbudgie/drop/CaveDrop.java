package ru.greenbudgie.drop;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
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

public class CaveDrop extends ChestBasedDrop {

    private static final ItemStack minerPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Instamine")
            .withColor(Color.ORANGE)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.HASTE).minutes(8).amplifier(9).build()
            ).build();
    private static final ItemStack defencePotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Defence")
            .withColor(Color.MAROON)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.SLOWNESS).minutes(1).amplifier(5).build(),
                    new PotionEffectBuilder(PotionEffectType.RESISTANCE).minutes(1).amplifier(3).build()
            ).build();

    private static final WeightedItemList weightedDrops = new WeightedItemList(
            WeightedItem.builder(minerPotion).build(),
            WeightedItem.builder(defencePotion).build(),

            WeightedEnchantedItem.item(Material.NETHERITE_PICKAXE)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.EFFICIENCY).level(5).build(),
                            WeightedEnchantment.builder(Enchantment.FORTUNE).level(4).build()
                    ).build(),

            WeightedItem.builder(Material.DIAMOND).amount(12, 20).build(),

            WeightedEnchantedItem.item(Material.CROSSBOW)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.QUICK_CHARGE).level(3).build(),
                            WeightedEnchantment.builder(Enchantment.PIERCING).build()
                    ).build(),

            WeightedEnchantedItem.item(Material.TRIDENT)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.LOYALTY).level(3).build(),
                            WeightedEnchantment.builder(Enchantment.UNBREAKING).level(3).build()
                    ).build(),

            WeightedEnchantedItem.item(Material.MACE)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.BREACH).level(4).build()
                    ).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_SPEAR)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.LUNGE).level(3).build(),
                            WeightedEnchantment.builder(Enchantment.UNBREAKING).level(3).build()
                    ).weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.SHARPNESS).level(1, 2).build()
                    ).number(0, 1).build()
    );


    private static final WeightedItemList FILLERS = new WeightedItemList(
            WeightedItem.builder(Material.BREAD).amount(5, 8).weight(3).build(),
            WeightedItem.builder(Material.LAPIS_LAZULI).amount(5, 10).weight(3).build(),
            WeightedItem.builder(Material.REDSTONE).amount(10, 15).weight(3).build(),
            WeightedItem.builder(Material.IRON_INGOT).amount(4, 8).weight(3).build(),
            WeightedItem.builder(Material.GOLD_INGOT).amount(2, 5).weight(3).build(),
            WeightedItem.builder(Material.APPLE).amount(1, 2).weight(2).build(),
            WeightedItem.builder(Material.ARROW).amount(5, 15).weight(2).build(),
            WeightedItem.builder(Material.OBSIDIAN).amount(4, 5).weight(2).build(),
            WeightedItem.builder(Material.DIAMOND).amount(1, 2).weight(1).build()
    );

    @Override
    public String getName() {
        return DARK_GREEN + "" + BOLD + "Кейвдроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 6 * 60;
    }

    @Override
    public int getFirstDropDelay() {
        return 2 * 60 + 20;
    }

    @Override
    protected Material getCasing() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public WeightedItemList getFillers() {
        return FILLERS;
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getActualMapSize()) / 2 - 10;
        int x = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockX() - size,
                WorldManager.spawnLocation.getBlockX() + size);
        int z = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockZ() - size,
                WorldManager.spawnLocation.getBlockZ() + size);
        int minHeight = WorldManager.getGameMap().getMinHeight() + 8;
        int y = MathUtils.randomRange(minHeight, 0);
        return new Location(WorldManager.getGameMap(), x, y, z);
    }

    @Override
    protected int getMinFillers() {
        return 3;
    }

    @Override
    protected int getMaxFillers() {
        return 5;
    }

    @Override
    public ChatColor getMarkerColor() {
        return DARK_GREEN;
    }

    @Override
    public WeightedItemList getWeightedItemList() {
        return weightedDrops;
    }

    @Override
    public Material getRepresentingItem() {
        return Material.DEEPSLATE;
    }

}

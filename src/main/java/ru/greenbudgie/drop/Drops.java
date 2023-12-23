package ru.greenbudgie.drop;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.Messages;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.item.Enchant;
import ru.greenbudgie.util.item.ItemUtils;
import ru.greenbudgie.util.weighted.WeightedEnchantedItem;
import ru.greenbudgie.util.weighted.WeightedEnchantment;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class Drops {

    public static final List<Drop> DROPS = new ArrayList<>();

    public static final AirDrop AIRDROP = new AirDrop();
    public static final CaveDrop CAVEDROP = new CaveDrop();
    public static final NetherDrop NETHERDROP = new NetherDrop();

    private static final ItemStack healingPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Life Essence")
            .withColor(Color.fromRGB(255, 182, 243))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.HEAL).amplifier(1).build(),
                    new PotionEffectBuilder(PotionEffectType.ABSORPTION).minutes(2).noParticles().build()
            ).build();
    private static final ItemStack toxicPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Bottle of Toxic Sludge")
            .splash()
            .withColor(Color.GREEN)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.POISON).seconds(6).amplifier(1).build(),
                    new PotionEffectBuilder(PotionEffectType.SLOW).amplifier(1).minutes(1).build()
            ).build();
    private static final ItemStack weaknessPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Weakening")
            .splash()
            .withColor(Color.GRAY)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.BLINDNESS).seconds(20).build(),
                    new PotionEffectBuilder(PotionEffectType.WEAKNESS).minutes(1).build()
            ).build();
    private static final ItemStack minerPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Miner Dream")
            .withColor(Color.WHITE)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.FAST_DIGGING).minutes(10).amplifier(2).build(),
                    new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).minutes(30).build()
            ).build();
    private static final ItemStack explorerPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Exploration")
            .withColor(Color.LIME)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.SPEED).minutes(15).amplifier(1).build(),
                    new PotionEffectBuilder(PotionEffectType.DOLPHINS_GRACE).minutes(15).build(),
                    new PotionEffectBuilder(PotionEffectType.CONDUIT_POWER).minutes(15).build(),
                    new PotionEffectBuilder(PotionEffectType.NIGHT_VISION).minutes(15).build()
            ).build();
    private static final ItemStack strengthPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Dominance")
            .withColor(Color.fromRGB(100, 0, 0))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.INCREASE_DAMAGE).minutes(1).build(),
                    new PotionEffectBuilder(PotionEffectType.DAMAGE_RESISTANCE).seconds(30).build()
            ).build();
    private static final ItemStack damagePotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Death")
            .splash()
            .withColor(Color.BLACK)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.HARM).build(),
                    new PotionEffectBuilder(PotionEffectType.WITHER).seconds(13).build()
            ).build();
    private static final ItemStack disrespectPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Enemy Disrespect")
            .splash()
            .withColor(Color.fromRGB(0, 0, 170))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.LEVITATION).seconds(6).amplifier(2).build(),
                    new PotionEffectBuilder(PotionEffectType.DARKNESS).seconds(30).build(),
                    new PotionEffectBuilder(PotionEffectType.HUNGER).minutes(2).amplifier(4).build()
            ).build();
    
    private static final WeightedItemList weightedDrops = new WeightedItemList(
            WeightedItem.builder(healingPotion).weight(2).build(),
            WeightedItem.builder(minerPotion).weight(2).build(),
            WeightedItem.builder(strengthPotion).weight(2).build(),
            WeightedItem.builder(explorerPotion).weight(2).build(),
            WeightedItem.builder(toxicPotion).weight(2).build(),
            WeightedItem.builder(weaknessPotion).weight(2).build(),
            WeightedItem.builder(damagePotion).weight(2).build(),
            WeightedItem.builder(disrespectPotion).weight(2).build(),
            WeightedItem.builder(Material.GOLDEN_APPLE).amount(2).weight(2).build(),
            WeightedItem.builder(Material.DIAMOND).amount(8, 14).weight(2).build(),
            WeightedItem.builder(Material.GOLD_INGOT).amount(20, 30).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(
                    new Enchant(Enchantment.THORNS, 3),
                    new Enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            ).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.LOOT_BONUS_BLOCKS).level(2, 3).build()
            ).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.LOOT_BONUS_MOBS).level(3).build()
            ).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(new Enchant(Enchantment.FIRE_ASPECT, 2)).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_BOOTS).weightedEnchantments(
                    WeightedEnchantment.builder(Enchantment.PROTECTION_FALL).level(2, 4).build(),
                    WeightedEnchantment.builder(Enchantment.DEPTH_STRIDER).level(2, 3).build(),
                    WeightedEnchantment.builder(Enchantment.PROTECTION_ENVIRONMENTAL).level(1).build()
            ).number(2).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_LEGGINGS).weightedEnchantments(
                    WeightedEnchantment.builder(Enchantment.PROTECTION_FIRE).level(2, 4).build(),
                    WeightedEnchantment.builder(Enchantment.PROTECTION_EXPLOSIONS).level(2, 4).build(),
                    WeightedEnchantment.builder(Enchantment.PROTECTION_ENVIRONMENTAL).level(1).build(),
                    WeightedEnchantment.builder(Enchantment.SWIFT_SNEAK).level(3).build()
            ).number(2).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_CHESTPLATE).alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.PROTECTION_ENVIRONMENTAL).level(1, 2).build(),
                    WeightedEnchantment.builder(Enchantment.THORNS).level(1).build()
            ).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_HELMET)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.OXYGEN).level(2, 3).build(),
                            WeightedEnchantment.builder(Enchantment.WATER_WORKER).level(1).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.PROTECTION_PROJECTILE).level(2, 3).build(),
                            WeightedEnchantment.builder(Enchantment.PROTECTION_ENVIRONMENTAL).level(1).build()
                    ).weight(2).build(),
            WeightedEnchantedItem.item(Material.BOW)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.ARROW_DAMAGE).level(2, 4).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.ARROW_KNOCKBACK).level(1, 2).weight(2).build(),
                            WeightedEnchantment.builder(Enchantment.ARROW_FIRE).build()
                    ).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_SWORD)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.DAMAGE_ALL).level(1, 2).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.KNOCKBACK).weight(2).build(),
                            WeightedEnchantment.builder(Enchantment.FIRE_ASPECT).build()
                    ).weight(2).build(),
            WeightedEnchantedItem.item(Material.DIAMOND_AXE)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.DIG_SPEED).level(2, 3).build(),
                            WeightedEnchantment.builder(Enchantment.KNOCKBACK).level(2).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.DAMAGE_ALL).build()
                    ).number(0, 1).weight(2).build(),
            WeightedEnchantedItem.item(Material.CROSSBOW)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.QUICK_CHARGE).level(2, 3).build(),
                            WeightedEnchantment.builder(Enchantment.PIERCING).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.MULTISHOT).build()
                    ).number(0, 1).weight(2).build(),
            WeightedItem.builder(Material.ENCHANTING_TABLE).weight(2).build(),
            WeightedItem.builder(Material.REDSTONE_BLOCK).amount(10, 14).weight(2).build(),
            WeightedItem.builder(Material.LAPIS_BLOCK).amount(4, 6).weight(2).build(),
            WeightedItem.builder(Material.SPECTRAL_ARROW).amount(32, 48).weight(2).build(),
            WeightedItem.builder(CustomItems.darkArtifact.getItemStack()).amount(20, 30).weight(2).build(),

            WeightedEnchantedItem.item(Material.TRIDENT)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.LOYALTY).level(1, 2).build()
                    ).weight(1).build(),

            WeightedItem.builder(Material.NETHERITE_HELMET).weight(1).build(),
            WeightedItem.builder(Material.NETHERITE_CHESTPLATE).weight(1).build(),
            WeightedItem.builder(Material.NETHERITE_LEGGINGS).weight(1).build(),
            WeightedItem.builder(Material.NETHERITE_BOOTS).weight(1).build(),
            WeightedItem.builder(Material.NETHERITE_SWORD).weight(1).build(),
            WeightedItem.builder(Material.NETHERITE_AXE).weight(1).build(),
            WeightedItem.builder(
                    ItemUtils.builder(Material.NETHERITE_INGOT)
                            .withSplittedLore(Messages.NETHERITE_TRIM_IS_NOT_REQUIRED)
                            .build()
            ).weight(1).build()
    );

    public static void update() {
        DROPS.forEach(Drop::update);
    }

    public static void firstSetup() {
        for(Drop drop : DROPS) {
            drop.setup();
            drop.timer -= MathUtils.randomRange(0, 30);
        }
    }

    public static void updateScoreboard(Scoreboard scoreboard) {
        for (Drop drop : DROPS) {
            drop.updateMarkerTeams(scoreboard);
        }
    }

    public static WeightedItemList getWeightedDropsList() {
        return weightedDrops;
    }

}

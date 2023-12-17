package ru.greenbudgie.drop;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.InventoryHelper;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.Messages;
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

    private static final ItemStack healingPotion = InventoryHelper.generatePotion(
            WHITE + "Potion of Life",
            Color.FUCHSIA,
            new PotionEffect(PotionEffectType.HEAL, 1, 1)
    );
    private static final ItemStack toxicPotion = InventoryHelper.generatePotion(
            WHITE + "Toxic Vial",
            Color.GREEN,
            true,
            false,
            new PotionEffect(PotionEffectType.POISON, 200, 1)
    );
    private static final ItemStack blindnessPotion = InventoryHelper.generatePotion(
            WHITE + "Sightbreaker Potion",
            Color.GRAY,
            true,
            false,
            new PotionEffect(PotionEffectType.BLINDNESS, 400, 0)
    );
    private static final ItemStack hastePotion = InventoryHelper.generatePotion(
            WHITE + "Potion of Quickhand",
            Color.YELLOW,
            new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 1)
    );
    private static final ItemStack strengthPotion = InventoryHelper.generatePotion(
            WHITE + "Potion of Power",
            Color.fromRGB(100, 0, 0),
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0)
    );
    private static final ItemStack damagePotion = InventoryHelper.generatePotion(
            WHITE + "Sharp Vial",
            Color.BLACK,
            true,
            false,
            new PotionEffect(PotionEffectType.HARM, 1, 1)
    );
    private static final ItemStack levitationPotion = InventoryHelper.generatePotion(
            WHITE + "Liquid Helium",
            Color.WHITE,
            true,
            false,
            new PotionEffect(PotionEffectType.LEVITATION, 600, 0)
    );
    private static final ItemStack nightVisionPotion = InventoryHelper.generatePotion(
            WHITE + "Everseeing Potion",
            Color.BLUE,
            false,
            false,
            new PotionEffect(PotionEffectType.NIGHT_VISION, 24000, 0)
    );
    
    private static final WeightedItemList weightedDrops = new WeightedItemList(
            WeightedItem.builder(healingPotion).weight(2).build(),
            WeightedItem.builder(toxicPotion).weight(2).build(),
            WeightedItem.builder(blindnessPotion).weight(2).build(),
            WeightedItem.builder(hastePotion).weight(2).build(),
            WeightedItem.builder(strengthPotion).weight(2).build(),
            WeightedItem.builder(damagePotion).weight(2).build(),
            WeightedItem.builder(levitationPotion).weight(2).build(),
            WeightedItem.builder(nightVisionPotion).weight(2).build(),
            WeightedItem.builder(Material.GOLDEN_APPLE).amount(2).weight(2).build(),
            WeightedItem.builder(Material.DIAMOND).amount(9, 18).weight(2).build(),
            WeightedItem.builder(Material.GOLD_INGOT).amount(16, 24).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(new Enchant(Enchantment.THORNS, 3)).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.LOOT_BONUS_BLOCKS).level(2, 3).build()
            ).weight(2).build(),
            WeightedEnchantedItem.book().alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.LOOT_BONUS_MOBS).level(2, 3).build()
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
            WeightedItem.builder(Material.REDSTONE_BLOCK).amount(8, 12).weight(2).build(),
            WeightedItem.builder(Material.LAPIS_BLOCK).amount(4, 5).weight(2).build(),
            WeightedItem.builder(Material.SPECTRAL_ARROW).amount(24, 32).weight(2).build(),
            WeightedItem.builder(CustomItems.darkArtifact.getItemStack()).amount(15, 25).weight(2).build(),

            WeightedEnchantedItem.item(Material.TRIDENT)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.DURABILITY).level(3).build(),
                            WeightedEnchantment.builder(Enchantment.LOYALTY).level(1, 2).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.CHANNELING).build()
                    ).number(0, 1).weight(1).build(),

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

    public static WeightedItemList getWeightedDropsList() {
        return weightedDrops;
    }

}

package ru.drop;

import com.google.common.collect.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.items.CustomItems;
import ru.util.InventoryHelper;
import ru.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Drops {

    public static final List<Drop> DROPS = new ArrayList<>();

    public static final AirDrop AIRDROP = new AirDrop();
    public static final CaveDrop CAVEDROP = new CaveDrop();

    public static void update() {
        DROPS.forEach(Drop::update);
    }

    public static void firstSetup() {
        for(Drop drop : DROPS) {
            drop.setup();
            drop.timer -= MathUtils.randomRange(0, 30);
        }
    }

    public static ItemStack getRandomDrop() {
        return MathUtils.choose(getDrops());
    }

    public static List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Potion of Lustiness", Color.FUCHSIA, new PotionEffect(PotionEffectType.HEAL, 1, 1)));
        drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Toxic Vial", Color.GREEN, true, false, new PotionEffect(PotionEffectType.POISON, 240, 0)));
        drops.add(InventoryHelper
                .generatePotion(ChatColor.WHITE + "Potion of Eyebreaking", Color.GRAY, true, false, new PotionEffect(PotionEffectType.BLINDNESS, 400, 0)));
        drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Potion of Quickhand", Color.YELLOW, new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0)));
        drops.add(InventoryHelper
                .generatePotion(ChatColor.WHITE + "Potion of Power", Color.fromRGB(100, 0, 0), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0)));
        drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Sharp Vial", Color.BLACK, true, false, new PotionEffect(PotionEffectType.HARM, 1, 1)));
        drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Liquid Helium", Color.WHITE, true, false, new PotionEffect(PotionEffectType.LEVITATION, 400, 0)));

        drops.add(new ItemStack(Material.GOLDEN_APPLE, 2));
        drops.add(new ItemStack(Material.DIAMOND_BLOCK, MathUtils.randomRange(1, 2)));
        drops.add(new ItemStack(Material.GOLD_BLOCK, MathUtils.randomRange(2, 3)));

        Set<ItemStack> books = Sets.newHashSet(
                getBook(Enchantment.THORNS, 3),
                getBook(Enchantment.KNOCKBACK, 2),
                getBook(Enchantment.ARROW_KNOCKBACK, 2),
                getBook(Enchantment.LOOT_BONUS_BLOCKS, MathUtils.randomRange(2, 3)),
                getBook(Enchantment.FIRE_ASPECT, 2));
        drops.add(MathUtils.choose(books));

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, MathUtils.randomRange(2, 3));
        boots.addEnchantment(Enchantment.DEPTH_STRIDER, MathUtils.randomRange(1, 3));
        drops.add(boots);

        ItemStack pants = new ItemStack(Material.DIAMOND_LEGGINGS);
        pants.addEnchantment(Enchantment.PROTECTION_FIRE, MathUtils.randomRange(1, 3));
        pants.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, MathUtils.randomRange(1, 3));
        drops.add(pants);

        ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, MathUtils.randomRange(1, 2));
        chest.addEnchantment(Enchantment.THORNS, 1);
        drops.add(chest);

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addEnchantment(Enchantment.PROTECTION_PROJECTILE, MathUtils.randomRange(2, 3));
        helmet.addEnchantment(Enchantment.OXYGEN, MathUtils.randomRange(1, 3));
        drops.add(helmet);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, MathUtils.randomRange(1, 2));
        if(MathUtils.chance(50)) bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
        if(MathUtils.chance(25)) bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        drops.add(bow);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, MathUtils.randomRange(1, 2));
        if(MathUtils.chance(50)) sword.addEnchantment(Enchantment.KNOCKBACK, 1);
        if(MathUtils.chance(25)) sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
        drops.add(sword);

        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        axe.addUnsafeEnchantment(Enchantment.KNOCKBACK, MathUtils.randomRange(1, 2));
        if(MathUtils.chance(25)) axe.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        drops.add(axe);

        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        crossbow.addEnchantment(Enchantment.QUICK_CHARGE, MathUtils.randomRange(1, 3));
        if(MathUtils.chance(70)) crossbow.addEnchantment(Enchantment.PIERCING, 1);
        if(MathUtils.chance(15)) crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
        drops.add(crossbow);

        ItemStack trident = new ItemStack(Material.TRIDENT);
        trident.addEnchantment(Enchantment.DURABILITY, 3);
        trident.addEnchantment(Enchantment.LOYALTY, MathUtils.randomRange(2, 3));
        if(MathUtils.chance(70)) trident.addEnchantment(Enchantment.IMPALING, MathUtils.randomRange(2, 4));
        if(MathUtils.chance(40)) trident.addEnchantment(Enchantment.CHANNELING, 1);
        drops.add(crossbow);

        drops.add(new ItemStack(Material.ENCHANTING_TABLE));
        drops.add(new ItemStack(Material.REDSTONE_BLOCK, MathUtils.randomRange(8, 12)));
        drops.add(new ItemStack(Material.LAPIS_BLOCK, MathUtils.randomRange(3, 4)));
        drops.add(new ItemStack(Material.SPECTRAL_ARROW, MathUtils.randomRange(32, 48)));
        drops.add(new ItemStack(Material.EMERALD_ORE, MathUtils.randomRange(3, 5)));

        if(MathUtils.chance(40)) { //Making the chance of appearing netherite really low
            drops.add(new ItemStack(Material.NETHERITE_BOOTS));
            drops.add(new ItemStack(Material.NETHERITE_HELMET));
            drops.add(new ItemStack(Material.NETHERITE_LEGGINGS));
            drops.add(new ItemStack(Material.NETHERITE_CHESTPLATE));
            drops.add(new ItemStack(Material.NETHERITE_SWORD));
            drops.add(new ItemStack(Material.NETHERITE_AXE));
            drops.add(new ItemStack(Material.NETHERITE_PICKAXE));
            drops.add(new ItemStack(Material.NETHERITE_INGOT));
        }
        ItemStack artifact = CustomItems.darkArtifact.getItemStack();
        artifact.setAmount(MathUtils.randomRange(10, 18));
        drops.add(artifact);
        return drops;
    }

    private static ItemStack getBook(Enchantment ench, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
        bookMeta.addStoredEnchant(ench, level, true);
        book.setItemMeta(bookMeta);
        return book;
    }

}

package ru.UHC;

import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.mutator.MutatorManager;
import ru.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Drops {

	public static final int airdropDelay = 720, cavedropDelay = 960;
	public static int airdropTimer = airdropDelay, cavedropTimer = cavedropDelay;
	public static Location airdropLocation, cavedropLocation;
	private static final int maxDropHeight = 100;
	private static double height = 2.5, dropHeight = maxDropHeight;

	public static void setupAirdrop() {
		if(MutatorManager.isActive(MutatorManager.airdrop)) {
			airdropTimer = airdropDelay / 2;
		} else {
			airdropTimer = airdropDelay;
		}
		chooseAirdropLocation();
		dropHeight = maxDropHeight;
	}

	public static void setupCavedrop() {
		if(MutatorManager.isActive(MutatorManager.airdrop)) {
			cavedropTimer = cavedropDelay / 2;
		} else {
			cavedropTimer = cavedropDelay;
		}
		chooseCavedropLocation();
	}

	public static void update() {
		//Cavedrop
		if(cavedropTimer <= 0 && TaskManager.isSecUpdated()) {
			spawnCavedrop();
			setupCavedrop();
		} else {
			if(TaskManager.isSecUpdated()) {
				ParticleUtils.createParticlesInRange(cavedropLocation, 1.5, Particle.SMOKE_NORMAL, null, 10);
			}
			if(TaskManager.isSecUpdated()) cavedropTimer--;
		}
		//Airdrop
		if(TaskManager.isSecUpdated()) {
			for(Item item : airdropLocation.getWorld().getEntitiesByClass(Item.class)) {
				if(item.hasMetadata("airdrop")) {
					ParticleUtils.createLine(item.getLocation(), item.getLocation().clone().add(0, 25, 0), Particle.FLAME, 1, Color.BLACK);
				}
			}
		}
		if(airdropTimer <= 0 && TaskManager.isSecUpdated()) {
			spawnAirdrop();
			setupAirdrop();
		} else {
			if(TaskManager.tick % 2 == 0) {
				height -= 0.05;
				if(height < 0) {
					height = 2.5;
				}
				double radius = 1.8;
				double angle = height * Math.PI * 2;
				Location l = airdropLocation.clone().add(Math.sin(angle) * radius, height - 1, Math.cos(angle) * radius);
				Location l2 = airdropLocation.clone().add(Math.sin(angle + Math.PI) * radius, height - 1, Math.cos(angle + Math.PI) * radius);
				ParticleUtils.createParticle(l, Particle.CLOUD, null);
				ParticleUtils.createParticle(l2, Particle.CLOUD, null);
			}
			final int initDrop = 5;
			if(airdropTimer < initDrop) {
				dropHeight -= (double) maxDropHeight / (initDrop * 20.0);
				double radius = 1;
				double angle = (maxDropHeight / dropHeight) * Math.PI * 20;
				Location l = airdropLocation.clone().add(Math.sin(angle) * radius, dropHeight - 1, Math.cos(angle) * radius);
				Location l2 = airdropLocation.clone().add(Math.sin(angle + Math.PI) * radius, dropHeight - 1, Math.cos(angle + Math.PI) * radius);
				ParticleUtils.createParticle(l, Particle.CLOUD, null);
				ParticleUtils.createParticle(l2, Particle.CLOUD, null);
				if(TaskManager.tick % 5 == 0) {
					l.getWorld().playSound(l, Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);
				}
			}
			if(TaskManager.isSecUpdated()) airdropTimer--;
		}
	}

	public static void spawnAirdrop() {
		if(airdropLocation != null) {
			Item item = airdropLocation.getWorld().dropItem(airdropLocation, new ItemStack(getRandomDrop()));
			item.setGlowing(true);
			item.setPickupDelay(1);
			item.setMetadata("airdrop", new FixedMetadataValue(UHCPlugin.instance, true));
			airdropLocation.getWorld().playSound(airdropLocation, Sound.ENTITY_ITEM_PICKUP, 1F, 0.5F);
			airdropLocation.getWorld().playSound(airdropLocation, Sound.BLOCK_WOOL_BREAK, 1.5F, 0.5F);
			ParticleUtils.createParticlesInsideSphere(airdropLocation, 3, Particle.REDSTONE, Color.WHITE, 40);
			for(Player p : UHC.getInGamePlayers()) {
				p.sendTitle("", ChatColor.AQUA + "Аирдроп" + ChatColor.DARK_AQUA + " заспавнен!", 10, 40, 20);
				String comma = ChatColor.WHITE + ", ";
				String airdropLocInfo =
						ChatColor.YELLOW + "Корды аирдропа: " + ChatColor.DARK_AQUA + airdropLocation.getBlockX() + comma + ChatColor.DARK_AQUA + airdropLocation.getBlockY()
								+ comma + ChatColor.DARK_AQUA + airdropLocation.getBlockZ();
				p.sendMessage(airdropLocInfo);
				p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5F, 1.5F);
			}
		}
	}

	public static void spawnCavedrop() {
		if(cavedropLocation != null) {
			for(int x = -1; x <= 1; x++) {
				for(int y = -1; y <= 1; y++) {
					for(int z = -1; z <= 1; z++) {
						Block block = cavedropLocation.clone().add(x, y, z).getBlock();
						block.setType(Material.RED_STAINED_GLASS);
					}
				}
			}
			Block chestBlock = cavedropLocation.getBlock();
			chestBlock.setType(Material.CHEST);
			Chest chest = (Chest) chestBlock.getState();
			Inventory inv = chest.getBlockInventory();
			Set<Integer> slotsToFill = Sets.newHashSet();
			for(int i = 0; i < inv.getSize(); i++) {
				slotsToFill.add(i);
			}
			int items = MathUtils.randomRange(3, 7);
			for(int i = 0; i < items; i++) {
				int slot = MathUtils.choose(slotsToFill);
				slotsToFill.remove(slot);
				inv.setItem(slot, i == 0 ? getRandomDrop() : getRandomFiller());
			}
			cavedropLocation.getWorld().playSound(cavedropLocation, Sound.ITEM_FIRECHARGE_USE, 1F, 0.5F);
			ParticleUtils.createParticlesInRange(cavedropLocation, 1.5, Particle.FLAME, null, 40);
			for(Player p : UHC.getInGamePlayers()) {
				p.sendTitle("", ChatColor.RED + "Кейвдроп" + ChatColor.DARK_AQUA + " заспавнен!", 5, 40, 20);
				String comma = ChatColor.WHITE + ", ";
				String airdropLocInfo =
						ChatColor.YELLOW + "Корды кейвдропа: " + ChatColor.DARK_AQUA + cavedropLocation.getBlockX() + comma + ChatColor.DARK_AQUA + cavedropLocation.getBlockY()
								+ comma + ChatColor.DARK_AQUA + cavedropLocation.getBlockZ();
				p.sendMessage(airdropLocInfo);
				p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5F, 0.5F);
			}
		}
	}

	public static void chooseAirdropLocation() {
		int size = ((int) WorldManager.getGameMap().getWorldBorder().getSize()) / 2 - 10;
		int x = MathUtils.randomRange(WorldManager.spawnLocation.getBlockX() - size, WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(WorldManager.spawnLocation.getBlockZ() - size, WorldManager.spawnLocation.getBlockZ() + size);
		int y = WorldManager.getGameMap().getHighestBlockYAt(x, z) + 1;
		airdropLocation = new Location(WorldManager.getGameMap(), x, y, z);
	}

	public static void chooseCavedropLocation() {
		int size = ((int) WorldManager.getGameMap().getWorldBorder().getSize()) / 2 - 10;
		int x = MathUtils.randomRange(WorldManager.spawnLocation.getBlockX() - size, WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(WorldManager.spawnLocation.getBlockZ() - size, WorldManager.spawnLocation.getBlockZ() + size);
		int y = MathUtils.randomRange(7, 16);
		cavedropLocation = new Location(WorldManager.getGameMap(), x, y, z);
	}

	public static ItemStack getRandomDrop() {
		return MathUtils.choose(getDrops());
	}

	public static ItemStack getRandomFiller() {
		return MathUtils.choose(getCavedropFillers());
	}

	public static List<ItemStack> getCavedropFillers() {
		List<ItemStack> fillers = new ArrayList<>();
		fillers.add(new ItemStack(Material.STRING, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.BREAD, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.CARROT, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.FEATHER, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.LEATHER));
		fillers.add(new ItemStack(Material.LAPIS_LAZULI, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.REDSTONE, MathUtils.randomRange(1, 5)));
		fillers.add(new ItemStack(Material.IRON_INGOT, MathUtils.randomRange(1, 3)));
		fillers.add(new ItemStack(Material.GOLD_INGOT, MathUtils.randomRange(1, 2)));
		fillers.add(CustomItems.darkArtifact.getItemStack());
		if(MathUtils.chance(50)) fillers.add(new ItemStack(Material.APPLE));
		if(MathUtils.chance(30)) fillers.add(new ItemStack(Material.DIAMOND));
		return fillers;
	}

	public static List<ItemStack> getDrops() {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(Material.GOLDEN_APPLE, 2));
		drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Potion of Lustiness", Color.FUCHSIA, new PotionEffect(PotionEffectType.HEAL, 1, 1)));
		drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Toxic Vial", Color.GREEN, true, false, new PotionEffect(PotionEffectType.POISON, 240, 0)));
		drops.add(InventoryHelper
				.generatePotion(ChatColor.WHITE + "Potion of Eyebreaking", Color.GRAY, true, false, new PotionEffect(PotionEffectType.BLINDNESS, 400, 0)));
		drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Potion of Quickhand", Color.YELLOW, new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0)));
		drops.add(InventoryHelper
				.generatePotion(ChatColor.WHITE + "Potion of Power", Color.fromRGB(100, 0, 0), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0)));
		drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Sharp Vial", Color.BLACK, true, false, new PotionEffect(PotionEffectType.HARM, 1, 1)));
		drops.add(InventoryHelper.generatePotion(ChatColor.WHITE + "Liquid Helium", Color.WHITE, true, false, new PotionEffect(PotionEffectType.LEVITATION, 400, 0)));
		drops.add(new ItemStack(Material.DIAMOND_BLOCK, MathUtils.randomRange(1, 2)));
		drops.add(new ItemStack(Material.GOLD_BLOCK, MathUtils.randomRange(2, 3)));
		Set<ItemStack> books = Sets.newHashSet(getBook(Enchantment.THORNS, 3), getBook(Enchantment.KNOCKBACK, 2), getBook(Enchantment.ARROW_KNOCKBACK, 2), getBook(Enchantment.LOOT_BONUS_BLOCKS, MathUtils.randomRange(2, 3)), getBook(Enchantment.FIRE_ASPECT, 2));
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
		ItemStack crossbow = new ItemStack(Material.CROSSBOW);
		crossbow.addEnchantment(Enchantment.QUICK_CHARGE, MathUtils.randomRange(1, 3));
		if(MathUtils.chance(70)) crossbow.addEnchantment(Enchantment.PIERCING, 1);
		if(MathUtils.chance(15)) crossbow.addEnchantment(Enchantment.MULTISHOT, 1);
		drops.add(crossbow);
		ItemStack trident = new ItemStack(Material.TRIDENT);
		trident.addEnchantment(Enchantment.DURABILITY, 5);
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

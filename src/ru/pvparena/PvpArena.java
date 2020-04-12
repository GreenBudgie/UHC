package ru.pvparena;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.UHC.PlayerStat;
import ru.UHC.SignManager;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PvpArena implements Listener {

	public static boolean isOpen = true;
	public static boolean isDuel = false;
	public static Player duelingPlayer1 = null, duelingPlayer2 = null;
	public static Set<Player> onArena = new HashSet<>();
	public static Location arenaSpawnLocation, duelSpawn1, duelSpawn2, duelInitSpot1, duelInitSpot2;
	public static List<Kit> kits = new ArrayList<>();
	public static Kit currentKit = null;
	public static int killsToNextKit = 8;

	public static void init() {

		arenaSpawnLocation = new Location(WorldManager.getLobby(), -49.5, 7, -23.5, 180, 0);
		duelSpawn1 = new Location(WorldManager.getLobby(), -35, 7, -45, 90, 0);
		duelSpawn2 = new Location(WorldManager.getLobby(), -64, 7, -45, -90, 0);
		duelInitSpot1 = new Location(WorldManager.getLobby(), -45, 7, -23);
		duelInitSpot2 = new Location(WorldManager.getLobby(), -45, 7, -25);

		Kit oneShotKillKit = new Kit("One-shot-kill");
		oneShotKillKit.addItem(
				ItemUtils.builder(Material.DIAMOND_SWORD).withEnchantments(new ItemUtils.Enchant(Enchantment.DAMAGE_ALL, 100)).withFlags(ItemFlag.HIDE_ENCHANTS).build());
		oneShotKillKit.withShield();

		Kit diamondKit = new Kit("Алмазка");
		diamondKit.addArmorSet(Kit.ArmorMaterial.DIAMOND);
		diamondKit.addItem(Material.DIAMOND_SWORD);
		diamondKit.addItem(Material.DIAMOND_AXE);
		diamondKit.withShield();

		Kit ironKit = new Kit("Железка");
		ironKit.addArmorSet(Kit.ArmorMaterial.IRON);
		ironKit.addItem(Material.IRON_SWORD);
		ironKit.addItem(Material.IRON_AXE);
		ironKit.withShield();

		Kit goldenKit = new Kit("Золото-железка");
		goldenKit.addArmorSet(Kit.ArmorMaterial.IRON);
		goldenKit.addItem(Material.GOLDEN_SWORD);
		goldenKit.addItem(Material.GOLDEN_AXE);
		goldenKit.withShield();

		Kit chainKit = new Kit("Кольчуга");
		chainKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		chainKit.addItem(Material.STONE_SWORD);
		chainKit.addItem(Material.STONE_AXE);
		chainKit.withShield();

		Kit leatherKit = new Kit("Кожанка");
		leatherKit.addArmorSet(Kit.ArmorMaterial.LEATHER);
		leatherKit.addItem(Material.WOODEN_SWORD);
		leatherKit.addItem(Material.WOODEN_AXE);
		leatherKit.withShield();

		Kit uselessKit = new Kit("Бесполезная лопата");
		uselessKit.addItem(Material.WOODEN_SHOVEL);

		Kit healingKit = new Kit("Много хила");
		healingKit.addArmorSet(Kit.ArmorMaterial.LEATHER);
		healingKit.addItem(Material.IRON_SWORD);
		ItemStack potion = ItemUtils.potionBuilder().asSplash().withColor(Color.FUCHSIA).withName("Heal").withEffects(new PotionEffect(PotionEffectType.HEAL, 1, 1)).build();
		for(int i = 0; i < 8; i++) {
			healingKit.addItem(potion);
		}

		Kit minerKit = new Kit("Шахтер");
		minerKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		minerKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.LAPIS_LAZULI), new ItemUtils.Enchant(Enchantment.DAMAGE_ALL, 4)));
		minerKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.GOLDEN_PICKAXE), new ItemUtils.Enchant(Enchantment.KNOCKBACK, 3)));
		minerKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.REDSTONE_ORE), new ItemUtils.Enchant(Enchantment.FIRE_ASPECT, 1)));

		Kit strangeKit = new Kit("Полет");
		strangeKit.addArmorSet(Kit.ArmorMaterial.LEATHER);
		strangeKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.STONE_SHOVEL), new ItemUtils.Enchant(Enchantment.KNOCKBACK, 4)));
		strangeKit.addItem(
				ItemUtils.potionBuilder().asSplash().withColor(Color.WHITE).withEffects(new PotionEffect(PotionEffectType.LEVITATION, 80, 1)).withName("Levitation Vial")
						.build());

		Kit archerKit = new Kit("Лучник");
		archerKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		archerKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.BOW), new ItemUtils.Enchant(Enchantment.ARROW_INFINITE)));
		archerKit.addItem(Material.ARROW);

		Kit crossbowKit = new Kit("Арбалетчик");
		crossbowKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		crossbowKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.CROSSBOW), new ItemUtils.Enchant(Enchantment.QUICK_CHARGE, 3)));
		for(int i = 0; i < 8; i++) {
			crossbowKit.addItem(new ItemStack(Material.ARROW, 64));
		}

		Kit advCrossbowKit = new Kit("Продвинутый Арбалетчик");
		advCrossbowKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		advCrossbowKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.CROSSBOW), new ItemUtils.Enchant(Enchantment.QUICK_CHARGE, 3)));
		ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, 64);
		FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
		meta.setPower(1);
		meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR).withColor(Color.MAROON).build());
		firework.setItemMeta(meta);
		advCrossbowKit.putItem(firework, InventoryHelper.getOffHandSlot());

		Kit advArcherKit = new Kit("Лучник 2");
		advArcherKit.addArmorSet(Kit.ArmorMaterial.DIAMOND);
		advArcherKit.addItem(
				ItemUtils.addEnchantments(new ItemStack(Material.BOW), new ItemUtils.Enchant(Enchantment.ARROW_INFINITE), new ItemUtils.Enchant(Enchantment.ARROW_FIRE)));
		advArcherKit.addItem(Material.ARROW);

		Kit mixedKit = new Kit("Смешанный");
		mixedKit.addArmorSet(Kit.ArmorMaterial.IRON);
		mixedKit.addItem(Material.IRON_SWORD);
		mixedKit.addItem(Material.IRON_AXE);
		mixedKit.addItem(ItemUtils.addEnchantments(new ItemStack(Material.BOW), new ItemUtils.Enchant(Enchantment.ARROW_INFINITE)));
		mixedKit.addItem(Material.ARROW);
		mixedKit.withShield();

		Kit mixedKit2 = new Kit("Смешанный 2");
		mixedKit2.addArmorSet(Kit.ArmorMaterial.DIAMOND);
		mixedKit2.addItem(Material.STONE_SWORD);
		mixedKit2.addItem(Material.STONE_AXE);
		mixedKit2.addItem(ItemUtils
				.addEnchantments(new ItemStack(Material.BOW), new ItemUtils.Enchant(Enchantment.ARROW_INFINITE), new ItemUtils.Enchant(Enchantment.ARROW_KNOCKBACK),
						new ItemUtils.Enchant(Enchantment.ARROW_DAMAGE, 2)));
		mixedKit2.addItem(Material.ARROW);
		mixedKit2.addItem(new ItemStack(Material.GOLDEN_APPLE));
		mixedKit2.withShield();

		Kit mixedKit3 = new Kit("Смешанный 3");
		mixedKit3.addArmorSet(Kit.ArmorMaterial.IRON);
		mixedKit3.addItem(Material.IRON_SWORD);
		mixedKit3.addItem(Material.IRON_AXE);
		mixedKit3.addItem(
				ItemUtils.addEnchantments(new ItemStack(Material.BOW), new ItemUtils.Enchant(Enchantment.ARROW_INFINITE), new ItemUtils.Enchant(Enchantment.ARROW_FIRE)));
		mixedKit3.addItem(
				ItemUtils.addEnchantments(new ItemStack(Material.CROSSBOW), new ItemUtils.Enchant(Enchantment.MULTISHOT), new ItemUtils.Enchant(Enchantment.PIERCING)));
		for(int i = 0; i < 5; i++) {
			mixedKit3.addItem(new ItemStack(Material.ARROW, 64));
		}
		mixedKit3.withShield();

		Kit wizardKit = new Kit("Маг");
		wizardKit.addArmorSet(Kit.ArmorMaterial.IRON);
		wizardKit.addItem(Material.STONE_SWORD);
		wizardKit.addItem(Material.STONE_AXE);
		wizardKit.addItem(
				ItemUtils.potionBuilder().asSplash().withColor(Color.BLACK).withEffects(new PotionEffect(PotionEffectType.HARM, 1, 1)).withName("Sharp Vial").build());
		wizardKit.addItem(
				ItemUtils.potionBuilder().asSplash().withColor(Color.GREEN).withEffects(new PotionEffect(PotionEffectType.POISON, 300, 0)).withName("Toxic Vial").build());
		wizardKit.addItem(ItemUtils.potionBuilder().asSplash().withColor(Color.GRAY)
				.withEffects(new PotionEffect(PotionEffectType.SLOW, 200, 1), new PotionEffect(PotionEffectType.BLINDNESS, 160, 0),
						new PotionEffect(PotionEffectType.WEAKNESS, 200, 0)).withName("Vial of Weakness").build());
		wizardKit.addItem(
				ItemUtils.potionBuilder().asDrinkable().withColor(Color.RED).withEffects(new PotionEffect(PotionEffectType.HEAL, 1, 1)).withName("Healing Vial").build());
		wizardKit.withShield();

		Kit wizardKit2 = new Kit("Маг 2");
		wizardKit2.addArmorSet(Kit.ArmorMaterial.DIAMOND);
		wizardKit2.addItem(Material.GOLDEN_SWORD);
		wizardKit2.addItem(Material.GOLDEN_AXE);
		wizardKit2.addItem(
				ItemUtils.potionBuilder().asDrinkable().withColor(Color.RED).withEffects(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1)).withName("Strength Vial")
						.build());
		wizardKit2.addItem(
				ItemUtils.potionBuilder().asDrinkable().withColor(Color.GRAY).withEffects(new PotionEffect(PotionEffectType.SPEED, 160, 3)).withName("Hyper Speed Vial")
						.build());
		wizardKit2.addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
		wizardKit2.addItem(ItemUtils.potionBuilder().asSplash().withColor(Color.GREEN)
				.withEffects(new PotionEffect(PotionEffectType.CONFUSION, 400, 0), new PotionEffect(PotionEffectType.POISON, 100, 0)).withName("Bad Vial").build());
		wizardKit2.withShield();

		currentKit = getRandomKit();
	}

	public static Kit getRandomKit() {
		List<Kit> availableKits = Lists.newArrayList(kits);
		if(currentKit != null) {
			availableKits.remove(currentKit);
		}
		return MathUtils.choose(availableKits);
	}

	private static List<Player> getLobbyPlayers() {
		return WorldManager.getLobby().getPlayers();
	}

	public static boolean isDueling(Player p) {
		return isDuel && (duelingPlayer1 == p || duelingPlayer2 == p);
	}

	public static boolean isOnArena(Player p) {
		return onArena.contains(p);
	}

	public static void removeKit(Player p) {
		InventoryHelper.removeItemsExcept(p.getInventory(), Material.REDSTONE, Material.PLAYER_HEAD);
	}

	public static void initDuel(Player p1, Player p2) {
		currentKit = getRandomKit();
		duelingPlayer1 = p1;
		duelingPlayer2 = p2;
		p1.teleport(duelSpawn1);
		p2.teleport(duelSpawn2);
		heal(p1);
		heal(p2);
		onArenaEnter(p1);
		onArenaEnter(p2);
		isDuel = true;
		for(Player p : getLobbyPlayers()) {
			p.sendTitle(ChatColor.AQUA + "Дуэль!", ChatColor.GOLD + p1.getName() + ChatColor.RED + ChatColor.BOLD + " VS " + ChatColor.RESET + ChatColor.GOLD + p2.getName(),
					5, 40, 10);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1F);
			heal(p);
		}
		PlayerStat.DUEL_COUNT.increaseValue(p1, 1);
		PlayerStat.DUEL_COUNT.increaseValue(p2, 1);
	}

	public static void endDuel(Player winner, Player loser) {
		duelingPlayer1 = null;
		duelingPlayer2 = null;
		for(Player player : getLobbyPlayers()) {
			player.sendTitle(ChatColor.DARK_AQUA + "Дуэль завершена", ChatColor.GOLD + winner.getName() + ChatColor.YELLOW + " замачил " + ChatColor.GOLD + loser.getName(), 5,
					60, 20);
		}
		heal(winner);
		Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(2);
		meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).withFlicker().build());
		firework.setFireworkMeta(meta);
		openArena();
		PlayerStat.DUEL_WINS.increaseValue(winner, 1);
		isDuel = false;
	}

	public static Player getRival(Player p) {
		if(!isDuel || p == null || duelingPlayer1 == null || duelingPlayer2 == null) return null;
		if(duelingPlayer1 == p) return duelingPlayer2;
		if(duelingPlayer2 == p) return duelingPlayer1;
		return null;
	}

	public static List<Player> getDuelWaitingPlayers() {
		List<Player> players = new ArrayList<>();
		for(Player p : getLobbyPlayers()) {
			if(WorldHelper.compareLocations(p.getLocation(), duelInitSpot1) || WorldHelper.compareLocations(p.getLocation(), duelInitSpot2)) {
				players.add(p);
			}
		}
		return players;
	}

	public static void openArena() {
		for(int x = -53; x <= -47; x++) {
			for(int y = 7; y <= 11; y++) {
				new Location(WorldManager.getLobby(), x, y, -28).getBlock().setType(Material.AIR);
			}
		}
		for(int x = -52; x <= -48; x++) {
			for(int y = 7; y <= 10; y++) {
				new Location(WorldManager.getLobby(), x, y, -27).getBlock().setType(Material.AIR);
			}
		}
		WorldManager.getLobby().playSound(new Location(WorldManager.getLobby(), -50, 8, -28), Sound.BLOCK_STONE_BREAK, 1F, 1F);
		isOpen = true;
	}

	public static void closeArena() {
		for(int x = -53; x <= -47; x++) {
			for(int y = 7; y <= 11; y++) {
				new Location(WorldManager.getLobby(), x, y, -28).getBlock().setType(Material.RED_STAINED_GLASS);
			}
		}
		for(int x = -52; x <= -48; x++) {
			for(int y = 7; y <= 10; y++) {
				new Location(WorldManager.getLobby(), x, y, -27).getBlock().setType(Material.RED_STAINED_GLASS);
			}
		}
		WorldManager.getLobby().playSound(new Location(WorldManager.getLobby(), -50, 8, -28), Sound.BLOCK_STONE_PLACE, 1F, 1F);
		isOpen = false;
	}

	/**
	 * Calls when a player leaves an arena in any way: walking out, using /lobby, quitting or dying
	 */
	public static void onArenaLeave(Player p) {
		if(isOnArena(p)) {
			onArena.remove(p);
			removeKit(p);
			if(isDueling(p)) {
				Player rival = getRival(p);
				if(rival != null) {
					endDuel(rival, p);
				}
			} else {
				InventoryHelper.sendActionBarMessage(p, ChatColor.GOLD + "Ты вышел с арены");
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
			}
		}
	}

	/**
	 * Calls when a player enters an arena in any way
	 */
	public static void onArenaEnter(Player p) {
		if(!isOnArena(p)) {
			currentKit.give(p);
			InventoryHelper.sendActionBarMessage(p, ChatColor.RED + "Ты зашел на арену");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1F);
			onArena.add(p);
		}
	}

	public static void update() {
		if(TaskManager.isSecUpdated()) {
			List<Player> waiting = getDuelWaitingPlayers();
			for(Player p : waiting) {
				if(waiting.size() == 1) {
					InventoryHelper.sendActionBarMessage(p, ChatColor.GRAY + "Ожидаем второго игрока...");
				} else if(waiting.size() >= 2 && !onArena.isEmpty()) {
					InventoryHelper.sendActionBarMessage(p, ChatColor.GRAY + "Ожидаем окончания битвы на арене...");
				}
			}
			if(waiting.size() >= 2) {
				if(isOpen) {
					closeArena();
				}
				if(onArena.size() <= 1) {
					if(onArena.size() == 1) {
						for(Player player : onArena) {
							player.teleport(arenaSpawnLocation);
							onArenaLeave(player);
						}
					}
					initDuel(waiting.get(0), waiting.get(1));
				}
			} else {
				if(!isOpen && !isDuel) {
					openArena();
				}
			}
		}
	}

	public static void heal(Player p) {
		p.getActivePotionEffects().forEach(ef -> p.removePotionEffect(ef.getType()));
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		p.setHealth(20);
		p.setFireTicks(0);
		p.setSaturation(20);
		p.setFoodLevel(20);
	}

	@EventHandler
	public void teleport(PlayerTeleportEvent e) {
		onArenaLeave(e.getPlayer());
	}

	@EventHandler
	public void quit(PlayerQuitEvent e) {
		onArenaLeave(e.getPlayer());
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(UHC.isInLobby(p)) {
			Location l = e.getTo();
			int x = l.getBlockX();
			int y = l.getBlockY();
			int z = l.getBlockZ();
			if(isOnArena(p) && x >= -52 && x <= -48 && y >= 7 && y <= 10 && z == -26) {
				onArenaLeave(p);
			}
			if(!isOnArena(p) && x >= -52 && x <= -48 && y >= 7 && y <= 10 && z == -28) {
				onArenaEnter(p);
			}
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(UHC.isInLobby(p)) {
			heal(p);
			e.getDrops().clear();
			e.setKeepInventory(true);
			if(isOnArena(p)) {
				onArenaLeave(p);
				killsToNextKit--;
				if(killsToNextKit <= 0) {
					killsToNextKit = 8;
					currentKit = getRandomKit();
					for(Player player : onArena) {
						InventoryHelper
								.sendActionBarMessage(player, ChatColor.GOLD + "В следующий раз будет выдан новый набор: " + ChatColor.LIGHT_PURPLE + currentKit.getName());
					}
				}
				PlayerStat.ARENA_DEATHS.increaseValue(p, 1);
				Player killer = p.getKiller();
				if(killer != null) {
					heal(killer);
					removeKit(killer);
					currentKit.give(killer);
					killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.8F, 1F);
					PlayerStat.ARENA_KILLS.increaseValue(killer, 1);
				}
				for(Arrow arrow : p.getWorld().getEntitiesByClass(Arrow.class)) {
					if(arrow.isInBlock()) {
						arrow.remove();
					}
				}
				SignManager.updateSigns();
				p.teleport(arenaSpawnLocation);
				TaskManager.invokeLater(() -> p.setVelocity(new Vector(0, 0, 0)));
			} else {
				p.teleport(WorldManager.getLobby().getSpawnLocation());
			}
		}
	}

}

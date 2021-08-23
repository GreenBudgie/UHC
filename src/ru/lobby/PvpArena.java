package ru.lobby;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
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
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.lobby.sign.SignManager;
import ru.main.UHCPlugin;
import ru.util.*;

import java.util.*;

public class PvpArena extends LobbyGame implements Listener {

	private boolean isOpen = true;
	private boolean isDuel = false;
	private Player duelingPlayer1 = null, duelingPlayer2 = null;
	private Set<Player> onArena = new HashSet<>();
	private Location spawnLocation, duelSpawn1, duelSpawn2, duelInitSpot1, duelInitSpot2;
	private List<Kit> kits = new ArrayList<>();
	private Kit currentKit = null;
	private int killsToNextKit = 8;
	private Region enterRegion, leaveRegion, closeRegion;

	protected PvpArena() {
		Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
	}

	protected void postSetup() {
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

	@Override
	public String getConfigName() {
		return "pvparena";
	}

	@Override
	@SuppressWarnings("unchecked")
	public void parseConfigOption(String option, Object value) {
		String[] locationBased = new String[] {
				"spawnLocation",
				"duelInitSpot1",
				"duelInitSpot2",
				"duelSpawn1",
				"duelSpawn2"
		};
		String[] regionBased = new String[] {
				"enterRegion",
				"leaveRegion",
				"closeRegion"
		};
		if(Arrays.asList(locationBased).contains(option)) {
			String locationStr = (String) value;
			Location location = WorldHelper.translateToLocation(Lobby.getLobby(), locationStr);
			if(location == null) {
				UHCPlugin.warning(option + " is not present in " + getConfigName());
				return;
			}
			location.setWorld(Lobby.getLobby());
			switch(option) {
				case "spawnLocation" -> spawnLocation = location;
				case "duelInitSpot1" -> duelInitSpot1 = location;
				case "duelInitSpot2" -> duelInitSpot2 = location;
				case "duelSpawn1" -> duelSpawn1 = location;
				case "duelSpawn2" -> duelSpawn2 = location;
			}
		} else if(Arrays.asList(regionBased).contains(option)) {
			ConfigurationSection regionSection = (ConfigurationSection) value;
			Region region = Region.deserialize(regionSection.getValues(false));
			if(region == null) {
				UHCPlugin.warning(option + " is not present in " + getConfigName());
				return;
			}
			region.setWorld(Lobby.getLobby());
			switch(option) {
				case "enterRegion" -> enterRegion = region;
				case "leaveRegion" -> leaveRegion = region;
				case "closeRegion" -> closeRegion = region;
			}
		}
	}

	public Kit getRandomKit() {
		List<Kit> availableKits = Lists.newArrayList(kits);
		if(currentKit != null) {
			availableKits.remove(currentKit);
		}
		return MathUtils.choose(availableKits);
	}

	private List<Player> getLobbyPlayers() {
		return WorldManager.getLobby().getPlayers();
	}

	public boolean isDueling(Player p) {
		return isDuel && (duelingPlayer1 == p || duelingPlayer2 == p);
	}

	public boolean isOnArena(Player player) {
		return onArena.contains(player);
	}

	public void initDuel(Player p1, Player p2) {
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
	}

	public void endDuel(Player winner, Player loser) {
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
		isDuel = false;
	}

	public Player getRival(Player p) {
		if(!isDuel || p == null || duelingPlayer1 == null || duelingPlayer2 == null) return null;
		if(duelingPlayer1 == p) return duelingPlayer2;
		if(duelingPlayer2 == p) return duelingPlayer1;
		return null;
	}

	public List<Player> getDuelWaitingPlayers() {
		List<Player> players = new ArrayList<>();
		for(Player p : getLobbyPlayers()) {
			if(WorldHelper.compareIntLocations(p.getLocation(), duelInitSpot1) || WorldHelper.compareIntLocations(p.getLocation(), duelInitSpot2)) {
				players.add(p);
			}
		}
		return players;
	}

	public void openArena() {
		for(Block block : closeRegion.getBlocksInside()) {
			block.setType(Material.AIR);
		}
		WorldManager.getLobby().playSound(closeRegion.getStartLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_OPEN, 1F, 0.6F);
		isOpen = true;
	}

	public void closeArena() {
		for(Block block : closeRegion.getBlocksInside()) {
			block.setType(Material.SPRUCE_FENCE);
		}
		WorldManager.getLobby().playSound(closeRegion.getStartLocation(), Sound.BLOCK_WOODEN_TRAPDOOR_CLOSE, 1F, 0.6F);
		isOpen = false;
	}

	/**
	 * Calls when a player leaves an arena in any way: walking out, using /lobby, quitting or dying
	 */
	public void onArenaLeave(Player p) {
		if(isOnArena(p)) {
			onArena.remove(p);
			p.getInventory().clear();
			if(isDueling(p)) {
				Player rival = getRival(p);
				if(rival != null) {
					endDuel(rival, p);
				}
			} else {
				InventoryHelper.sendActionBarMessage(p, ChatColor.GRAY + "> " +
						ChatColor.GOLD + ChatColor.BOLD + "Ты вышел с арены" +
						ChatColor.RESET + ChatColor.GRAY + " <");
				p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5F, 0.8F);
			}
		}
	}

	/**
	 * Calls when a player enters an arena in any way
	 */
	public void onArenaEnter(Player p) {
		if(!isOnArena(p)) {
			if(isDuel) {
				p.teleport(spawnLocation);
				InventoryHelper.sendActionBarMessage(p, ChatColor.DARK_RED + "Сейчас идет дуэль");
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
			} else {
				currentKit.give(p);
				InventoryHelper.sendActionBarMessage(p, ChatColor.GRAY + "> " +
						ChatColor.RED + ChatColor.BOLD + "Ты зашел на арену" +
						ChatColor.RESET + ChatColor.GRAY + " <");
				p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.5F, 1F);
				onArena.add(p);
			}
		}
	}

	public void update() {
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
							player.teleport(spawnLocation);
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

	public boolean isOpen() {
		return isOpen;
	}

	public List<Kit> getKits() {
		return kits;
	}

	public Kit getCurrentKit() {
		return currentKit;
	}

	public void setCurrentKit(Kit currentKit) {
		this.currentKit = currentKit;
	}

	public int getKillsToNextKit() {
		return killsToNextKit;
	}

	public void setKillsToNextKit(int killsToNextKit) {
		this.killsToNextKit = killsToNextKit;
	}

	public Set<Player> getPlayersOnArena() {
		return onArena;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void heal(Player p) {
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
		Player player = e.getPlayer();
		if(UHC.isInLobby(player)) {
			Location to = e.getTo();
			if(isOnArena(player) && leaveRegion.isInside(to)) {
				onArenaLeave(player);
			}
			if(!isOnArena(player) && enterRegion.isInside(to)) {
				onArenaEnter(player);
			}
		}
	}

	@EventHandler
	public void death(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if(UHC.isInLobby(player)) {
			heal(player);
			e.getDrops().clear();
			e.setKeepInventory(true);
			if(isOnArena(player)) {
				onArenaLeave(player);
				killsToNextKit--;
				if(killsToNextKit <= 0) {
					killsToNextKit = 8;
					currentKit = getRandomKit();
					for(Player currentPlayer : onArena) {
						InventoryHelper.sendActionBarMessage(currentPlayer,
										ChatColor.GOLD + "В следующий раз будет выдан новый набор: " +
												ChatColor.LIGHT_PURPLE + currentKit.getName());
					}
				}
				Player killer = player.getKiller();
				if(killer != null) {
					heal(killer);
					killer.getInventory().clear();
					currentKit.give(killer);
					killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.8F, 1F);
				}
				for(Arrow arrow : player.getWorld().getEntitiesByClass(Arrow.class)) {
					if(arrow.isInBlock()) {
						arrow.remove();
					}
				}
				SignManager.updateTextOnSigns();
				player.teleport(spawnLocation);
				TaskManager.invokeLater(() -> player.setVelocity(new Vector(0, 0, 0)));
			} else {
				player.teleport(WorldManager.getLobby().getSpawnLocation());
			}
		}
	}

}

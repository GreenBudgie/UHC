package ru.greenbudgie.lobby.game.arena;

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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.event.BeforeGameInitializeEvent;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.lobby.game.LobbyGame;
import ru.greenbudgie.lobby.sign.SignManager;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.*;

import java.util.*;

import static org.bukkit.ChatColor.*;

public class LobbyGamePvpArena extends LobbyGame implements Listener {

	private boolean isOpen = true;
	private boolean isDuel = false;
	private Player duelingPlayer1 = null, duelingPlayer2 = null;
	private Set<Player> onArena = new HashSet<>();
	private Location spawnLocation, duelSpawn1, duelSpawn2, duelInitSpot1, duelInitSpot2;
	private List<Kit> kits = new ArrayList<>();
	private Kit currentKit = null;
	private int killsToNextKit = 8;
	private Region enterRegion, leaveRegion, closeRegion;

	public LobbyGamePvpArena() {
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

		Kit usefulKit = new Kit("НЕ бесполезная лопата");
		usefulKit.addArmorSet(Kit.ArmorMaterial.CHAIN);
		usefulKit.addItem(ItemUtils.builder(Material.WOODEN_SHOVEL).withEnchantments(
				new ItemUtils.Enchant(Enchantment.KNOCKBACK, 10),
				new ItemUtils.Enchant(Enchantment.FIRE_ASPECT, 2),
				new ItemUtils.Enchant(Enchantment.DAMAGE_ALL, 1)).build());

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
				ItemUtils.addEnchantments(new ItemStack(Material.CROSSBOW), new ItemUtils.Enchant(Enchantment.QUICK_CHARGE, 2), new ItemUtils.Enchant(Enchantment.MULTISHOT), new ItemUtils.Enchant(Enchantment.PIERCING)));
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

		Kit wizardKit3 = new Kit("Маг 3");
		wizardKit3.addArmorSet(Kit.ArmorMaterial.NETHERITE);
		wizardKit3.addItem(ItemUtils.builder(Material.GOLDEN_AXE).
				withEnchantments(new ItemUtils.Enchant(Enchantment.FIRE_ASPECT)).
				build());
		ItemStack damagingLiquid = ItemUtils.potionBuilder().
				asLingering().
				withColor(Color.BLACK).
				withName(DARK_GRAY + "Damaging Liquid").
				withEffects(new PotionEffect(PotionEffectType.HARM, 1, 0)).
				build();
		wizardKit3.addItem(damagingLiquid);
		wizardKit3.addItem(damagingLiquid);
		wizardKit3.addItem(ItemUtils.potionBuilder().
				asLingering().
				withColor(Color.WHITE).
				withName("Liquid of heaven").
				withEffects(new PotionEffect(PotionEffectType.LEVITATION, 10, 10)).
				build());
		wizardKit3.addItem(ItemUtils.potionBuilder().
				asDrinkable().
				withColor(Color.AQUA).
				withName(AQUA + "" + BOLD + "Power Potion").
				withEffects(
						new PotionEffect(PotionEffectType.REGENERATION, 160, 1),
						new PotionEffect(PotionEffectType.ABSORPTION, 160, 0),
						new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 160, 0),
						new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 160, 0),
						new PotionEffect(PotionEffectType.FAST_DIGGING, 160, 4),
						new PotionEffect(PotionEffectType.SPEED, 160, 1),
						new PotionEffect(PotionEffectType.GLOWING, 160, 0)).
				build());

		Kit manyDamage = new Kit("Много дамага");
		manyDamage.addArmorSet(Kit.ArmorMaterial.NETHERITE);
		manyDamage.addItem(Material.WOODEN_SWORD);
		ItemStack damagePotion = ItemUtils.potionBuilder().
				asSplash().
				withColor(Color.BLACK).
				withName("Damage").
				withEffects(new PotionEffect(PotionEffectType.HARM, 1, 0)).build();
		for(int i = 0; i < 8; i++) {
			manyDamage.addItem(damagePotion);
		}

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

	private List<Player> getNearArenaPlayers() {
		List<Player> nearestPlayers = new ArrayList<>();
		for(Player player : getLobbyPlayers()) {
			if(isOnArena(player) || spawnLocation.distance(player.getLocation()) <= 32) {
				nearestPlayers.add(player);
			}
		}
		return nearestPlayers;
	}

	public void initDuel(Player p1, Player p2) {
		currentKit = getRandomKit();
		duelingPlayer1 = p1;
		duelingPlayer2 = p2;
		p1.teleport(duelSpawn1);
		p2.teleport(duelSpawn2);
		heal(p1);
		heal(p2);
		isDuel = true;
		onArenaEnter(p1);
		onArenaEnter(p2);
		for(Player player : getNearArenaPlayers()) {
			player.sendTitle(" ", GOLD + p1.getName() + RED + BOLD + " VS " + RESET + GOLD + p2.getName(),
					5, 40, 10);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1F);
			heal(player);
		}
	}

	public void endDuel(Player winner, Player loser) {
		duelingPlayer1 = null;
		duelingPlayer2 = null;
		for(Player player : getNearArenaPlayers()) {
			player.sendTitle(" ", GOLD + winner.getName() + YELLOW + " замачил " + GOLD + loser.getName(), 5,
					60, 20);
		}
		int winnerHealth = (int) Math.round(winner.getHealth());
		InventoryHelper.sendActionBarMessage(loser, GRAY + "У противника осталось " + DARK_RED + BOLD + winnerHealth + GRAY + " ХП");
		heal(winner);
		Firework firework = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(2);
		meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.GREEN).withFlicker().build());
		firework.setFireworkMeta(meta);
		openArena();
		isDuel = false;
	}

	public Player getOpponent(Player p) {
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
				Player opponent = getOpponent(p);
				if(opponent != null) {
					endDuel(opponent, p);
				}
			} else {
				InventoryHelper.sendActionBarMessage(p, GRAY + "> " +
						GOLD + BOLD + "Ты вышел с арены" +
						RESET + GRAY + " <");
				p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5F, 0.8F);
			}
		}
	}

	/**
	 * Calls when a player enters an arena in any way
	 */
	public void onArenaEnter(Player player) {
		if(!isOnArena(player)) {
			if(!isOpen && !isDueling(player)) {
				player.teleport(spawnLocation);
				InventoryHelper.sendActionBarMessage(player, DARK_RED + "Подожди, арена закрыта");
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
			} else if(isDuel && !isDueling(player)) {
				player.teleport(spawnLocation);
				InventoryHelper.sendActionBarMessage(player, DARK_RED + "Сейчас идет дуэль");
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
			} else {
				currentKit.give(player);
				InventoryHelper.sendActionBarMessage(player, GRAY + "> " +
						RED + BOLD + "Ты зашел на арену" +
						RESET + GRAY + " <");
				player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 0.5F, 1F);
				onArena.add(player);
				Bukkit.getPluginManager().callEvent(new PvpArenaEnterEvent(player));
			}
		}
	}

	public void update() {
		if(TaskManager.isSecUpdated()) {
			List<Player> waiting = getDuelWaitingPlayers();
			for(Player p : waiting) {
				if(waiting.size() == 1) {
					InventoryHelper.sendActionBarMessage(p, GRAY + "Ожидаем второго игрока...");
				} else if(waiting.size() >= 2 && !onArena.isEmpty()) {
					InventoryHelper.sendActionBarMessage(p, GRAY + "Ожидаем окончания битвы на арене...");
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
		if(Lobby.getLobby().getPlayers().contains(player)) {
			Location to = e.getTo();
			if(isOnArena(player) && leaveRegion.isInside(to)) {
				onArenaLeave(player);
			}
			if(!isOnArena(player) && enterRegion.isInside(to)) {
				onArenaEnter(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void death(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if(Lobby.isInLobby(player) && isOnArena(player)) {
			onArenaLeave(player);
			killsToNextKit--;
			if(killsToNextKit <= 0) {
				killsToNextKit = 8;
				currentKit = getRandomKit();
				for(Player currentPlayer : onArena) {
					InventoryHelper.sendActionBarMessage(currentPlayer,
							GOLD + "В следующий раз будет выдан новый набор: " +
									LIGHT_PURPLE + currentKit.getName());
				}
			}
			Player killer = player.getKiller();
			if(killer != null && isOnArena(killer)) {
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
		}
	}

	@EventHandler
	public void leaveArenaOnGameStart(BeforeGameInitializeEvent event) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isOnArena(player)) {
				onArenaLeave(player);
			}
		}
		openIfClosed();
	}

	@EventHandler
	public void onPluginDisable(PluginDisableEvent event) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(isOnArena(player)) {
				player.teleport(getSpawnLocation());
				onArenaLeave(player);
			}
		}
		openIfClosed();
	}

	private void openIfClosed() {
		if(!isOpen()) {
			openArena();
		}
	}

}

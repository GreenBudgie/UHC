package ru.greenbudgie.mutator;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.event.SpectatorLeaveEvent;
import ru.greenbudgie.event.UHCPlayerLeaveEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.*;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class MutatorHealingZone extends Mutator implements Listener {


	private static final int REGION_SIZE = 6;

	private static final BarColor OUTSIDE_COLOR = BarColor.WHITE;
	private static final BarColor INSIDE_COLOR = BarColor.PINK;

	private static final int TICKS_PER_UPDATE = 4;
	private static final int HEAL_TIME = 60;
	private static final double TIMER_DECREASE_PER_UPDATE = 0.4;
	private static final double TIMER_INCREASE_PER_UPDATE = 0.2;

	private Region healingRegion;
	private Location center;

	private final Map<Player, BossBar> bossBars = new HashMap<>();
	private final Map<Player, Double> healingTimers = new HashMap<>();

	@Override
	public Material getItemToShow() {
		return Material.MANGROVE_PRESSURE_PLATE;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Зона Регенерации";
	}

	@Override
	public String getDescription() {
		return "В случайном месте на поверхности карты появляется зона, при нахождении в которой каждую минуту " +
				"будет восстанавливаться 1 сердце.";
	}

	@Override
	public void onChoose() {
		setupRandomLocation();
		updateBars();
	}

	@Override
	public void onDeactivate() {
		bossBars.values().forEach(bar -> {
			bar.setVisible(false);
			bar.removeAll();
		});
		bossBars.clear();
		healingTimers.clear();
	}

	@Override
	public void update() {
		if (!TaskManager.ticksPassed(TICKS_PER_UPDATE)) {
			return;
		}
		updateBars();
		double randomDensity = MathUtils.randomRangeDouble(0.2, 0.6);
		ParticleUtils.createParticlesOnRegionEdges(healingRegion, Particle.HEART, randomDensity, null);
		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			if (isInsideHealingRegion(player)) {
				updatePlayerInsideRegion(player);
			} else {
				updatePlayerOutsideRegion(player);
			}
		}
	}


	@Override
	public boolean containsBossBar() {
		return true;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	private void updatePlayerInsideRegion(Player player) {
		double timer = getHealingTimer(player);
		if (timer >= HEAL_TIME) {
			healPlayerAndReset(player);
			return;
		}
		increaseHealingTimer(player);
	}

	private void updatePlayerOutsideRegion(Player player) {
		double timer = getHealingTimer(player);
		if (timer >= 0) {
			decreaseHealingTimer(player);
		}
	}

	private void healPlayerAndReset(Player player) {
		player.addPotionEffect(
				new PotionEffectBuilder(PotionEffectType.REGENERATION).seconds(6).build()
		);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
		ParticleUtils.createParticlesAround(player, Particle.HEART, null, 10);
		healingTimers.put(player, 0D);
	}

	private void setupRandomLocation() {
		double halfSize = REGION_SIZE / 2.0;
		int size = ((int) WorldManager.getActualMapSize()) / 2 - 10;
		int x = MathUtils.randomRange(
				WorldManager.spawnLocation.getBlockX() - size,
				WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(
				WorldManager.spawnLocation.getBlockZ() - size,
				WorldManager.spawnLocation.getBlockZ() + size);
		int y = WorldManager.getGameMap().getHighestBlockYAt(x, z) + (int) Math.floor(halfSize);
		center = new Location(WorldManager.getGameMap(), x, y, z);
		healingRegion = new Region(
				center.clone().subtract(halfSize, halfSize, halfSize),
				center.clone().add(halfSize, halfSize, halfSize)
		);
	}

	private void updateBars() {
		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			BossBar bar = bossBars.computeIfAbsent(player, this::createDefaultBossBar);
			updateBarForPlayer(player, bar);
		}
		for (Player spectator : PlayerManager.getSpectators()) {
			BossBar bar = bossBars.computeIfAbsent(spectator, this::createDefaultBossBar);
			updateBarForSpectator(spectator, bar);
		}
	}

	private void updateBarForPlayer(Player player, BossBar bar) {
		if (isInsideHealingRegion(player)) {
			bar.setColor(INSIDE_COLOR);
		} else {
			bar.setColor(OUTSIDE_COLOR);
		}
		double timer = getHealingTimer(player);
		bar.setProgress(MathUtils.clamp(timer / HEAL_TIME, 0, 1));
		setBarName(player, bar);
	}

	private void updateBarForSpectator(Player spectator, BossBar bar) {
		bar.setColor(OUTSIDE_COLOR);
		bar.setProgress(0);
		setBarName(spectator, bar);
	}

	private void setBarName(Player player, BossBar bar) {
		String locationInfo = LocationFormatter.formatToWithDistanceAndArrow(
				player.getLocation(),
				center,
				DARK_AQUA,
				WHITE,
				AQUA,
				DARK_GRAY,
				AQUA,
				false
		);
		bar.setTitle(LIGHT_PURPLE + "Зона регенерации" + GRAY + ": " + locationInfo);
	}

	private void increaseHealingTimer(Player player) {
		healingTimers.merge(player, TIMER_INCREASE_PER_UPDATE, Double::sum);
	}

	private void decreaseHealingTimer(Player player) {
		if (healingTimers.containsKey(player)) {
			double newValue = Math.max(0, healingTimers.get(player) - TIMER_DECREASE_PER_UPDATE);
			healingTimers.put(player, newValue);
		}
	}

	private double getHealingTimer(Player player) {
		return healingTimers.getOrDefault(player, 0.0);
	}

	private boolean isInsideHealingRegion(Player player) {
		return healingRegion.isInside(player);
	}

	private BossBar createDefaultBossBar(Player player) {
		BossBar bar = Bukkit.createBossBar("", OUTSIDE_COLOR, BarStyle.SOLID);
		bar.setVisible(true);
		bar.addPlayer(player);
		return bar;
	}

	@EventHandler
	public void playerLeave(UHCPlayerLeaveEvent event) {
		removeBossBar(event.getUHCPlayer().getPlayer());
	}

	@EventHandler
	public void spectatorLeave(SpectatorLeaveEvent event) {
		removeBossBar(event.getPlayer());
	}

	private void removeBossBar(Player player) {
		if (bossBars.containsKey(player)) {
			BossBar bar = bossBars.get(player);
			bar.removeAll();
			bar.setVisible(false);
			bossBars.remove(player);
		}
	}

}

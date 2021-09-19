package ru.mutator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.UHC.*;
import ru.event.UHCPlayerDeathEvent;
import ru.event.UHCPlayerLeaveEvent;
import ru.event.UHCPlayerRejoinEvent;
import ru.main.UHCPlugin;
import ru.util.MathUtils;
import ru.util.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class MutatorIncreasingDamage extends Mutator implements Listener {

	private final double MAX_DAMAGE_INCREASE = 2;
	private final double INCREASE_PER_SECOND = 1 / 1200D;

	private final Map<UHCPlayer, Double> damageIncrease = new HashMap<>();
	private final Map<Player, BossBar> bars = new HashMap<>();

	@Override
	public Material getItemToShow() {
		return Material.BLAZE_POWDER;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Зависимость от Урона";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return super.conflictsWith(another) || another == MutatorManager.doubleDamage;
	}

	@Override
	public String getDescription() {
		return "Чем дольше ты не получаешь урон, тем больше урона пройдет по тебе в следующий раз. Получение любого урона сбрасывает шкалу к нулю.";
	}

	@Override
	public boolean containsBossBar() {
		return true;
	}

	@Override
	public void onChoose() {
		for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
			damageIncrease.put(uhcPlayer, 0D);
			if(uhcPlayer.isOnline()) {
				createBar(uhcPlayer.getPlayer());
			}
		}
	}

	@EventHandler
	public void handleRejoin(UHCPlayerRejoinEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		if(uhcPlayer.isAlive()) {
			createBar(uhcPlayer.getPlayer());
		}
	}

	@EventHandler
	public void handleLeave(UHCPlayerLeaveEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		unregisterBarFor(uhcPlayer.getPlayer());
	}

	@EventHandler
	public void handleDeath(UHCPlayerDeathEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		if(uhcPlayer.getPlayer() != null) {
			unregisterBarFor(uhcPlayer.getPlayer());
		}
	}

	@Override
	public void onDeactivate() {
		for(Player player : bars.keySet()) {
			BossBar bar = bars.get(player);
			bar.setVisible(false);
			bar.removeAll();
		}
		bars.clear();
		damageIncrease.clear();
	}

	/**
	 * Raw damage increase is always a number between 0 (no damage increase) and 1 (max damage increase)
	 */
	public double getRawDamageIncrease(Player player) {
		UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
		if(uhcPlayer == null) return 0;
		return damageIncrease.getOrDefault(uhcPlayer, 0D);
	}

	/**
	 * Real damage increase is a number between 1 and MAX_DAMAGE_INCREASE
	 */
	public double getDamageIncreaseMultiplier(Player player) {
		return (getRawDamageIncrease(player) * (MAX_DAMAGE_INCREASE - 1D)) + 1D;
	}

	public BossBar getBar(Player player) {
		return bars.get(player);
	}

	public void createBar(Player player) {
		BossBar bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		bar.addPlayer(player);
		bar.setVisible(true);
		bars.put(player, bar);
		updateBar(player);
	}

	public String getBarTitle(Player player) {
		int damageIncreasePercent = (int) Math.round(getRawDamageIncrease(player) * MAX_DAMAGE_INCREASE * 100);
		return ChatColor.RED + "Получаемый урон " +
				ChatColor.GRAY + "+" +
				ChatColor.DARK_RED + "" + ChatColor.BOLD + damageIncreasePercent +
				ChatColor.GRAY + "%";
	}

	public void updateBar(Player player) {
		BossBar bar = getBar(player);
		if(bar != null) {
			bar.setTitle(getBarTitle(player));
			bar.setProgress(getRawDamageIncrease(player));
		}
	}

	public void unregisterBarFor(Player player) {
		BossBar bar = getBar(player);
		if(bar != null) {
			bar.setVisible(false);
			bar.removeAll();
			bars.remove(player);
		}
	}

	@Override
	public void update() {
		if(TaskManager.isSecUpdated()) {
			for(UHCPlayer uhcPlayer : damageIncrease.keySet()) {
				double currentValue = damageIncrease.get(uhcPlayer);
				double increasedValue = Math.min(currentValue + INCREASE_PER_SECOND, 1);
				damageIncrease.put(uhcPlayer, increasedValue);
				if(uhcPlayer.isOnline()) {
					updateBar(uhcPlayer.getPlayer());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void takeDamage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getFinalDamage() > 0 && event.getEntity() instanceof Player player) {
			if(PlayerManager.isPlaying(player)) {
				double multiplier = getDamageIncreaseMultiplier(player);
				event.setDamage(event.getDamage() * multiplier);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void monitorDamage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getEntity() instanceof Player player && PlayerManager.isPlaying(player) && event.getFinalDamage() > 0) {
			UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
			if(uhcPlayer != null) {
				damageIncrease.put(uhcPlayer, 0D);
			}
		}
	}

}

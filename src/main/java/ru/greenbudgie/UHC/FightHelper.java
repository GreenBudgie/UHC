package ru.greenbudgie.UHC;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.projectiles.ProjectileSource;
import ru.greenbudgie.event.AfterGameEndEvent;
import ru.greenbudgie.event.GameStartEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FightHelper implements Listener {

	private static final List<FightProcess> processes = new ArrayList<>();
	private static final Map<Location, UHCPlayer> spilledLava = new HashMap<>();
	private static final Map<Location, UHCPlayer> burningFire = new HashMap<>();

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		reset();
	}

	@EventHandler
	public void onGameEnd(AfterGameEndEvent event) {
		reset();
	}

	private static void reset() {
		processes.clear();
		spilledLava.clear();
		burningFire.clear();
	}

	/**
	 * Gets the custom player's killer
	 *
	 * @param victim The Player
	 * @return Player's killer
	 */
	public static UHCPlayer getKiller(Player victim) {
		if(victim == null) return null;
		UHCPlayer uhcDamager = getCustomDamager(victim);
		Player defaultKiller = victim.getKiller();
		UHCPlayer uhcDefaultKiller = null;
		if(defaultKiller != null) uhcDefaultKiller = PlayerManager.asUHCPlayer(defaultKiller);
		return uhcDefaultKiller == null ? uhcDamager : uhcDefaultKiller;
	}

	/**
	 * Gets the custom player's last damager
	 *
	 * @param victim The Player
	 * @return Player's damager or null
	 */
	public static UHCPlayer getCustomDamager(Player victim) {
		FightProcess process = getProcess(victim);
		return process == null ? null : process.attacker;
	}

	/**
	 * Sets last damager to a player withing a certain amount of ticks to remove the information about the damager
	 *
	 * @param victim        The player
	 * @param damager       A damager
	 * @param ticksToRemove Amount of ticks to remove the information about the damager
	 */
	public static void setDamager(Player victim, Player damager, int ticksToRemove) {
		if(victim == damager) return;
		UHCPlayer uhcDamager = PlayerManager.asUHCPlayer(damager);
		processes.add(new FightProcess(victim, uhcDamager, ticksToRemove));
	}

	/**
	 * Sets last damager to a player withing a certain amount of ticks to remove the information about the damager
	 *
	 * @param victim        The player
	 * @param damager       A damager
	 * @param ticksToRemove Amount of ticks to remove the information about the damager
	 * @param killMessage   A message to show if a player has been killed
	 */
	public static void setDamager(Player victim, Player damager, int ticksToRemove, String killMessage) {
		if(victim == damager) return;
		UHCPlayer uhcDamager = PlayerManager.asUHCPlayer(damager);
		processes.add(new FightProcess(victim, uhcDamager, ticksToRemove, killMessage));
	}

	public static void setDamager(Player victim, UHCPlayer damager, int ticksToRemove) {
		if(victim == damager.getPlayer()) return;
		processes.add(new FightProcess(victim, damager, ticksToRemove));
	}

	public static void setDamager(Player victim, UHCPlayer damager, int ticksToRemove, String killMessage) {
		if(victim == damager.getPlayer()) return;
		processes.add(new FightProcess(victim, damager, ticksToRemove, killMessage));
	}

	public static void update() {
		processes.removeIf(process -> process.ticks <= 0);
		for(FightProcess process : processes) {
			process.ticks--;
		}

		spilledLava.entrySet().removeIf(entry ->
				entry.getKey().getBlock().getType() != Material.LAVA
		);

		burningFire.entrySet().removeIf(entry ->
				entry.getKey().getBlock().getType() != Material.FIRE
		);
	}

	private static FightProcess getProcess(Player victim) {
		for(FightProcess process : processes) {
			if(process.victim == victim) {
				return process;
			}
		}
		return null;
	}

	public static String padCrosses(String deathMessage) {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "\u274C " + ChatColor.RESET + deathMessage + ChatColor.DARK_RED + "" + ChatColor.BOLD + " \u274C";
	}

	public static LivingEntity getDamagerOrShooter(EntityDamageByEntityEvent event) {
		Entity rawDamager = event.getDamager();
		if(rawDamager instanceof Projectile projectile) {
			ProjectileSource shooter = projectile.getShooter();
			if(shooter instanceof LivingEntity damager) return damager;
		}
		if(rawDamager instanceof LivingEntity damager) return damager;
		return null;
	}

	public static String getDeathMessage(Player victim) {
		FightProcess process = getProcess(victim);
		Player killer = victim.getKiller();
		String deathMessage;
		if(process == null) {
			if(killer == null) {
				deathMessage = ChatColor.GOLD + victim.getName() + ChatColor.RED + " замачили";
			} else {
				deathMessage = ChatColor.GOLD + victim.getKiller().getName() + ChatColor.RED + " замачил " + ChatColor.GOLD + victim.getName();
			}
		} else {
			if(process.killMessage.isEmpty()) {
				deathMessage = ChatColor.GOLD + process.attacker.getNickname() + ChatColor.RED + " замачил " + ChatColor.GOLD + victim.getName();
			} else {
				deathMessage = ChatColor.GOLD + process.attacker.getNickname() + ChatColor.RED + " " + process.killMessage + " " + ChatColor.GOLD + victim.getName();
			}
		}
		return padCrosses(deathMessage);
	}

	@EventHandler
	public void lavaSpill(PlayerBucketEmptyEvent event) {
		if (!UHC.playing) {
			return;
		}

		var player = PlayerManager.asUHCPlayer(event.getPlayer());
		if (player == null) {
			return;
		}

		if (event.getBucket() != Material.LAVA_BUCKET) {
			return;
		}

		spilledLava.put(event.getBlock().getLocation(), player);
	}

	@EventHandler
	public void ignite(BlockIgniteEvent event) {
		if (!UHC.playing) {
			return;
		}

		var player = PlayerManager.asUHCPlayer(event.getPlayer());
		if (player == null) {
			return;
		}

		var cause = event.getCause();
		if (cause == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL || cause == BlockIgniteEvent.IgniteCause.FIREBALL) {
			burningFire.put(event.getBlock().getLocation(), player);
		}
	}

	@EventHandler
	public void combustByBlock(EntityCombustByBlockEvent event) {
		if (!UHC.playing) {
			return;
		}

		if (!(event.getEntity() instanceof Player victim)) {
			return;
		}

		var combuster = event.getCombuster();
		if (combuster == null) {
			return;
		}

		var location = combuster.getLocation();
		UHCPlayer attacker;
		String killMessage;
		if (spilledLava.containsKey(location)) {
			attacker = spilledLava.get(location);
			killMessage = "утопил в лаве";
		} else if (burningFire.containsKey(location)) {
			attacker = burningFire.get(location);
			killMessage = "сжёг";
		} else {
			return;
		}

		setDamager(victim, attacker, (int)(event.getDuration() * 20 + 20), killMessage);
	}

	private static class FightProcess {

		private final Player victim;
		private final UHCPlayer attacker;
		private int ticks;
		private final String killMessage;

		public FightProcess(Player victim, UHCPlayer attacker, int ticks) {
			this(victim, attacker, ticks, "");
		}

		public FightProcess(Player victim, UHCPlayer attacker, int ticks, String killMessage) {
			this.victim = victim;
			this.attacker = attacker;
			this.ticks = ticks;
			this.killMessage = killMessage;
		}

	}

}

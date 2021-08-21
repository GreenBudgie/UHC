package ru.UHC;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FightHelper {

	public static List<FightProcess> processes = new ArrayList<>();

	/**
	 * Gets the custom player's killer
	 *
	 * @param victim The Player
	 * @return Player's killer
	 */
	public static UHCPlayer getKiller(Player victim) {
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
		UHCPlayer uhcDamager = PlayerManager.asUHCPlayer(damager);
		processes.add(new FightProcess(victim, uhcDamager, ticksToRemove, killMessage));
	}

	public static void setDamager(Player victim, UHCPlayer damager, int ticksToRemove) {
		processes.add(new FightProcess(victim, damager, ticksToRemove));
	}

	public static void setDamager(Player victim, UHCPlayer damager, int ticksToRemove, String killMessage) {
		processes.add(new FightProcess(victim, damager, ticksToRemove, killMessage));
	}

	public static void update() {
		processes.removeIf(process -> process.ticks <= 0);
		for(FightProcess process : processes) {
			process.ticks--;
		}
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

	private static class FightProcess {

		private Player victim;
		private UHCPlayer attacker;
		private int ticks;
		private String killMessage = "";

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

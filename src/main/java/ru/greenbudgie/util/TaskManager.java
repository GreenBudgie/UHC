package ru.greenbudgie.util;

import org.bukkit.Bukkit;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.LobbyGameManager;
import ru.greenbudgie.main.UHCPlugin;

public class TaskManager {

	public static int tick = 0;
	public static long fullTicks = 0;
	public static int sec = 0;
	public static long fullSeconds = 0;
	public static int min = 0;
	public static long fullMinutes = 0;

	public static void init() {
		UHCPlugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(UHCPlugin.instance, () -> {
			UHC.tickGame();
			LobbyGameManager.updateGames();
			if(tick < 19) {
				tick++;
				fullTicks++;
			} else {
				tick = 0;
				if(sec < 59) {
					sec++;
					fullSeconds++;
				} else {
					sec = 0;
					if(min < 59) {
						min++;
						fullMinutes++;
					} else {
						min = 0;
					}
				}
			}
		}, 0L, 1L);
	}

	public static boolean ticksPassed(int ticks) {
		return fullTicks % ticks == 0;
	}

	public static boolean secondsPassed(int seconds) {
		return isSecUpdated() && fullSeconds % seconds == 0;
	}

	public static boolean minutesPassed(int minutes) {
		return isMinUpdated() && fullMinutes % minutes == 0;
	}

	public static void invokeLater(Runnable task) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(UHCPlugin.instance, task);
	}

	public static void invokeLater(Runnable task, long delay) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(UHCPlugin.instance, task, delay);
	}

	@SuppressWarnings("deprecation")
	public static void asyncInvokeLater(Runnable task, long delay) {
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(UHCPlugin.instance, task, delay);
	}

	public static boolean isSecUpdated() {
		return tick == 0;
	}

	public static boolean isMinUpdated() {
		return sec == 0 && isSecUpdated();
	}

	public static int getFullTicks() {
		return tick + (sec * 20) + (min * 1200);
	}

}

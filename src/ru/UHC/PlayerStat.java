package ru.UHC;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.main.UHCPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum PlayerStat {

	POINTS("Очки"), WINS("Победы"), KILLS("Убийства"), GAMES("Игр сыграно"), DUEL_COUNT("Дуэлей сыграно"), DUEL_WINS("Дуэлей выиграно"), ARENA_KILLS(
			"Убийства на Арене"), ARENA_DEATHS("Смерти на Арене");

	private static File statsFile = new File(UHCPlugin.instance.getDataFolder() + File.separator + "stats.yml");
	private static YamlConfiguration stats = YamlConfiguration.loadConfiguration(statsFile);
	private String name;

	PlayerStat(String name) {
		this.name = name;
	}

	public static YamlConfiguration getStatsConfig() {
		return stats;
	}

	public static List<String> getRegisteredPlayerNames() {
		return Lists.newArrayList(stats.getKeys(false));
	}

	public static double ratio(String p, PlayerStat stat1, PlayerStat stat2) {
		double val1 = (double) stat1.getValue(p);
		double val2 = (double) stat2.getValue(p);
		if(val2 == 0) return 0;
		return val1 / val2;
	}

	public static void defaultStats(Player p) {
		defaultStats(p.getName());
	}

	public static void defaultStats(String s) {
		if(!stats.contains(s)) {
			for(PlayerStat stat : values()) {
				stat.setValue(s, 0);
			}
		}
	}

	public static void deleteStats(Player p) {
		deleteStats(p.getName());
	}

	public static void deleteStats(String s) {
		if(stats.contains(s)) {
			stats.set(s, null);
		}
	}

	public static void zeroStats(Player p) {
		deleteStats(p.getName());
	}

	public static void zeroStats(String s) {
		if(stats.contains(s)) {
			for(PlayerStat stat : values()) {
				stat.setValue(s, 0);
			}
		}
	}

	public static Map<PlayerStat, Integer> getStats(Player p) {
		return getStats(p.getName());
	}

	public static Map<PlayerStat, Integer> getStats(String s) {
		Map<PlayerStat, Integer> map = new HashMap<>();
		for(PlayerStat stat : values()) {
			map.put(stat, stat.getValue(s));
		}
		return map;
	}

	public static void save() {
		try {
			stats.save(statsFile);
		} catch(IOException e) {
		}
	}

	public void increaseValue(Player p, int value) {
		increaseValue(p.getName(), value);
	}

	public void increaseValue(String playerName, int value) {
		this.setValue(playerName, this.getValue(playerName) + value);
	}

	public int getValue(String name) {
		return stats.getInt(name + "." + this.name());
	}

	public void setValue(String name, int value) {
		stats.set(name + "." + this.name(), value);
		save();
	}

	public int getValue(Player p) {
		return getValue(p.getName());
	}

	public void setValue(Player p, int value) {
		setValue(p.getName(), value);
	}

	public String getName() {
		return name;
	}

}

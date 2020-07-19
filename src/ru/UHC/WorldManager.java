package ru.UHC;

import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import ru.util.TaskManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class WorldManager {

	public static boolean keepMap = false;
	public static Location spawnLocation;
	private static World lobby, gameMap, arena, tempArena, arena2;

	public static void init() {
		lobby = Bukkit.createWorld(new WorldCreator("Lobby"));
		lobby.setDifficulty(Difficulty.NORMAL);
		lobby.setPVP(true);
		lobby.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		lobby.setGameRule(GameRule.NATURAL_REGENERATION, false);
		arena = Bukkit.createWorld(new WorldCreator("Arena"));
		arena.setDifficulty(Difficulty.HARD);
		arena.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		arena.setGameRule(GameRule.NATURAL_REGENERATION, false);
		arena.setGameRule(GameRule.DO_FIRE_TICK, false);
		arena.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		arena.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		arena.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		arena.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		arena.setPVP(false);
		arena.setTime(6000);
		arena2 = Bukkit.createWorld(new WorldCreator("Arena2"));
		arena2.setDifficulty(Difficulty.HARD);
		arena2.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		arena2.setGameRule(GameRule.NATURAL_REGENERATION, false);
		arena2.setGameRule(GameRule.DO_FIRE_TICK, false);
		arena2.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		arena2.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		arena2.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		arena2.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		arena2.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		arena2.setPVP(false);
		arena2.setTime(6000);
		if(Bukkit.getWorld("CurrentMap") != null) {
			gameMap = Bukkit.createWorld(new WorldCreator("CurrentMap"));
			gameMap.setDifficulty(Difficulty.HARD);
			spawnLocation = gameMap.getSpawnLocation().clone();
		}
		if(Bukkit.getWorld("ArenaTemp") != null) {
			tempArena = Bukkit.createWorld(new WorldCreator("ArenaTemp"));
		}
	}

	public static World createMap() {
		Bukkit.broadcastMessage(ChatColor.GOLD + "Начинается генерация мира");
		UHC.generating = true;
		World map = Bukkit.createWorld(new WorldCreator("CurrentMap"));
		map.setDifficulty(Difficulty.HARD);
		map.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		map.setGameRule(GameRule.NATURAL_REGENERATION, false);
		map.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		map.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		map.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		map.setPVP(false);
		spawnLocation = map.getSpawnLocation().clone();
		gameMap = map;
		Bukkit.broadcastMessage(ChatColor.GOLD + "Копирование арены...");
		tempArena = copyAsTemp(arena);
		Bukkit.broadcastMessage(ChatColor.GREEN + "Новый мир создан!");
		TaskManager.asyncInvokeLater(() -> UHC.generating = false, 20);
		return map;
	}

	public static void updateBorder() {
		WorldBorder border = gameMap.getWorldBorder();
		if(UHC.mapSize != 3) {
			border.setSize(UHC.getMapSize() * Bukkit.getOnlinePlayers().size());
		} else {
			border.setSize(UHC.getMapSize());
		}
		border.setWarningTime(0);
		border.setWarningDistance(0);
		border.setDamageBuffer(0);
		border.setDamageAmount(0);
		border.setCenter(spawnLocation);
	}

	public static void regenMap() {
		removeMap();
		createMap();
		SignManager.updateSigns();
	}

	public static boolean deleteWorld(File path) {
		if(path.exists()) {
			File files[] = path.listFiles();
			for(int i = 0; i < files.length; i++) {
				if(files[i].isDirectory()) {
					deleteWorld(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return path.delete();
	}

	public static void removeMap() {
		if(hasMap()) {
			Bukkit.unloadWorld(gameMap, false);
			WorldManager.deleteWorld(gameMap.getWorldFolder());
			gameMap = null;
			Bukkit.unloadWorld(tempArena, false);
			WorldManager.deleteTempWorld(tempArena);
			tempArena = null;
		}
	}

	public static World copyAsTemp(World world) {
		File source = world.getWorldFolder();
		String path = source.getAbsolutePath();
		path = path.substring(0, path.length() - 2);
		File target = new File(path + "Temp\\.");
		copyWorld(source, target);
		try {
			new File(target.getAbsolutePath() + "temp.info").createNewFile();
		} catch(IOException e) {
		}
		return Bukkit.createWorld(new WorldCreator(world.getName() + "Temp"));
	}

	private static void copyWorld(File source, File target) {
		try {
			ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
			if(!ignore.contains(source.getName())) {
				if(source.isDirectory()) {
					if(!target.exists()) target.mkdirs();
					String files[] = source.list();
					for(String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorld(srcFile, destFile);
					}
				} else {
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch(IOException e) {
		}
	}

	public static boolean deleteTempWorld(World world) {
		if(!(new File(world.getWorldFolder().getAbsolutePath() + "temp.info").exists())) {
			return false;
		}
		return deleteWorld(world.getWorldFolder());
	}

	public static boolean hasMap() {
		return gameMap != null && tempArena != null;
	}

	public static World getLobby() {
		return lobby;
	}

	public static World getGameMap() {
		return gameMap;
	}

	public static World getMainArena() {
		return arena;
	}

	public static World getSecondArena() {
		return arena2;
	}

	public static World getArena() {
		return tempArena;
	}

}

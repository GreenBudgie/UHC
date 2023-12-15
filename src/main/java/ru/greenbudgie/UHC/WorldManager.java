package ru.greenbudgie.UHC;

import org.bukkit.*;
import ru.greenbudgie.lobby.LobbyMapPreview;
import ru.greenbudgie.lobby.sign.SignManager;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.TaskManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.bukkit.ChatColor.*;

public class WorldManager {

	public static boolean keepMap = true;
	public static Location spawnLocation;
	private static World gameMap, gameMapNether;

	public static void init() {
		ArenaManager.init();

		if(Bukkit.getWorld("CurrentMap") != null) {
			gameMap = Bukkit.createWorld(new WorldCreator("CurrentMap"));
			gameMap.setDifficulty(Difficulty.HARD);
			spawnLocation = gameMap.getSpawnLocation().clone();
			LobbyMapPreview.setPreview();
		} else {
			generateAndSetGameMap();
		}

		if(Bukkit.getWorld("CurrentMapNether") != null) {
			gameMapNether = Bukkit.createWorld(new WorldCreator("CurrentMapNether"));
			gameMapNether.setDifficulty(Difficulty.HARD);
		} else {
			generateAndSetGameMapNether();
		}
	}

	public static void createMap() {
		String arrows = DARK_GRAY + "" + BOLD + ">>>";
		Bukkit.broadcastMessage(arrows + DARK_AQUA + "" + BOLD + " Начинается генерация мира! " + RESET + GOLD +
				"Сервер может зависнуть на некоторое время. Это нормально.");
		UHC.generating = true;
		generateAndSetGameMap();
		Bukkit.broadcastMessage(arrows + DARK_RED + BOLD + " Генерация ада");
		generateAndSetGameMapNether();
		Bukkit.broadcastMessage(arrows + GRAY + BOLD + " Копирование арены");
		ArenaManager.setupCurrentArena();
		Bukkit.broadcastMessage(arrows + DARK_GREEN + BOLD + " Новый мир создан!");
		TaskManager.asyncInvokeLater(() -> UHC.generating = false, 20);
	}

	private static void generateAndSetGameMap() {
		World map = Bukkit.createWorld(new WorldCreator("CurrentMap"));
		setRules(map);
		spawnLocation = map.getSpawnLocation().clone();
		gameMap = map;
		LobbyMapPreview.setPreview();
	}

	private static void generateAndSetGameMapNether() {
		World nether = Bukkit.createWorld(
				new WorldCreator("CurrentMapNether").environment(World.Environment.NETHER)
		);
		setRules(nether);
		gameMapNether = nether;
	}

	private static void setRules(World map) {
		map.setDifficulty(Difficulty.HARD);
		map.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		map.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		map.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
		map.setGameRule(GameRule.NATURAL_REGENERATION, false);
		map.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		map.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		map.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		map.setGameRule(GameRule.DO_INSOMNIA, false);
		map.setPVP(false);
	}

	public static void updateBorder() {
		WorldBorder border = gameMap.getWorldBorder();
		border.setSize(UHC.mapSize.getWorldBorderSideSize(Bukkit.getOnlinePlayers().size()));
		border.setWarningTime(0);
		border.setWarningDistance(0);
		border.setDamageBuffer(0);
		border.setDamageAmount(0);
		border.setCenter(spawnLocation);

		WorldBorder netherBorder = gameMapNether.getWorldBorder();
		netherBorder.setSize(gameMap.getWorldBorder().getSize() * 3);
		netherBorder.setWarningTime(0);
		netherBorder.setWarningDistance(0);
		netherBorder.setDamageBuffer(0);
		netherBorder.setDamageAmount(0);
		netherBorder.setCenter(new Location(gameMapNether, spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ()));
	}

	public static void regenMap() {
		removeMap();
		createMap();
		SignManager.updateTextOnSigns();
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

	public static double getActualMapSize() {
		if(gameMap != null)
			return gameMap.getWorldBorder().getSize();
		return -1;
	}

	public static void removeMap() {
		if(gameMap != null) {
			Bukkit.unloadWorld(gameMap, false);
			WorldManager.deleteWorld(gameMap.getWorldFolder());
			gameMap = null;
		}
		if(gameMapNether != null) {
			Bukkit.unloadWorld(gameMapNether, false);
			WorldManager.deleteWorld(gameMapNether.getWorldFolder());
			gameMapNether = null;
		}
		if(ArenaManager.getCurrentArena() != null) {
			ArenaManager.removeCurrentArena();
		}
	}

	public static World copyAsTemp(World world) {
		try {
			File source = world.getWorldFolder();
			File target = new File(source.getCanonicalPath() + "Temp");
			copyWorld(source, target);
			new File(target.getCanonicalPath() + File.separator + "temp.info").createNewFile();
		} catch(IOException e) {
			UHCPlugin.error("Cannot copy \"" + world.getName() + "\" as temp");
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
		try {
			if(!(new File(world.getWorldFolder().getCanonicalPath() + File.separator + "temp.info").exists())) {
				UHCPlugin.warning("Cannot delete \"" + world.getName() + "\": not a temp world");
				return false;
			}
			return deleteWorld(world.getWorldFolder());
		} catch(IOException e) {
			return false;
		}
	}

	public static boolean hasMap() {
		return gameMap != null && gameMapNether != null && ArenaManager.getCurrentArena() != null;
	}

	public static World getGameMap() {
		return gameMap;
	}

	public static World getGameMapNether() {
		return gameMapNether;
	}

}

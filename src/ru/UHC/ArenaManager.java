package ru.UHC;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.main.UHCPlugin;
import ru.util.MathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {

    private static Arena chosenArena = null;
    private static boolean announceArena = true;
    private static Arena currentArena;
    private static List<Arena> arenas = new ArrayList<>();
    
    public static void init() {
        Map<String, Object> defaultParameters = new HashMap<>();
        defaultParameters.put("name", "Unknown arena");
        defaultParameters.put("minBorderSize", 16);
        defaultParameters.put("maxBorderSize", 64);
        for(File file : Bukkit.getWorldContainer().listFiles()) {
            if(file.getName().startsWith("Arena")) {
                World arenaWorld = Bukkit.createWorld(new WorldCreator(file.getName()));
                if(arenaWorld == null) {
                    UHCPlugin.warning("\"" + file.getName() + "\" has incorrect format");
                    continue;
                }
                File configFile = new File(file.getAbsolutePath() + File.separator + "arena.yml");
                if(!configFile.exists()) {
                    UHCPlugin.warning("\"" + file.getName() + "\" has no config file; creating default");
                    try {
                        configFile.createNewFile();
                    } catch(Exception e) {
                        UHCPlugin.error("Unable to create default config for \"" + file.getName() + "\"");
                        continue;
                    }
                }
                setupArenaWorld(arenaWorld);
                YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(configFile);
                boolean toUpdate = false;
                String name = "";
                int minBorderSize = 0;
                int maxBorderSize = 0;
                for(String parameterName : defaultParameters.keySet()) {
                    Object parameter = arenaConfig.get(parameterName);
                    if(parameter == null) {
                        UHCPlugin.warning("\"" + file.getName() + "\" config has no parameter " + parameterName);
                        parameter = defaultParameters.get(parameterName);
                        arenaConfig.set(parameterName, parameter);
                        toUpdate = true;
                    }
                    switch(parameterName) {
                        case "name" -> name = (String) parameter;
                        case "minBorderSize" -> minBorderSize = (int) parameter;
                        case "maxBorderSize" -> maxBorderSize = (int) parameter;
                    }
                }
                Arena arena;
                try {
                    arena = new Arena(arenaWorld, name, minBorderSize, maxBorderSize);
                } catch(Exception e) {
                    UHCPlugin.error("Unable to setup \"" + file.getName() + "\"");
                    e.printStackTrace();
                    continue;
                }
                if(toUpdate) {
                    try {
                        arenaConfig.save(configFile);
                    } catch(Exception e) {
                        UHCPlugin.error("Unable to save config of \"" + file.getName() + "\"");
                    }
                }
                if(file.getName().endsWith("Temp")) {
                    currentArena = arena;
                } else {
                    arenas.add(arena);
                }
            }
        }
    }
    
    private static void setupArenaWorld(World world) {
        world.setDifficulty(Difficulty.HARD);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setPVP(false);
    }

    public static void removeCurrentArena() {
        if(currentArena != null) {
            Bukkit.unloadWorld(currentArena.world(), false);
            WorldManager.deleteTempWorld(currentArena.world());
            currentArena = null;
        }
    }

    public static void setupCurrentArena() {
        if(chosenArena == null) {
            currentArena = MathUtils.choose(arenas).cloneAsTemp();
        } else {
            currentArena = chosenArena.cloneAsTemp();
        }
    }

    public static List<Arena> getArenas() {
        return arenas;
    }

    /**
     * Gets the arena that players chose in the lobby
     */
    public static Arena getChosenArena() {
        return chosenArena;
    }

    /**
     * Gets the arena that current game's deathmatch will go on
     */
    public static Arena getCurrentArena() {
        return currentArena;
    }

    public record Arena(World world, String name, int maxBorderSize, int minBorderSize) {

        public Arena cloneAsTemp() {
            World tempWorld = WorldManager.copyAsTemp(world);
            return new Arena(tempWorld, name, maxBorderSize, minBorderSize);
        }

    }

}

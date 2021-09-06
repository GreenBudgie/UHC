package ru.UHC;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.main.UHCPlugin;
import ru.util.MathUtils;

import javax.annotation.Nullable;
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

    private static boolean needsUpdate = false;
    
    public static void init() {
        Map<String, Object> defaultParameters = new HashMap<>();
        defaultParameters.put("name", "Unknown arena");
        defaultParameters.put("minBorderSize", 10);
        defaultParameters.put("maxBorderSize", 24);
        defaultParameters.put("isOpen", true);
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
                boolean isOpen = true;
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
                        case "isOpen" -> isOpen = (boolean) parameter;
                    }
                }
                Arena arena;
                try {
                    arena = new Arena(arenaWorld, name, maxBorderSize, minBorderSize, isOpen);
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
                resetArenaBorder(arena);
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

    public static boolean doAnnounceArena() {
        return announceArena;
    }

    public static void setAnnounceArena(boolean announce) {
        announceArena = announce;
    }

    public static void removeCurrentArena() {
        if(currentArena != null) {
            Bukkit.unloadWorld(currentArena.world(), false);
            WorldManager.deleteTempWorld(currentArena.world());
            currentArena = null;
        }
    }

    /**
     * Whether the current arena does not match the chosen arena
     */
    public static boolean needsUpdate() {
        if(needsUpdate) return true;
        return WorldManager.hasMap() && currentArena != null && chosenArena != null && !currentArena.name().equals(chosenArena.name());
    }

    public static void setupCurrentArena() {
        removeCurrentArena();
        if(chosenArena == null) {
            currentArena = MathUtils.choose(arenas).cloneAsTemp();
        } else {
            currentArena = chosenArena.cloneAsTemp();
        }
        resetArenaBorder(currentArena);
        needsUpdate = false;
    }

    public static void resetArenaBorder(Arena arena) {
        WorldBorder arenaBorder = arena.world().getWorldBorder();
        arenaBorder.setDamageBuffer(1);
        arenaBorder.setWarningDistance(1);
        arenaBorder.setSize(arena.maxBorderSize() * 4);
        arenaBorder.setCenter(arena.world().getSpawnLocation());
    }

    public static void switchChosenArena() {
        int chosenArenaIndex = arenas.indexOf(chosenArena);
        if(chosenArenaIndex == -1) {
            chosenArena = arenas.get(0);
            needsUpdate = false;
        } else {
            if(chosenArenaIndex == arenas.size() - 1) {
                chosenArena = null;
                needsUpdate = true;
            } else {
                chosenArena = arenas.get(chosenArenaIndex + 1);
            }
        }
    }

    public static List<Arena> getArenas() {
        return arenas;
    }

    /**
     * Gets the arena that players chose in the lobby
     */
    @Nullable
    public static Arena getChosenArena() {
        return chosenArena;
    }

    public static void setChosenArena(Arena chosenArena) {
        ArenaManager.chosenArena = chosenArena;
    }

    /**
     * Gets the arena that current game's deathmatch will go on
     */
    public static Arena getCurrentArena() {
        return currentArena;
    }

    public record Arena(World world, String name, int maxBorderSize, int minBorderSize, boolean isOpen) {

        public Arena cloneAsTemp() {
            World tempWorld = WorldManager.copyAsTemp(world);
            return new Arena(tempWorld, name, maxBorderSize, minBorderSize, isOpen);
        }

        public String getSimpleName() {
            return name.replaceAll(" ", "_");
        }

    }

}

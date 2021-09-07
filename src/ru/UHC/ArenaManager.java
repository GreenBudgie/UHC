package ru.UHC;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import ru.main.UHCPlugin;
import ru.util.MathUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class ArenaManager {

    private static Arena chosenArena = null;
    private static boolean announceArena = true;
    private static Arena currentArena;
    private static List<Arena> arenas = new ArrayList<>();

    private static boolean needsUpdate = false;
    
    public static void init() {
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
                for(ArenaOptions option : ArenaOptions.values()) {
                    String parameterName = option.name();
                    Object parameter = arenaConfig.get(parameterName);
                    if(parameter == null) {
                        UHCPlugin.warning("\"" + file.getName() + "\" config has no parameter " + parameterName);
                        parameter = option.getDefaultValue();
                        arenaConfig.set(parameterName, parameter);
                        toUpdate = true;
                    }
                }
                Arena arena;
                try {
                    arena = Arena.deserialize(arenaWorld, arenaConfig.getValues(false));
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
            Bukkit.unloadWorld(currentArena.getWorld(), false);
            WorldManager.deleteTempWorld(currentArena.getWorld());
            currentArena = null;
        }
    }

    /**
     * Whether the current arena does not match the chosen arena
     */
    public static boolean needsUpdate() {
        if(needsUpdate) return true;
        return WorldManager.hasMap() && currentArena != null && chosenArena != null && !currentArena.getName().equals(chosenArena.getName());
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
        WorldBorder arenaBorder = arena.getWorld().getWorldBorder();
        arenaBorder.setDamageBuffer(1);
        arenaBorder.setWarningDistance(1);
        arenaBorder.setSize(arena.getMaxBorderSize() * 4);
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

    public enum ArenaOptions {

        NAME("Unknown arena"),
        MAX_BORDER_SIZE(32),
        MIN_BORDER_SIZE(10),
        IS_OPEN(true),
        IS_ENABLED(true);

        private Object defaultValue;

        ArenaOptions(Object defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static final class Arena implements ConfigurationSerializable {

        private final World world;
        private String name;
        private int maxBorderSize;
        private int minBorderSize;
        private boolean isOpen;
        private boolean isEnabled;

        public Arena(World world, String name, int maxBorderSize, int minBorderSize, boolean isOpen, boolean isEnabled) {
            this.world = world;
            this.name = name;
            this.maxBorderSize = maxBorderSize;
            this.minBorderSize = minBorderSize;
            this.isOpen = isOpen;
            this.isEnabled = isEnabled;
        }

        public Object getByOption(ArenaOptions option) {
            return switch(option) {
                case NAME -> name;
                case MAX_BORDER_SIZE -> maxBorderSize;
                case MIN_BORDER_SIZE -> minBorderSize;
                case IS_OPEN -> isOpen;
                case IS_ENABLED -> isEnabled;
            };
        }

        public Arena cloneAsTemp() {
            World tempWorld = WorldManager.copyAsTemp(world);
            return new Arena(tempWorld, name, maxBorderSize, minBorderSize, isOpen, isEnabled);
        }

        public String getSimpleName() {
            return name.replaceAll(" ", "_");
        }

        public boolean updateConfig() {
            File worldFile = world.getWorldFolder();
            File configFile = new File(worldFile.getAbsolutePath() + File.separator + "arena.yml");
            if(configFile.exists()) {
                configFile.delete();
            }
            try {
                configFile.createNewFile();
            } catch(Exception exception) {
                UHCPlugin.error("Unable to update config for \"" + getName() + "\"");
                exception.printStackTrace();
                return false;
            }
            YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(configFile);
            try {
                arenaConfig.save(configFile);
            } catch(Exception exception) {
                UHCPlugin.error("Unable to update config for \"" + getName() + "\"");
                exception.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> output = new HashMap<>();
            for(ArenaOptions option : ArenaOptions.values()) {
                output.put(option.name(), getByOption(option));
            }
            return output;
        }

        public static Arena deserialize(World world, Map<String, Object> input) {
            String name = (String) input.getOrDefault(ArenaOptions.NAME.name(), ArenaOptions.NAME.getDefaultValue());
            int maxBorderSize = (int) input.getOrDefault(ArenaOptions.MAX_BORDER_SIZE.name(), ArenaOptions.MAX_BORDER_SIZE.getDefaultValue());
            int minBorderSize = (int) input.getOrDefault(ArenaOptions.MIN_BORDER_SIZE.name(), ArenaOptions.MIN_BORDER_SIZE.getDefaultValue());
            boolean isOpen = (boolean) input.getOrDefault(ArenaOptions.IS_OPEN.name(), ArenaOptions.IS_OPEN.getDefaultValue());
            boolean isEnabled = (boolean) input.getOrDefault(ArenaOptions.IS_ENABLED.name(), ArenaOptions.IS_ENABLED.getDefaultValue());
            return new Arena(world, name, maxBorderSize, minBorderSize, isOpen, isEnabled);
        }

        public World getWorld() {
            return world;
        }

        public String getName() {
            return name;
        }

        public int getMaxBorderSize() {
            return maxBorderSize;
        }

        public int getMinBorderSize() {
            return minBorderSize;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setMaxBorderSize(int maxBorderSize) {
            this.maxBorderSize = maxBorderSize;
        }

        public void setMinBorderSize(int minBorderSize) {
            this.minBorderSize = minBorderSize;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }
    }

}

package ru.greenbudgie.UHC;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.item.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class ArenaManager implements Listener {

    private static final String INVENTORY_TITLE = DARK_GREEN + "Просмотр Арены";

    private static Arena chosenArena = null;
    private static boolean announceArena = true;
    private static Arena currentArena;
    private final static List<Arena> arenas = new ArrayList<>();

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
        if (currentArena == null) {
            setupCurrentArena();
        }
        Bukkit.getPluginManager().registerEvents(new ArenaManager(), UHCPlugin.instance);
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
            currentArena = MathUtils.choose(getEnabledArenas()).cloneAsTemp();
        } else {
            currentArena = chosenArena.cloneAsTemp();
        }
        resetArenaBorder(currentArena);
        currentArena.getWorld().setGameRule(GameRule.DO_TILE_DROPS, false);
        needsUpdate = false;
    }

    public static void resetArenaBorder(Arena arena) {
        WorldBorder arenaBorder = arena.getWorld().getWorldBorder();
        arenaBorder.setDamageBuffer(1);
        arenaBorder.setWarningDistance(1);
        arenaBorder.setSize(arena.getMaxBorderSize());
    }

    public static void switchChosenArena() {
        List<Arena> enabledArenas = getEnabledArenas();
        int chosenArenaIndex = enabledArenas.indexOf(chosenArena);
        if(chosenArenaIndex == -1) {
            chosenArena = enabledArenas.get(0);
            needsUpdate = false;
        } else {
            if(chosenArenaIndex == enabledArenas.size() - 1) {
                chosenArena = null;
                needsUpdate = true;
            } else {
                chosenArena = enabledArenas.get(chosenArenaIndex + 1);
            }
        }
    }

    /**
     * Gets the list of arenas.
     * This does not include temp (current) arena.
     */
    public static List<Arena> getArenas() {
        return arenas;
    }

    /**
     * Gets the list of arena worlds.
     * This does not include temp (current) arena world.
     */
    public static List<World> getArenaWorlds() {
        return arenas.stream().map(Arena::getWorld).toList();
    }

    /**
     * Gets the list of enabled arenas.
     * This does not include temp (current) arena.
     */
    public static List<Arena> getEnabledArenas() {
        return arenas.stream().filter(Arena::isEnabled).toList();
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

    /**
     * Whether PVP on the current deathmatch arena is off.
     * Note: it is off in the first 15 seconds of the deathmatch
     */
    public static boolean isPvpDisabled() {
        return !currentArena.getWorld().getPVP();
    }

    public static void openArenaPreviewInventory(Player player) {
        int inventorySize = ((arenas.size() - 1) / 9) * 9 + 9;
        Inventory inventory = Bukkit.createInventory(player, inventorySize, INVENTORY_TITLE);
        for (int i = 0; i < arenas.size(); i++) {
            Arena arena = arenas.get(i);
            ItemStack item = arena.getRepresentingItem();
            inventory.setItem(i, item);
        }
        player.openInventory(inventory);
    }
    
    public static void previewArena(@Nonnull Arena arena, Player player) {
        if (!Lobby.isInLobbyOrWatchingArena(player)) {
            return;
        }
        player.teleport(arena.getWorld().getSpawnLocation());
        player.sendMessage(WHITE + "Просмотр арены - " + DARK_GREEN + arena.getName());
        if(!arena.isOpen()) {
            player.sendMessage(WHITE + "Это " +
                    GRAY + BOLD + "закрытая " +
                    WHITE + "арена - нельзя выйти за ее пределы");
        }
        player.sendMessage(GRAY + "Напиши " + WHITE + "/lobby" + GRAY + ", чтобы вернуться");
        if(!arena.isEnabled()) {
            player.sendMessage(RED + "Эта арена сейчас не используется!");
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(INVENTORY_TITLE)) {
            return;
        }
        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        Material type = item.getType();
        for (Arena arena : arenas) {
            if (arena.itemToShow == type) {
                previewArena(arena, (Player) event.getWhoClicked());
                return;
            }
        }
    }

    public enum ArenaOptions {

        NAME("Unknown arena"),
        MAX_BORDER_SIZE(32),
        MIN_BORDER_SIZE(10),
        IS_OPEN(true),
        IS_ENABLED(true),
        ITEM_TO_SHOW(Material.DIAMOND_SWORD.name())
        ;

        private final Object defaultValue;

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
        private Material itemToShow;

        private Arena(World world) {
            this.world = world;
        }

        public Arena(
                World world,
                String name,
                int maxBorderSize,
                int minBorderSize,
                boolean isOpen,
                boolean isEnabled,
                Material itemToShow
        ) {
            this.world = world;
            this.name = name;
            this.maxBorderSize = maxBorderSize;
            this.minBorderSize = minBorderSize;
            this.isOpen = isOpen;
            this.isEnabled = isEnabled;
            this.itemToShow = itemToShow;
        }

        public void setByOption(ArenaOptions option, String value) throws NumberFormatException {
            switch(option) {
                case NAME -> setName(value.replaceAll("_", " "));
                case MAX_BORDER_SIZE -> setMaxBorderSize(Integer.parseInt(value));
                case MIN_BORDER_SIZE -> setMinBorderSize(Integer.parseInt(value));
                case IS_OPEN -> setOpen(Boolean.parseBoolean(value));
                case IS_ENABLED -> setEnabled(Boolean.parseBoolean(value));
                case ITEM_TO_SHOW -> setItemToShow(Material.valueOf(value));
            }
        }

        public Object getByOption(ArenaOptions option) {
            return switch(option) {
                case NAME -> name;
                case MAX_BORDER_SIZE -> maxBorderSize;
                case MIN_BORDER_SIZE -> minBorderSize;
                case IS_OPEN -> isOpen;
                case IS_ENABLED -> isEnabled;
                case ITEM_TO_SHOW -> itemToShow.name();
            };
        }

        public ItemStack getRepresentingItem() {
            return ItemUtils.builder(itemToShow)
                    .withName(DARK_GREEN + "" + BOLD + name)
                    .ifTrue(isOpen).withLore(AQUA + "Открытая арена")
                    .ifFalse(isOpen).withLore(DARK_AQUA + "Закрытая арена")
                    .withLore(GOLD + "" + BOLD + "<ЛКМ>" + GRAY + " телепортироваться")
                    .build();
        }

        public Arena cloneAsTemp() {
            World tempWorld = WorldManager.copyAsTemp(world);
            return new Arena(tempWorld, name, maxBorderSize, minBorderSize, isOpen, isEnabled, itemToShow);
        }

        public String getSimpleName() {
            return name.replaceAll(" ", "_");
        }

        public boolean updateConfig() {
            File worldFile = world.getWorldFolder();
            File configFile = new File(worldFile.getAbsolutePath() + File.separator + "arena.yml");
            if(!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch(Exception exception) {
                    UHCPlugin.error("Unable to update config for \"" + getName() + "\"");
                    exception.printStackTrace();
                    return false;
                }
            }
            YamlConfiguration arenaConfig = YamlConfiguration.loadConfiguration(configFile);
            for(ArenaOptions option : ArenaOptions.values()) {
                arenaConfig.set(option.name(), getByOption(option));
            }
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
            Arena arena = new Arena(world);
            for(ArenaOptions option : ArenaOptions.values()) {
                arena.setByOption(option, input.getOrDefault(option.name(), option.getDefaultValue()).toString());
            }
            return arena;
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

        public Material getItemToShow() {
            return itemToShow;
        }

        public void setItemToShow(Material itemToShow) {
            this.itemToShow = itemToShow;
        }

    }

}

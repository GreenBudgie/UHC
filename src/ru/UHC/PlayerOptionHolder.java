package ru.UHC;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.classes.ClassManager;
import ru.classes.UHCClass;
import ru.main.UHCPlugin;

import javax.annotation.Nullable;
import java.io.File;

/**
 * A class for saving and restoring player preferences as the plugin restarts.
 */
public class PlayerOptionHolder {

    private static final File savedPlayerOptionsFile = new File(UHCPlugin.instance.getDataFolder() + File.separator + "player_options.yml");
    private static final YamlConfiguration savedPlayerOptions = YamlConfiguration.loadConfiguration(savedPlayerOptionsFile);

    public static void setSelectedClass(Player player, UHCClass uhcClass) {
        savedPlayerOptions.set(player.getName() + ".class", uhcClass == null ? null : uhcClass.getConfigName());
    }

    @Nullable
    public static UHCClass getSelectedClass(Player player) {
        ConfigurationSection section = savedPlayerOptions.getConfigurationSection(player.getName());
        if(section == null) return null;
        String rawClass = section.getString("class");
        if(rawClass == null) return null;
        return ClassManager.getClassByConfigName(rawClass);
    }

    public static void saveOptions() {
        try {
            savedPlayerOptions.save(savedPlayerOptionsFile);
        } catch(Exception ignored) {}
    }

}

package ru.greenbudgie.UHC;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.greenbudgie.classes.ClassManager;
import ru.greenbudgie.classes.UHCClass;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.MutatorManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static Set<Mutator> getMutatorPreferences(String playerName) {
        ConfigurationSection section = savedPlayerOptions.getConfigurationSection(playerName);
        if(section == null) return new HashSet<>();
        List<String> mutatorNames = section.getStringList("mutators");
        if(mutatorNames.isEmpty()) return new HashSet<>();
        Set<Mutator> preferredMutators = new HashSet<>();
        for(String mutatorName : mutatorNames) {
            Mutator mutator = MutatorManager.getMutatorByConfigName(mutatorName);
            if(mutator != null) preferredMutators.add(mutator);
        }
        return preferredMutators;
    }

    public static void saveMutatorPreferences(String playerName, @Nullable Set<Mutator> preferredMutators) {
        List<String> mutatorConfigNames = null;
        if(preferredMutators != null) {
            mutatorConfigNames = preferredMutators.stream().map(Mutator::getConfigName).toList();
        }
        savedPlayerOptions.set(playerName + ".mutators", mutatorConfigNames);
    }

    @Nullable
    public static String getLobbyTeammateName(String playerName) {
        ConfigurationSection section = savedPlayerOptions.getConfigurationSection(playerName);
        if(section == null) return null;
        return section.getString("teammate");
    }

    public static void saveLobbyTeammates(String player1Name, String player2Name) {
        savedPlayerOptions.set(player1Name + ".teammate", player2Name);
        savedPlayerOptions.set(player2Name + ".teammate", player1Name);
    }

    public static void removeLobbyTeammate(String playerName) {
        savedPlayerOptions.set(playerName + ".teammate", null);
    }

    public static void saveOptions() {
        try {
            savedPlayerOptions.save(savedPlayerOptionsFile);
        } catch(Exception ignored) {}
    }

}

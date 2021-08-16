package ru.lobby;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.UHC.WorldManager;
import ru.main.UHCPlugin;

import java.io.File;
import java.util.ArrayList;

public class Lobby {

    private static YamlConfiguration lobbyConfig;

    public static void init() {
        boolean toUpdate = false;
        File file = new File(WorldManager.getLobby().getWorldFolder() + File.separator + "lobby.yml");
        try {
            if(!file.exists()) file.createNewFile();
        } catch(Exception e) {
            UHCPlugin.error("Cannot create lobby.yml file");
        }
        lobbyConfig = YamlConfiguration.loadConfiguration(file);
        if(!lobbyConfig.contains("signs")) {
            UHCPlugin.warning("No lobby signs are declared in the config");
            lobbyConfig.createSection("signs");
            toUpdate = true;
        }
        ConfigurationSection signsSection = lobbyConfig.getConfigurationSection("signs");
        for(SignType type : SignType.values()) {
            if(!signsSection.contains(type.name()) || signsSection.getStringList(type.name()).isEmpty()) {
                UHCPlugin.warning("There are no locations declared for sign type " + type.name());
                signsSection.set(type.name(), new ArrayList<>());
                toUpdate = true;
            }
        }
        try {
            if (toUpdate) lobbyConfig.save(file);
        } catch(Exception e) {
            UHCPlugin.error("Unable to save lobby.yml");
        }
        SignManager.init();
    }

    public static YamlConfiguration getLobbyConfig() {
        return lobbyConfig;
    }

    public static World getLobby() {
        return WorldManager.getLobby();
    }

}

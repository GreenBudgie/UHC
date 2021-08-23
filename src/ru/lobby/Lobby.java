package ru.lobby;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.UHC.WorldManager;
import ru.lobby.sign.LobbySign;
import ru.lobby.sign.SignManager;
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
        for(LobbySign lobbySign : SignManager.getSigns()) {
            if(!signsSection.contains(lobbySign.getConfigName()) ||
                    signsSection.getStringList(lobbySign.getConfigName()).isEmpty()) {
                UHCPlugin.warning("There are no locations declared for sign type " + lobbySign.getConfigName());
                signsSection.set(lobbySign.getConfigName(), new ArrayList<>());
                toUpdate = true;
            }
        }
        try {
            if (toUpdate) lobbyConfig.save(file);
        } catch(Exception e) {
            UHCPlugin.error("Unable to save lobby.yml");
        }
        SignManager.init();
        LobbyGameManager.init();
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), UHCPlugin.instance);
        Bukkit.getPluginManager().registerEvents(new LobbyTeamBuilder(), UHCPlugin.instance);
    }

    public static YamlConfiguration getLobbyConfig() {
        return lobbyConfig;
    }

    public static World getLobby() {
        return WorldManager.getLobby();
    }

    public static boolean isInLobby(Player player) {
        return getLobby().getPlayers().contains(player);
    }



}

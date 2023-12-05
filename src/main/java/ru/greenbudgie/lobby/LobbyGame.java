package ru.greenbudgie.lobby;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.greenbudgie.main.UHCPlugin;

import java.util.Map;

public abstract class LobbyGame {

    protected LobbyGame() {
        LobbyGameManager.lobbyGames.add(this);
    }

    protected final void parseConfig() {
        YamlConfiguration config = Lobby.getLobbyConfig();
        ConfigurationSection section = config.getConfigurationSection(getConfigName());
        if(section == null) {
            UHCPlugin.warning(getConfigName() + " is not present in the config");
            return;
        }
        Map<String, Object> values = section.getValues(false);
        for(String option : values.keySet()) {
            parseConfigOption(option, values.get(option));
        }
    }

    public abstract String getConfigName();
    public abstract void parseConfigOption(String option, Object value);
    public abstract void update();
    protected void postSetup() {}

}

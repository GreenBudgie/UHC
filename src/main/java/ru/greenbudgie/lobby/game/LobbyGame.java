package ru.greenbudgie.lobby.game;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.main.UHCPlugin;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class LobbyGame {

    protected LobbyGame() {
        LobbyGameManager.lobbyGames.add(this);
    }

    protected final void parseConfig() {
        String configName = getConfigName();
        if (configName == null) {
            return;
        }
        YamlConfiguration config = Lobby.getLobbyConfig();
        ConfigurationSection section = config.getConfigurationSection(configName);
        if (section == null) {
            UHCPlugin.warning(getConfigName() + " is not present in the lobby config");
            return;
        }
        Map<String, Object> values = section.getValues(false);
        for(String option : values.keySet()) {
            parseConfigOption(option, values.get(option));
        }
    }

    /**
     * Name of the config section in lobby config file.
     * Can be null if config is not needed.
     */
    @Nullable
    public abstract String getConfigName();
    public abstract void parseConfigOption(String option, Object value);
    public abstract void update();

    /**
     * Whether the player is participating in this game
     */
    public abstract boolean isParticipating(Player player);
    protected void postSetup() {}

}

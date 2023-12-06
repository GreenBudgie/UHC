package ru.greenbudgie.lobby.sign;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.lobby.Lobby;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignManager implements Listener {

	protected static List<LobbySign> signs = new ArrayList<>();

	static {
		new LobbySignGameStart();
		new LobbySignGameDuo();
		new LobbySignGameArena();
		new LobbySignGameMapSize();
		new LobbySignGameDuration();
		new LobbySignGameFastStart();
		new LobbySignGameRating();
		new LobbySignArenaNextKit();
		new LobbySignSpectate();
		new LobbySignTeammateSelect();
		new LobbySignMapGenerate();
		new LobbySignClassSelect();
		new LobbySignMutatorSelect();
		new LobbySignShowRating();
		new LobbySignGameType();
		new LobbySignReturnLobby();
		new LobbySignTeleportToArena();
		new LobbySignShowRequests();
		new LobbySignShowArtifacts();
	}

	public static void init() {
		ConfigurationSection signsSection = Lobby.getLobbyConfig().getConfigurationSection("signs");
		if (signsSection == null) {
			UHCPlugin.error("Signs configuration is not present in config");
			return;
		}
		Map<String, Object> map = signsSection.getValues(false);
		for(String signName : map.keySet()) {
			LobbySign sign = getSignByName(signName);
			if(sign == null) {
				UHCPlugin.warning("There is no such sign type " + signName);
				continue;
			}
			List<String> locations = signsSection.getStringList(signName);
			for(String locationString : locations) {
				Location location = WorldHelper.translateToLocation(Lobby.getLobby(), locationString);
				if(location == null) {
					UHCPlugin.warning("Illegal sign location notation: " + locationString);
					continue;
				}
				if(!(location.getBlock().getState() instanceof Sign)) {
					UHCPlugin.warning("No sign at specified location: " + locationString + "(" + signName + ")");
					continue;
				}
				sign.addLocation(location);
			}
		}
		updateTextOnSigns();
	}

	public static List<LobbySign> getSigns() {
		return signs;
	}

	private static LobbySign getSignByName(String name) {
		for(LobbySign sign : signs) {
			if(sign.getConfigName().equals(name)) return sign;
		}
		return null;
	}

	public static LobbySign getSignAt(Location location) {
		for(LobbySign sign : signs) {
			if(sign.hasSignAtLocation(location)) return sign;
		}
		return null;
	}

	public static void clearSign(Sign sign) {
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
	}

	public static void updateTextOnSigns() {
		for(LobbySign lobbySign : signs) {
			for(Location signLocation : lobbySign.getLocations()) {
				Sign sign = lobbySign.getSignState(signLocation);
				clearSign(sign);
				lobbySign.updateText(sign);
				sign.update();
			}
		}
	}

	@EventHandler
	public void signClick(PlayerInteractEvent e) {
		Player clickedPlayer = e.getPlayer();
		if(!UHC.generating && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block clickedBlock = e.getClickedBlock();
			LobbySign lobbySign = getSignAt(clickedBlock.getLocation());
			if(lobbySign != null &&
					(!UHC.playing || lobbySign.canUseWhilePlaying()) &&
					(clickedPlayer.isOp() || lobbySign.canBeUsedByAnyone())) {
				Sign sign = lobbySign.getSignState(clickedBlock.getLocation());
				clickedBlock.getWorld().playSound(clickedBlock.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.05F, 1.5F);
				lobbySign.onClick(clickedPlayer, sign, e);
				updateTextOnSigns();
			}
		}
	}

}

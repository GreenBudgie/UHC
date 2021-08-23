package ru.lobby.sign;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.util.WorldHelper;

import java.util.HashSet;
import java.util.Set;

public abstract class LobbySign {

	private final Set<Location> locations = new HashSet<>();

	public LobbySign() {
		SignManager.signs.add(this);
	}

	public boolean hasSignAtLocation(Location location) {
		return locations.stream().
				anyMatch(currentLocation -> WorldHelper.compareIntLocations(location, currentLocation));
	}

	public abstract String getConfigName();
	public abstract void onClick(Player clicker, Sign sign, PlayerInteractEvent event);
	public abstract void updateText(Sign sign);

	/**
	 * Whether not OP players can use this sign
	 */
	public boolean canBeUsedByAnyone() {
		return false;
	}

	/**
	 * Whether this sign is available while the game is running
	 */
	public boolean canUseWhilePlaying() {
		return false;
	}

	public void addLocation(Location location) {
		locations.add(location);
	}

	public Sign getSignState(Location location) {
		return (Sign) location.getBlock().getState();
	}

	public Set<Location> getLocations() {
		return locations;
	}

}

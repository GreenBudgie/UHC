package ru.lobby;

import org.bukkit.Location;
import org.bukkit.block.Sign;

public record LobbySign(Location location, SignType type) {

	public Sign getSign() {
		return (Sign) location.getBlock().getState();
	}

}

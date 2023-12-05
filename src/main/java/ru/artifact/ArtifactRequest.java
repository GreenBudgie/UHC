package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import ru.UHC.WorldManager;
import ru.drop.Drop;
import ru.drop.Drops;
import ru.requester.ItemRequester;
import ru.requester.RequestedItem;
import ru.util.MathUtils;

public class ArtifactRequest extends Artifact {

	@Override
	public String getName() {
		return ChatColor.AQUA + "Послание с неба";
	}

	@Override
	public String getDescription() {
		return "Создает случайный запрос в случайном месте. В аду не работает.";
	}

	@Override
	public int getStartingPrice() {
		return 8;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	private Location getRandomLocation() {
		int size = ((int) WorldManager.getGameMap().getWorldBorder().getSize()) / 2 - 10;
		int x = MathUtils.randomRange(WorldManager.spawnLocation.getBlockX() - size, WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(WorldManager.spawnLocation.getBlockZ() - size, WorldManager.spawnLocation.getBlockZ() + size);
		int y = WorldManager.getGameMap().getHighestBlockYAt(x, z);
		return new Location(WorldManager.getGameMap(), x, y, z);
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		if(player != null) {
			if(player.getWorld() != WorldManager.getGameMap()) return false;
			player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 2);
		}
		Location location = getRandomLocation();
		RequestedItem requestedItem = new RequestedItem(location, MathUtils.choose(ItemRequester.requesterCustomItems.values()).getItemStack());
		requestedItem.announce(null);
		ItemRequester.requestedItems.add(requestedItem);
		return true;
	}

	@Override
	public Material getType() {
		return Material.PHANTOM_MEMBRANE;
	}

}

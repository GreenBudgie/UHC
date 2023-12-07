package ru.greenbudgie.artifact;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.requester.ItemRequester;
import ru.greenbudgie.requester.RequestedItem;
import ru.greenbudgie.util.MathUtils;

import javax.annotation.Nullable;

public class ArtifactRequest extends Artifact {

	@Override
	public String getName() {
		return "Послание с неба";
	}

	@Override
	public String getDescription() {
		return "Создает случайный запрос в случайном месте. Если ты в аду, запрос все равно будет создан на Земле.";
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
		Location location = getRandomLocation();
		RequestedItem requestedItem = new RequestedItem(location, MathUtils.choose(ItemRequester.requesterCustomItems.values()).getItemStack());
		requestedItem.announce(null);
		ItemRequester.requestedItems.add(requestedItem);
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 1, 2);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.PHANTOM_MEMBRANE;
	}

	@Override
	public boolean canBeUsedOnArena() {
		return false;
	}

}

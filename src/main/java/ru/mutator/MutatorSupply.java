package ru.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.loot.LootTables;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.WorldManager;
import ru.util.MathUtils;

public class MutatorSupply extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.CHEST_MINECART;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Клад";
	}

	@Override
	public String getDescription() {
		return "С неба падает несколько сундуков с рандомным лутом в случайные места на карте";
	}

	private Location chooseLocation() {
		int size = ((int) WorldManager.getGameMap().getWorldBorder().getSize()) / 2 - 10;
		int x = MathUtils.randomRange(WorldManager.spawnLocation.getBlockX() - size, WorldManager.spawnLocation.getBlockX() + size);
		int z = MathUtils.randomRange(WorldManager.spawnLocation.getBlockZ() - size, WorldManager.spawnLocation.getBlockZ() + size);
		int y = WorldManager.getGameMap().getHighestBlockYAt(x, z) + 50;
		return new Location(WorldManager.getGameMap(), x, y, z);
	}

	private LootTables getRandomLootTable() {
		return MathUtils.choose(LootTables.ABANDONED_MINESHAFT, LootTables.BURIED_TREASURE, LootTables.END_CITY_TREASURE, LootTables.DESERT_PYRAMID, LootTables.JUNGLE_TEMPLE, LootTables.NETHER_BRIDGE, LootTables.SHIPWRECK_TREASURE, LootTables.SHIPWRECK_SUPPLY, LootTables.SPAWN_BONUS_CHEST, LootTables.SIMPLE_DUNGEON, LootTables.WOODLAND_MANSION, LootTables.STRONGHOLD_CORRIDOR, LootTables.STRONGHOLD_CROSSING, LootTables.STRONGHOLD_LIBRARY, LootTables.UNDERWATER_RUIN_BIG, LootTables.UNDERWATER_RUIN_SMALL, LootTables.VILLAGE_TOOLSMITH);
	}

	@Override
	public void onChoose() {
		for(int i = 0; i < PlayerManager.getAliveOnlinePlayers().size() * 2; i++) {
			Location loc = chooseLocation();
			StorageMinecart minecart = (StorageMinecart) loc.getWorld().spawnEntity(loc, EntityType.MINECART_CHEST);
			minecart.setLootTable(getRandomLootTable().getLootTable());
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public boolean canBeDeactivatedByArtifact() {
		return false;
	}

}

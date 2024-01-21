package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TrollingEventSpawnMobs extends TrollingEvent {

    private static final int RADIUS = 5;
    private static final int MIN_MOBS_TO_SPAWN = 1;
    private static final int MAX_MOBS_TO_SPAWN = 3;

    private static final EntityType[] MOBS = new EntityType[] {
            EntityType.COW,
            EntityType.MUSHROOM_COW,
            EntityType.BEE,
            EntityType.RABBIT,
            EntityType.SHEEP,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.SALMON,
            EntityType.PUFFERFISH,
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.CREEPER,
            EntityType.PIGLIN,
            EntityType.GUARDIAN,
            EntityType.ENDERMITE,
            EntityType.ILLUSIONER,
            EntityType.SILVERFISH,
    };

    @Override
    public String getName() {
        return "Призыв Мобов";
    }

    @Override
    public void execute() {
        int mobCount = MathUtils.randomRange(MIN_MOBS_TO_SPAWN, MAX_MOBS_TO_SPAWN);
        List<EntityType> mobsToSpawn = new ArrayList<>();
        for (int i = 0; i < mobCount; i++) {
            mobsToSpawn.add(MathUtils.choose(MOBS));
        }
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            Location location = player.getLocation();
            Region region = new Region(
                    location.clone().add(-RADIUS, 1, -RADIUS),
                    location.clone().add(RADIUS, -1, RADIUS)
            );
            Set<Location> allLocations = region.getLocationsInside();
            List<Location> availableLocations = allLocations.stream()
                    .map(Location::getBlock)
                    .filter(block -> !block.getType().isSolid())
                    .map(Block::getLocation)
                    .toList();
            if (availableLocations.isEmpty()) {
                return;
            }
            World world = Objects.requireNonNull(location.getWorld());
            for (EntityType mobToSpawn : mobsToSpawn) {
                Location mobSpawnLocation = MathUtils.choose(availableLocations);
                world.spawnEntity(mobSpawnLocation, mobToSpawn);
            }
        }
    }

}

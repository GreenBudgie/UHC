package ru.greenbudgie.UHC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameStartPlatformManager {

    /**
     * Height of the platform from its floor to its ceiling
     */
    private static final int PLATFORM_HEIGHT = 4;
    /**
     * Vanilla nether generates the bedrock roof as a gradient in the top 5 blocks of the generation height
     */
    private static final int NETHER_BEDROCK_ROOF_THICKNESS = 5;
    /**
     * Minimal number of free blocks between the nether platform and the lava sea or the bedrock roof
     */
    private static final int NETHER_PLATFORM_GAP = 5;

    private static Region overworldPlatformRegion = null;
    private static Region netherPlatformRegion = null;

    public static void removeAllPlatforms() {
        if (overworldPlatformRegion != null) {
            clearRegion(overworldPlatformRegion);
            overworldPlatformRegion = null;
        }
        if (netherPlatformRegion != null) {
            clearRegion(netherPlatformRegion);
            netherPlatformRegion = null;
        }
    }

    public static void createNetherPlatformAndTeleportPlayers() {
        Location spawnLocation = WorldManager.getGameMap().getSpawnLocation();
        World nether = WorldManager.getGameMapNether();
        int lavaTopY = nether.getSeaLevel() - 1;
        int bedrockRoofBottomY = nether.getMinHeight() + nether.getLogicalHeight() - NETHER_BEDROCK_ROOF_THICKNESS;
        int minPlatformY = lavaTopY + 1 + NETHER_PLATFORM_GAP;
        int maxPlatformY = bedrockRoofBottomY - 1 - NETHER_PLATFORM_GAP - PLATFORM_HEIGHT;
        int platformY = MathUtils.clamp(spawnLocation.getBlockY(), minPlatformY, maxPlatformY);
        Location netherSpawnLocation = new Location(nether, spawnLocation.getX(), platformY, spawnLocation.getZ());
        Map<UHCPlayer, Location> playerSpawnLocations = getPlayerSpawnLocations(netherSpawnLocation);

        netherPlatformRegion = createPlatform(netherSpawnLocation);

        teleportPlayersToSpawnLocations(netherSpawnLocation, playerSpawnLocations);
    }

    public static void createOverworldPlatformAndTeleportPlayers() {
        Location spawnCenter = WorldManager.spawnLocation.clone();
        int spawnHeight = Objects.requireNonNull(spawnCenter.getWorld()).getHighestBlockYAt(spawnCenter) + 16;
        spawnCenter.setY(spawnHeight);
        Map<UHCPlayer, Location> playerSpawnLocations = getPlayerSpawnLocations(spawnCenter);

        overworldPlatformRegion = createPlatform(spawnCenter);

        teleportPlayersToSpawnLocations(spawnCenter, playerSpawnLocations);
    }

    private static Map<UHCPlayer, Location> getPlayerSpawnLocations(Location spawnCenter) {
        List<UHCPlayer> players = PlayerManager.getPlayers();
        int playerNumber = players.size();
        double radiansPerPlayer = 2 * Math.PI / PlayerManager.getPlayers().size();
        Map<UHCPlayer, Location> playerSpawnLocations = new HashMap<>();
        for(int i = 0; i < PlayerManager.getPlayers().size(); i++) {
            int x = (int) Math.round(spawnCenter.getX() + Math.cos(radiansPerPlayer * i) * playerNumber);
            int z = (int) Math.round(spawnCenter.getZ() + Math.sin(radiansPerPlayer * i) * playerNumber);
            Location playerSpawnLocation = new Location(
                    spawnCenter.getWorld(),
                    x,
                    spawnCenter.getY(),
                    z
            );
            playerSpawnLocations.put(players.get(i), playerSpawnLocation);
        }

        return playerSpawnLocations;
    }

    private static void teleportPlayersToSpawnLocations(
            Location spawnCenter,
            Map<UHCPlayer, Location> playerSpawnLocations
    ) {
        playerSpawnLocations.forEach((player, playerSpawnLocation) -> {
            double xLength = spawnCenter.getX() - playerSpawnLocation.getX();
            double zLength = spawnCenter.getZ() - playerSpawnLocation.getZ();
            double yaw = -Math.atan2(xLength, zLength);
            Location teleportLocation = playerSpawnLocation.clone().add(0.5, 1, 0.5);
            teleportLocation.setYaw((float) Math.toDegrees(yaw));
            player.teleport(teleportLocation);
        });
    }

    private static Region createPlatform(Location spawnCenter) {
        int playerNumber = PlayerManager.getPlayers().size();
        double radius = playerNumber + 2.5;

        Region region = new Region(
                spawnCenter.clone().add(-radius, 0, -radius),
                spawnCenter.clone().add(radius, PLATFORM_HEIGHT, radius)
        );
        clearRegion(region);

        double invRadius = 1 / radius;
        int ceilRadius = (int) Math.ceil(radius);

        double nextXn = 0;
        for(int x = 0; x <= ceilRadius; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadius;
            double nextZn = 0;
            for(int z = 0; z <= ceilRadius; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadius;
                double distanceSq = (xn * xn) + (zn * zn);
                if(distanceSq > 1) {
                    continue;
                }
                double xBorder = (nextXn * nextXn) + (zn * zn);
                double zBorder = (xn * xn) + (nextZn * nextZn);
                int y = 0;
                Material block = Material.GLASS;
                if(xBorder > 1 || zBorder > 1 ||
                        (radius > 6 && (xBorder < 0.2 || zBorder < 0.2))) {
                    y = 2;
                    block = Material.BARRIER;
                }

                spawnCenter.clone().add(x, y, z).getBlock().setType(block);
                spawnCenter.clone().add(x, y, -z).getBlock().setType(block);
                spawnCenter.clone().add(-x, y, z).getBlock().setType(block);
                spawnCenter.clone().add(-x, y, -z).getBlock().setType(block);

                if(y == 0) {
                    spawnCenter.clone().add(x, PLATFORM_HEIGHT, z).getBlock().setType(Material.BARRIER);
                    spawnCenter.clone().add(x, PLATFORM_HEIGHT, -z).getBlock().setType(Material.BARRIER);
                    spawnCenter.clone().add(-x, PLATFORM_HEIGHT, z).getBlock().setType(Material.BARRIER);
                    spawnCenter.clone().add(-x, PLATFORM_HEIGHT, -z).getBlock().setType(Material.BARRIER);
                }
            }
        }
        return region;
    }

    private static void clearRegion(Region region) {
        for(Block block : region.getBlocksInside()) {
            block.setType(Material.AIR);
        }
    }

}

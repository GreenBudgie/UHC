package ru.greenbudgie.block;

import org.bukkit.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;

public class CustomBlockTerraTracer extends CustomBlockItem {

    private Location nearestOre;

    public CustomBlockTerraTracer(Location location) {
        super(location);
    }

    @Override
    public void onCreate() {
        World world = location.getWorld();
        world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.5F, 0.5F);
        ParticleUtils.createCircle(centerLocation, Particle.CLOUD, null, 1.5, 10);
        int distance = getSearchDistance();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();
        Location nearest = null;
        for(int x = centerX - distance; x <= centerX + distance; x++) {
            for(int y = centerY - distance; y <= centerY + distance; y++) {
                for(int z = centerZ - distance; z <= centerZ + distance; z++) {
                    Location currentLocation = new Location(world, x, y, z);
                    if(!world.getWorldBorder().isInside(currentLocation)) continue;
                    Material blockType = currentLocation.getBlock().getType();
                    if(blockType == Material.DIAMOND_ORE || blockType == Material.DEEPSLATE_DIAMOND_ORE || blockType == Material.ANCIENT_DEBRIS) {
                        if(nearest == null) {
                            nearest = currentLocation;
                        } else {
                            if(currentLocation.distanceSquared(location) < nearest.distanceSquared(location)) {
                                nearest = currentLocation;
                            }
                        }
                    }
                }
            }
        }
        nearestOre = nearest;
        if(nearestOre != null) {
            nearestOre.add(0.5, 0.5, 0.5);
        }
    }

    @Override
    public boolean isUnbreakable() {
        return true;
    }

    @Override
    public boolean removeIfRealBlockNotPresent() {
        return true;
    }

    private boolean hasLocatedOre() {
        return nearestOre != null;
    }

    /**
     * A 1/2 side length of a cuboid that the beacon will search through
     */
    private int getSearchDistance() {
        return 20;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
        if(ticksPassed > 15 && ticksPassed < 80 && ticksPassed % 5 == 0) {
            double radius = 1.3;
            double u = Math.random();
            double v = Math.random();
            double theta = 2 * Math.PI * u;
            double phi = Math.acos(2 * v - 1);
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = Math.abs(radius * Math.sin(phi) * Math.sin(theta));
            double z = radius * Math.cos(phi);
            Location randomLocation = centerLocation.clone().add(x, y, z);
            ParticleUtils.createParticlesOutlineSphere(
                    randomLocation, 0.3, Particle.REDSTONE, Color.fromRGB(59, 111, 255), 8);
            randomLocation.getWorld().playSound(randomLocation, Sound.BLOCK_AMETHYST_BLOCK_HIT, 2, (float) MathUtils.randomRangeDouble(0.5, 2));
        }
        if(ticksPassed == 80) {
            if(hasLocatedOre()) {
                ParticleUtils.flash(centerLocation);
                location.getWorld().playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);
            } else {
                location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.5F, 1.5F);
                ParticleUtils.createCircle(centerLocation, Particle.SMOKE_NORMAL, null, 1.5, 10);
                dropAndRemove();
            }
        }
        if(hasLocatedOre() && ticksPassed > 80 && ticksPassed % 10 == 0) {
            if(nearestOre.getBlock().getType() == Material.AIR) {
                location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 1.5F, 1.5F);
                ParticleUtils.createCircle(centerLocation, Particle.SMOKE_NORMAL, null, 1.5, 10);
                remove();
                return;
            }
            ParticleUtils.createLine(centerLocation, nearestOre, Particle.REDSTONE, 3, Color.fromRGB(59, 111, 255));
        }
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.terraTracer;
    }

}

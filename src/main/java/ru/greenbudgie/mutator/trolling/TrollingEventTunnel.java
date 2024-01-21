package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.WorldHelper;

import java.util.Objects;

public class TrollingEventTunnel extends TrollingEvent {


    @Override
    public String getName() {
        return "Тоннель";
    }

    @Override
    public boolean canWorkOnArena() {
        return false;
    }

    @Override
    public void execute() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            Location location = player.getLocation();
            World world = Objects.requireNonNull(location.getWorld());
            int maxHeight = world.getHighestBlockYAt(location);
            if (location.getY() >= maxHeight) {
                return;
            }
            Location endLocation = new Location(location.getWorld(), location.getX(), maxHeight, location.getZ());
            Region tunnelRegion = new Region(
                    location,
                    endLocation
            );
            tunnelRegion.getBlocksInside().stream()
                    .filter(block -> !WorldHelper.isUnbreakable(block))
                    .forEach(Block::breakNaturally);
            ParticleUtils.createParticlesOnRegionFaces(tunnelRegion, Particle.CLOUD, 2, null);
            world.playSound(location, Sound.ENTITY_GUARDIAN_HURT, 1F, 1.8F);
        }
    }

}

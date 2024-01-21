package ru.greenbudgie.mutator.trolling;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.MathUtils;

import java.util.Objects;

public class TrollingEventSpawnTnt extends TrollingEvent {

    @Override
    public String getName() {
        return "Взрыв";
    }

    @Override
    public void execute() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            Location location = player.getLocation();
            World world = Objects.requireNonNull(location.getWorld());
            world.playSound(location, Sound.ENTITY_TNT_PRIMED, 1F, 1.5F);
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(location, EntityType.PRIMED_TNT);
            tnt.setVelocity(
                    new Vector(
                            MathUtils.randomRangeDouble(-0.2, 0.2),
                            MathUtils.randomRangeDouble(0.1, 0.3),
                            MathUtils.randomRangeDouble(-0.2, 0.2)
                    )
            );
            tnt.setFuseTicks(80);
        }
    }

}

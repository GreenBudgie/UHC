package ru.greenbudgie.mutator.trolling;

import org.bukkit.entity.LivingEntity;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.WorldHelper;

public class TrollingEventTeleport extends TrollingEvent {

    private static final int RANGE = 50;

    @Override
    public String getName() {
        return "Телепортация";
    }

    @Override
    public void execute() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            LivingEntity entity = player.getPlayerOrGhost();
            WorldHelper.chorusTeleport(entity, RANGE);
        }
    }

}

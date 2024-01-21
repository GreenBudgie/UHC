package ru.greenbudgie.mutator.trolling;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.PotionEffectBuilder;

public class TrollingEventGlowing extends TrollingEvent {

    @Override
    public String getName() {
        return "Свечение";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            player.addPotionEffect(
                    new PotionEffectBuilder(PotionEffectType.GLOWING).seconds(10).build()
            );
        }
    }

}

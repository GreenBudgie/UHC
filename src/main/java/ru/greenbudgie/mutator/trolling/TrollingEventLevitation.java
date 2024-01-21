package ru.greenbudgie.mutator.trolling;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.PotionEffectBuilder;

public class TrollingEventLevitation extends TrollingEvent {

    @Override
    public String getName() {
        return "Левитация";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            player.addPotionEffect(
                    new PotionEffectBuilder(PotionEffectType.LEVITATION).seconds(5).amplifier(2).build()
            );
        }
    }

}

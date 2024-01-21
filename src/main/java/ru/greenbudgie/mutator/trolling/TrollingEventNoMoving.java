package ru.greenbudgie.mutator.trolling;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.PotionEffectBuilder;

import java.util.List;

public class TrollingEventNoMoving extends TrollingEvent {

    private static final List<PotionEffect> effects = List.of(
            new PotionEffectBuilder(PotionEffectType.SLOW).seconds(10).amplifier(255).build(),
            new PotionEffectBuilder(PotionEffectType.JUMP).seconds(10).amplifier(200).build()
    );

    @Override
    public String getName() {
        return "Обездвиживание";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            player.addPotionEffects(effects);
        }
    }

}

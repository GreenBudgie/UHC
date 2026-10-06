package ru.greenbudgie.mutator.trolling;

import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.PotionEffectBuilder;

public class TrollingEventCreeper extends TrollingEvent {

    private static final PotionEffect effect = new PotionEffectBuilder(PotionEffectType.BLINDNESS)
            .seconds(10)
            .amplifier(0)
            .build();

    @Override
    public String getName() {
        return "Крипер?";
    }

    @Override
    public void execute() {
        for (Player player : PlayerManager.getAliveOnlinePlayers()) {
            player.addPotionEffect(effect);
            player.playSound(player, Sound.ENTITY_CREEPER_PRIMED, 2f, 0.5f);
            Creeper creeper = (Creeper) player.getWorld().spawnEntity(player.getLocation(), EntityType.CREEPER);
            creeper.setExplosionRadius(0);
            creeper.ignite();
            creeper.setPowered(true);
        }
    }

}

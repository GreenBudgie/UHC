package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.FightHelper;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;

import java.util.List;

public class CustomBlockInfernalTotem extends CustomBlockTotem {

    public CustomBlockInfernalTotem(Location location, Player owner) {
        super(location, owner);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.infernalTotem;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location.getWorld().playSound(location, Sound.ENTITY_BLAZE_DEATH, 1, 0.5f);
    }

    @Override
    public void produceEffect() {
        ParticleUtils.createParticlesOutlineSphere(centerLocation, getEffectRadius(), Particle.FLAME, null, 4);
        if(ticksPassed > 0 && ticksPassed % 15 == 0) {
            ParticleUtils.createParticlesOutlineSphere(centerLocation, 2, Particle.SOUL_FIRE_FLAME, null, 20);
            location.getWorld().playSound(location, Sound.ENTITY_BLAZE_HURT, 0.7F, 0.5F);
            List<LivingEntity> entities = location.getWorld().getLivingEntities();
            for(LivingEntity entity : entities) {
                if(entity.getLocation().distanceSquared(centerLocation) <= getEffectRadius() * getEffectRadius()) {
                    if(entity instanceof Player player && (!PlayerManager.isPlaying(player) || isImmune(player))) continue;
                    ParticleUtils.createParticlesAround(entity, Particle.SMOKE_LARGE, null, 10);
                    entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 0.6F);
                    entity.setFireTicks(Math.max(entity.getFireTicks(), 100));
                    if(entity instanceof Player player) {
                        FightHelper.setDamager(player, owner, 110, "убил тотемом");
                    }
                }
            }
        }
    }

    @Override
    public void onEffectStop() {
        super.onEffectStop();
        ParticleUtils.createParticlesInside(getBlock(), Particle.SMOKE_LARGE, null, 10);
        location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0.5f);
    }

    @Override
    public int getEffectDuration() {
        return 20 * 30;
    }

    @Override
    public double getEffectRadius() {
        return 5;
    }

}

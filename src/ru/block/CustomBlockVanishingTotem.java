package ru.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import ru.UHC.FightHelper;
import ru.UHC.PlayerManager;
import ru.items.CustomItem;
import ru.items.CustomItems;
import ru.util.ParticleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomBlockVanishingTotem extends CustomBlockTotem {

    public CustomBlockVanishingTotem(Location location, Player owner) {
        super(location, owner);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.vanishingTotem;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1, 2f);
    }

    @Override
    public void produceEffect() {
        ParticleUtils.createParticlesOutlineSphere(centerLocation, getEffectRadius(), Particle.CLOUD, null, 6);
        Collection<Projectile> projectiles = location.getWorld().getEntitiesByClass(Projectile.class);
        double radiusSq = (getEffectRadius() + 1) * (getEffectRadius() + 1);
        for(Projectile projectile : projectiles) {
            if(projectile.getShooter() instanceof Player shooter && isImmune(shooter)) continue;
            if(projectile.getLocation().distanceSquared(centerLocation) <= radiusSq) {
                ParticleUtils.createParticlesAround(projectile, Particle.SMOKE_LARGE, null, 4);
                ParticleUtils.createLine(centerLocation, projectile.getLocation(), Particle.SMALL_FLAME, 3, null);
                projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_PHANTOM_HURT, 1, 2);
                projectile.remove();
            }
        }
        if(ticksPassed > 0 && ticksPassed % 30 == 0) {
            ParticleUtils.createParticle(centerLocation, Particle.FLASH, null);
            location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.5F, 0.5F);
        }
    }

    @Override
    public void onEffectStop() {
        super.onEffectStop();
        ParticleUtils.createParticlesInside(getBlock(), Particle.SMOKE_LARGE, null, 10);
        location.getWorld().playSound(location, Sound.ENTITY_ENDER_EYE_DEATH, 1, 0.5f);
    }

    @Override
    public int getEffectDuration() {
        return 20 * 30;
    }

    @Override
    public double getEffectRadius() {
        return 6;
    }

}

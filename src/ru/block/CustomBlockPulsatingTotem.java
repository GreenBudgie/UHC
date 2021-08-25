package ru.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import ru.items.CustomItem;
import ru.items.CustomItems;
import ru.util.ParticleUtils;

import java.util.Collection;

public class CustomBlockPulsatingTotem extends CustomBlockTotem {

    public CustomBlockPulsatingTotem(Location location, Player owner) {
        super(location, owner);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.pulsatingTotem;
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

                Location projectileLocation = projectile.getLocation();
                Vector projectilePointer = new Vector(
                        projectileLocation.getX() - centerLocation.getX(),
                        projectileLocation.getY() - centerLocation.getY(),
                        projectileLocation.getZ() - centerLocation.getZ());
                projectilePointer.normalize();
                projectilePointer.multiply(0.7);
                projectile.setVelocity(projectilePointer);

                ParticleUtils.createParticlesAround(projectile, Particle.SMOKE_LARGE, null, 4);
                ParticleUtils.createLine(centerLocation, projectile.getLocation(), Particle.SMALL_FLAME, 3, null);
                projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_PHANTOM_HURT, 1, 2);
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

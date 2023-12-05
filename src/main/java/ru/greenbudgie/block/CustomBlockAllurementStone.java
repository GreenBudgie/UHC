package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockAllurementStone extends CustomBlockTotem {

    public CustomBlockAllurementStone(Location location, Player owner) {
        super(location, owner);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.allurementStone;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location.getWorld().playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, 1, 0.5f);
    }

    @Override
    public boolean arePlayersImmuneWithNoPVP() {
        return false;
    }

    @Override
    public void produceEffect() {
        if(ticksPassed % 2 == 0) {
            ParticleUtils.createParticlesOutlineSphere(centerLocation, getEffectRadius(), Particle.END_ROD, null, 12);
            ParticleUtils.createParticlesInside(getBlock(), Particle.PORTAL, null, 1);
        }
        if(ticksPassed > 0 && ticksPassed % 12 == 0) {
            List<LivingEntity> nearbyEntities = new ArrayList<>();
            List<LivingEntity> entities = location.getWorld().getLivingEntities();
            for(LivingEntity entity : entities) {
                if(entity.getLocation().distanceSquared(centerLocation) <= getEffectRadius() * getEffectRadius()) {
                    if(entity instanceof Player player && (!PlayerManager.isPlaying(player) || isImmune(player))) continue;
                    nearbyEntities.add(entity);
                }
            }
            for(LivingEntity entity : nearbyEntities) {
                Location playerLocation = entity.getEyeLocation();
                Vector playerPointer = new Vector(
                        playerLocation.getX() - centerLocation.getX(),
                        playerLocation.getY() - centerLocation.getY(),
                        playerLocation.getZ() - centerLocation.getZ());
                playerPointer.normalize();
                playerPointer.multiply(-1.6);
                Vector currentVelocity = entity.getVelocity();
                currentVelocity.add(playerPointer);
                entity.setVelocity(currentVelocity);
                ParticleUtils.createParticlesAround(entity, Particle.CLOUD, null, 5);
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.5F, 2F);
            }
            location.getWorld().playSound(location, Math.random() < 0.5 ? Sound.ENTITY_PHANTOM_AMBIENT : Sound.ENTITY_PHANTOM_HURT, 0.6F, 2F);
        }
    }

    @Override
    public void onEffectStop() {
        super.onEffectStop();
        ParticleUtils.flash(getLocation());
        location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1, 0.6f);
    }

    @Override
    public int getEffectDuration() {
        return 20 * 30;
    }

    @Override
    public double getEffectRadius() {
        return 7;
    }

}

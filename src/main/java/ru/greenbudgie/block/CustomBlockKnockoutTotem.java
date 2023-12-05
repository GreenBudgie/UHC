package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.greenbudgie.UHC.FightHelper;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockKnockoutTotem extends CustomBlockTotem {

    public CustomBlockKnockoutTotem(Location location, Player owner) {
        super(location, owner);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.knockoutTotem;
    }

    @Override
    public boolean arePlayersImmuneWithNoPVP() {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location.getWorld().playSound(location, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1, 0.5f);
    }

    @Override
    public void produceEffect() {
        if(ticksPassed % 2 == 0) {
            ParticleUtils.createParticlesOutlineSphere(centerLocation, getEffectRadius(), Particle.SMOKE_NORMAL, null, 12);
            ParticleUtils.createParticlesInside(getBlock(), Particle.END_ROD, null, 1);
        }
        if(ticksPassed > 0 && ticksPassed % 20 == 0) {
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
                playerPointer.multiply(1.8);
                Vector currentVelocity = entity.getVelocity();
                currentVelocity.add(playerPointer);
                entity.setVelocity(currentVelocity);
                ParticleUtils.createParticlesAround(entity, Particle.SMOKE_LARGE, null, 5);
                entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_FIRECHARGE_USE, 0.5F, 1.5F);
                if(entity instanceof Player player) {
                    FightHelper.setDamager(player, owner, 70, "убил тотемом");
                }
            }
            ParticleUtils.createParticlesOutlineSphere(centerLocation, getEffectRadius() / 3, Particle.SMOKE_LARGE, null, 20);
            location.getWorld().playSound(location, Sound.BLOCK_ANVIL_LAND, 0.4F, 0.5F);
        }
    }

    @Override
    public void onEffectStop() {
        super.onEffectStop();
        ParticleUtils.createParticlesInside(getBlock(), Particle.SMOKE_LARGE, null, 10);
        location.getWorld().playSound(location, Sound.ITEM_AXE_STRIP, 1, 0.8f);
    }

    @Override
    public int getEffectDuration() {
        return 20 * 45;
    }

    @Override
    public double getEffectRadius() {
        return 8;
    }

}

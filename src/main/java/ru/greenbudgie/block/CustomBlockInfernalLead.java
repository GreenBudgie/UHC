package ru.greenbudgie.block;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;

public class CustomBlockInfernalLead extends CustomBlockItem {

    private Location nearestFortress;
    private Location fortressPointingLocation;

    public CustomBlockInfernalLead(Location location) {
        super(location);
    }

    @Override
    public void onCreate() {
        World world = location.getWorld();
        int radius = (int) (world.getWorldBorder().getSize() / 3);
        nearestFortress = world.locateNearestStructure(location, StructureType.NETHER_FORTRESS, radius, false);
        location.getWorld().playSound(location, Sound.ITEM_TRIDENT_THUNDER, 0.5F, 2F);
        ParticleUtils.createParticlesInsideSphere(centerLocation, 2, Particle.LAVA, null, 20);
    }

    private boolean hasLocatedFortress() {
        return nearestFortress != null;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onUpdate() {
        int[] ticksToProcess = new int[] {20, 35, 45, 50};
        for(int i = 0; i < ticksToProcess.length; i++) {
            int tick = ticksToProcess[i];
            if(tick == ticksPassed) {
                Block block = getBlock();
                RespawnAnchor anchor = (RespawnAnchor) block.getState().getBlockData();

                anchor.setCharges(i + 1);
                float pitch = (i + 1) / 4f + 0.5f;
                location.getWorld().playSound(location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, pitch);

                block.setBlockData(anchor);
                block.getState().update();
            }
        }
        int maxTick = ticksToProcess[ticksToProcess.length - 1];
        if(ticksPassed <= maxTick) {
            double ratio = ticksPassed / (double) maxTick;
            double radius = 1 - ratio + 0.2;
            double height = ratio * 3;
            double angle = ratio * Math.PI * 10;
            Location l = centerLocation.clone().add(
                    Math.sin(angle) * radius,
                    height,
                    Math.cos(angle) * radius);
            ParticleUtils.createParticle(l, Particle.FLAME, null);
        }
        if(ticksPassed == maxTick + 5) {
            if(hasLocatedFortress()) {
                location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 1, 0.5F);
                ParticleUtils.createParticle(centerLocation, Particle.FLASH, null);
                ParticleUtils.createParticlesOutlineSphere(centerLocation, 1.5, Particle.SOUL_FIRE_FLAME, null, 75);
                Vector vector = new Vector(nearestFortress.getX() - centerLocation.getX(), 0,
                        nearestFortress.getZ() - centerLocation.getZ());
                vector.normalize();
                vector.multiply(3);
                fortressPointingLocation = centerLocation.clone().add(vector.getX(), 0, vector.getZ());
            } else {
                location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
                ParticleUtils.createParticlesInsideSphere(centerLocation, 2, Particle.SMOKE_LARGE, null, 20);
                Block block = getBlock();
                RespawnAnchor anchor = (RespawnAnchor) block.getState().getBlockData();
                anchor.setCharges(0);
                block.setBlockData(anchor);
                block.getState().update();
            }
        }
        if(ticksPassed >= maxTick && ticksPassed % 5 == 0 && fortressPointingLocation != null) {
            ParticleUtils.createLine(centerLocation, fortressPointingLocation, Particle.SOUL_FIRE_FLAME, 4, null);
        }
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.infernalLead;
    }

}

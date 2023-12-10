package ru.greenbudgie.block;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.WorldHelper;

import static org.bukkit.ChatColor.*;

public class CustomBlockInfernalLead extends CustomBlockItem {

    private Location nearestFortress;
    private Location fortressPointingLocation;
    private ArmorStand infoStand;

    public CustomBlockInfernalLead(Location location) {
        super(location);
    }

    @Override
    public void onCreate() {
        World world = location.getWorld();
        int radius = (int) (world.getWorldBorder().getSize() / 2);
        StructureSearchResult searchResult = world.locateNearestStructure(
                location,
                StructureType.FORTRESS,
                radius,
                false
        );
        if (searchResult != null) {
            nearestFortress = searchResult.getLocation();
        }
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
            createInfoStand();
        }
        if(ticksPassed >= maxTick && ticksPassed % 5 == 0 && fortressPointingLocation != null) {
            ParticleUtils.createLine(centerLocation, fortressPointingLocation, Particle.SOUL_FIRE_FLAME, 4, null);
        }
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.infernalLead;
    }

    @Override
    public void onRemove() {
        removeInfoStand();
    }

    private void createInfoStand() {
        infoStand = (ArmorStand) location.getWorld().spawnEntity(
                location.clone().add(0.5, 1.2, 0.5),
                EntityType.ARMOR_STAND
        );
        hideStand(infoStand);
        String customName;
        if (nearestFortress == null) {
            customName = DARK_RED + "" + BOLD + "Крепость не найдена!";
        } else {
            int distance = (int) WorldHelper.distanceNoY(nearestFortress, location);
            customName = RED + "" + BOLD + distance;
        }
        infoStand.setCustomName(customName);
    }

    private void removeInfoStand() {
        if (infoStand != null) {
            infoStand.remove();
        }
    }

    private void hideStand(ArmorStand stand) {
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
    }

}

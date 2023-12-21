package ru.greenbudgie.drop;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.drop.marker.AirDropMarker;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import static org.bukkit.ChatColor.*;

public class AirDrop extends Drop {

    private final int MAX_DROP_HEIGHT = 100;
    private double height = 2.5, dropHeight = MAX_DROP_HEIGHT;

    @Override
    public String getName() {
        return AQUA + "" + BOLD + "Аирдроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 600;
    }

    @Override
    public ChatColor getMarkerColor() {
        return AQUA;
    }

    @Override
    public void drop() {
        Item item = location.getWorld().dropItem(
                location,
                Drops.getWeightedDropsList().getRandomElementWeighted().getItem().clone()
        );
        item.setGlowing(true);
        item.setPickupDelay(1);
        item.setMetadata("airdrop", new FixedMetadataValue(UHCPlugin.instance, true));
        location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1F, 0.5F);
        location.getWorld().playSound(location, Sound.BLOCK_WOOL_BREAK, 1.5F, 0.5F);
        ParticleUtils.createParticlesInsideSphere(location, 3, Particle.REDSTONE, Color.WHITE, 40);
        for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
            p.sendTitle(" ", getSpawnMessage(), 10, 40, 20);
            p.sendMessage(getChatDropCoordinatesInfo());
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5F, 1.5F);
        }
        if (currentMarker != null) {
            currentMarker.setDropped();
        }
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getActualMapSize()) / 2 - 10;
        int x = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockX() - size,
                WorldManager.spawnLocation.getBlockX() + size);
        int z = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockZ() - size,
                WorldManager.spawnLocation.getBlockZ() + size);
        int y = WorldManager.getGameMap().getHighestBlockYAt(x, z) + 1;
        return new Location(WorldManager.getGameMap(), x, y, z);
    }

    @Override
    public void setup() {
        super.setup();
        dropHeight = MAX_DROP_HEIGHT;
    }

    @Override
    public void update() {
        if(timer <= 0 && TaskManager.isSecUpdated()) {
            drop();
            setup();
            return;
        }
        if(TaskManager.tick % 2 == 0) {
            height -= 0.05;
            if(height < 0) {
                height = 2.5;
            }
            double radius = 1.8;
            double angle = height * Math.PI * 2;
            Location l = location.clone().add(
                    Math.sin(angle) * radius,
                    height,
                    Math.cos(angle) * radius);
            Location l2 = location.clone().add(
                    Math.sin(angle + Math.PI) * radius,
                    height,
                    Math.cos(angle + Math.PI) * radius);
            ParticleUtils.createParticle(l, Particle.CLOUD, null);
            ParticleUtils.createParticle(l2, Particle.CLOUD, null);
        }
        final int initDrop = 5;
        if(timer < initDrop) {
            dropHeight -= (double) MAX_DROP_HEIGHT / (initDrop * 20.0);
            double radius = 1;
            double angle = (MAX_DROP_HEIGHT / dropHeight) * Math.PI * 20;
            Location l = location.clone().add(Math.sin(angle) * radius, dropHeight - 1, Math.cos(angle) * radius);
            Location l2 = location.clone().add(Math.sin(angle + Math.PI) * radius, dropHeight - 1, Math.cos(angle + Math.PI) * radius);
            ParticleUtils.createParticle(l, Particle.CLOUD, null);
            ParticleUtils.createParticle(l2, Particle.CLOUD, null);
            if(TaskManager.tick % 5 == 0) {
                location.getWorld().playSound(l, Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);
            }
        }
        if(TaskManager.isSecUpdated()) {
            timer--;
        }
    }

    @Override
    public AirDropMarker createMarker() {
        return new AirDropMarker(this);
    }

}

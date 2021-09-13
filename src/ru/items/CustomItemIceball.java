package ru.items;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;
import ru.util.ItemInfo;
import ru.util.ParticleUtils;

public class CustomItemIceball extends RequesterCustomItem implements Listener {

    @Override
    public String getName() {
        return ChatColor.AQUA + "Iceball";
    }

    @Override
    public Material getMaterial() {
        return Material.SNOWBALL;
    }

    @Override
    public ItemInfo getDescription() {
        return new ItemInfo("Ледяной снежок, отталкивающий игроков, но при этом не наносящий урона");
    }

    @Override
    public int getRedstonePrice() {
        return 28;
    }

    @Override
    public int getLapisPrice() {
        return 0;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        item.setAmount(4);
        return item;
    }

    @EventHandler
    public void iceballLand(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Snowball snowball && snowball.getShooter() instanceof Player player && isEquals(snowball.getItem())) {
            Entity hitEntity = event.getHitEntity();
            if(hitEntity instanceof LivingEntity livingEntity && !livingEntity.isInvulnerable() && livingEntity.getNoDamageTicks() == 0) {
                Vector projectileVelocityVector = event.getEntity().getVelocity();
                projectileVelocityVector.normalize();
                Vector currentVelocity = livingEntity.getVelocity();
                currentVelocity.add(projectileVelocityVector);
                livingEntity.setVelocity(currentVelocity);
                livingEntity.damage(0.0000001, player);
            }
        }
    }

}

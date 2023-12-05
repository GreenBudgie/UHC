package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import ru.greenbudgie.util.ItemInfo;

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
        item.setAmount(6);
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

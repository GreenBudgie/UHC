package ru.classes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.UHC.PlayerManager;
import ru.UHC.WorldManager;
import ru.util.MathUtils;

public class ClassDemon extends UHCClass implements Listener {

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Демон";
    }

    @Override
    public String[] getAdvantages() {
        return new String[] {
                "Урон от огня, магмы и лавы снижен в 2 раза",
                "Пиглины дружелюбны",
                "С кварцевой руды падает редстоун, 1-2шт"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "Получаемый урон в обычном мире увеличен на 30% (кроме дезматча)"
        };
    }

    @Override
    public Material getItemToShow() {
        return Material.CRYING_OBSIDIAN;
    }

    @EventHandler
    public void breakQuartz(BlockBreakEvent event) {
        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE && hasClass(event.getPlayer())) {
            int redstoneDropAmount = MathUtils.randomRange(1, 2);
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(),
                    new ItemStack(Material.REDSTONE, redstoneDropAmount));
        }
    }

    @EventHandler
    public void noPiglinAnger(EntityTargetLivingEntityEvent event) {
        if(event.getEntity() instanceof Piglin &&
                event.getTarget() instanceof Player player && hasClass(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player && hasClass(player)) {
            if(event.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                    event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    event.getCause() == EntityDamageEvent.DamageCause.LAVA ||
                    event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                event.setDamage(event.getDamage() * 0.5);
            }
            if(player.getWorld() == WorldManager.getGameMap()) {
                event.setDamage(event.getDamage() * 1.3);
            }
        }
    }

}

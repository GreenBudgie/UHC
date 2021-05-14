package ru.classes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import ru.util.ParticleUtils;

public class ClassArcher extends UHCClass implements Listener {

    @Override
    public String getName() {
        return ChatColor.DARK_PURPLE + "Лучник";
    }

    @Override
    public String[] getAdvantages() {
        return new String[] {
                "Крафтится 8 стрел вместо 4х",
                "Урон от стрел увеличен на 25%"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "Урон в ближнем бою снижен на 25%"
        };
    }

    @Override
    public Material getItemToShow() {
        return Material.BOW;
    }

    @EventHandler
    public void bowShot(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player && e.getBow() != null && e.getBow().getType() == Material.BOW) {
            Player player = (Player) e.getEntity();
            if(hasClass(player)) {
                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 2);
                ParticleUtils.createParticlesAround(player, Particle.SOUL_FIRE_FLAME, null, 10);
            }
        }
    }

}

package ru.classes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.FightHelper;
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.event.GameInitializeEvent;
import ru.event.UHCPlayerDeathEvent;
import ru.items.CustomItems;
import ru.util.MathUtils;
import ru.util.ParticleUtils;

public class ClassNecromancer extends UHCClass {

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Некромант";
    }

    @Override
    public String[] getAdvantages() {
        return new String[] {
                "При убийстве игрока максимально возможное количество здоровья увеличивается на 2 сердца",
                "При смерти любого игрока во время игры регенерируется 1 сердце",
                "При убийстве игрока регенерируется 2 сердца",
                "При убийстве любого моба или игрока получает эффект поглощения урона",
                "Предмет: яйцо для призыва армии зомби и скелетов, атакующих врагов"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "В начале игры максимальное количество здоровья ограничено в 7 сердец"
        };
    }

    @EventHandler
    public void gameInit(GameInitializeEvent event) {
        for(UHCPlayer uhcPlayer : getPlayersWithClass()) {
            uhcPlayer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
        }
    }

    @Override
    public ItemStack[] getStartItems() {
        ItemStack egg = CustomItems.underworldEgg.getItemStack();
        egg.setAmount(2);
        return new ItemStack[] {egg};
    }

    @Override
    public Material getItemToShow() {
        return Material.BONE;
    }

    @EventHandler
    public void handlePlayerDeathRegeneration(UHCPlayerDeathEvent event) {
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        for(UHCPlayer uhcNecromancer : getAliveOnlinePlayersWithClass()) {
            if(uhcNecromancer == uhcPlayer) continue;
            Player necromancer = uhcNecromancer.getPlayer();
            necromancer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
        }
        UHCPlayer uhcKiller = event.getKiller();
        if(uhcKiller != null && uhcKiller.isAliveAndOnline()) {
            Player killer = uhcKiller.getPlayer();
            if(hasClass(killer)) {
                AttributeInstance maxHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                maxHealth.setBaseValue(maxHealth.getBaseValue() + 4);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
            }
        }
    }

    @EventHandler
    public void absorptionOnEntityKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if(!(entity instanceof ArmorStand)) {
            Player killer = entity.getKiller();
            if(killer == null && entity instanceof Player victim) {
                UHCPlayer uhcKiller = FightHelper.getKiller(victim);
                if(uhcKiller != null && uhcKiller.isAliveAndOnline()) killer = uhcKiller.getPlayer();
            }
            if(killer != null && hasClass(killer)) {
                ParticleUtils.createParticlesAround(entity, Particle.SPELL_MOB, Color.RED, 30);
                entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_HOE_TILL, 1F, 0.5F);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 40, 0));
            }
        }
    }

}

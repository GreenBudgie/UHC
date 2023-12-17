package ru.greenbudgie.classes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.FightHelper;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.AfterGameInitializeEvent;
import ru.greenbudgie.event.UHCPlayerDeathEvent;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;

public class ClassNecromancer extends UHCClass {

    private final double MAX_CLASS_HP = 16;

    @Override
    public String getName() {
        return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Некромант";
    }

    @Override
    public ItemInfo[] getAdvantages() {
        return new ItemInfo[] {
                new ItemInfo("При убийстве игрока максимально возможное количество здоровья увеличивается на 2 сердца")
                        .example("На старте игры у тебя 8 сердец. Убийство игрока увеличит это значение до 10 сердец. Однако, их нужно будет отрегенить."),
                new ItemInfo("При смерти любого игрока во время игры регенерируется 1 ед. здоровья"),
                new ItemInfo("При убийстве игрока регенерируется 4 ед. здоровья"),
                new ItemInfo("При убийстве моба или игрока выдается эффект поглощения урона")
                        .extra("Ты получаешь 2 дополнительных сердца на 45 секунд")
                        .note("Работает даже при убийстве мирных мобов"),
                new ItemInfo("Зомби и скелеты дружелюбны к тебе").
                        extra("Распространяется на зомби-пиглинов, визер-скелетов, хасков, стреев и утопленников").
                        note("Артефакты с этих мобов не выпадают. Мобы, заспавненные с помощью яйца некроманта, будут агрессивны.")
        };
    }

    @Override
    public ItemInfo[] getDisadvantages() {
        return new ItemInfo[] {
                new ItemInfo("В начале игры максимальное количество здоровья ограничено в 8 сердец")
        };
    }

    @EventHandler
    public void gameInit(AfterGameInitializeEvent event) {
        for(UHCPlayer uhcPlayer : getPlayersWithClass()) {
            uhcPlayer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(MAX_CLASS_HP);
        }
    }

    @Override
    public ItemStack[] getStartItems() {
        ItemStack egg = CustomItems.underworldEgg.getItemStack();
        egg.setAmount(1);
        return new ItemStack[] {egg};
    }

    @Override
    public Material getItemToShow() {
        return Material.BONE;
    }

    @EventHandler
    public void handlePlayerDeathRegeneration(UHCPlayerDeathEvent event) {
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        for(UHCPlayer uhcNecromancer : getAlivePlayersWithClass()) {
            if(uhcNecromancer == uhcPlayer) continue;
            if(uhcNecromancer.isOnline()) {
                Player necromancer = uhcNecromancer.getPlayer();
                necromancer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0));
            } else {
                uhcNecromancer.addOfflineHealth(1);
            }
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

    public boolean isFriendly(Entity entity) {
        return entity instanceof AbstractSkeleton || entity instanceof Zombie;
    }

    @EventHandler
    public void noEnemyTarget(EntityTargetLivingEntityEvent event) {
        if(event.getTarget() instanceof Player target && hasClass(target) && isFriendly(event.getEntity())) {
            if(!event.getEntity().hasMetadata("necromancer_owner")) {
                event.setCancelled(true);
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
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 45, 0));
            }
        }
    }

}

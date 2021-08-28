package ru.classes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.util.MathUtils;
import ru.util.ParticleUtils;

import java.util.HashMap;
import java.util.Map;

public class ClassBerserk extends BarHolderUHCClass {

    private final Map<UHCPlayer, Double> battleRage = new HashMap<>(); //From 0 to 1
    private final int MAX_MOBS_TO_KILL = 50;
    private final double BATTLE_RAGE_PER_KILL = 1D / MAX_MOBS_TO_KILL; //How much soul flame progress to burn per hit
    private final double MAX_DAMAGE_INCREASE = 1.3;

    @Override
    public String getName() {
        return ChatColor.RED + "" + ChatColor.BOLD + "Берсерк";
    }

    @Override
    public String[] getAdvantages() {
        return new String[] {
                "20 сердец при старте игры",
                "Сопротивление урону на 15 секунд, когда ешь сырое мясо",
                "Шкала Battle Rage: увеличивается урон при убийстве враждебных мобов не из спавнера",
                "Предмет: выдает спешку III на 7 секунд"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "Может носить только кожаную броню",
                "Нельзя атаковать мечами",
                "Эффекты регенерации в два раза слабее"
        };
    }

    /**
     * Gets the player soul flame value, or 0 if not found
     */
    private double getBattleRage(UHCPlayer uhcPlayer) {
        return battleRage.getOrDefault(uhcPlayer, 0D);
    }

    private void setBattleRage(UHCPlayer uhcPlayer, double rage) {
        battleRage.put(uhcPlayer, rage);
    }

    @Override
    public ItemStack[] getStartItems() {

        return new ItemStack[] {};
    }

    @Override
    public Material getItemToShow() {
        return Material.IRON_AXE;
    }

    @Override
    public void onGameInit(UHCPlayer uhcPlayer) {
        super.onGameInit(uhcPlayer);
        uhcPlayer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        UHC.heal(uhcPlayer.getPlayer());
    }

    @Override
    public void onGameStart(UHCPlayer uhcPlayer) {
        super.onGameStart(uhcPlayer);
        if(uhcPlayer.isAliveAndOnline()) {
            updateBattleRage(uhcPlayer);
        }
    }

    @Override
    public void onPlayerRejoin(UHCPlayer uhcPlayer) {
        super.onPlayerRejoin(uhcPlayer);
        if(uhcPlayer.getPlayer() != null) {
            updateBattleRage(uhcPlayer);
        }
    }

    private double getDamageIncrease(UHCPlayer uhcPlayer) {
        return getBattleRage(uhcPlayer) * (MAX_DAMAGE_INCREASE - 1D) + 1;
    }

    private void updateBattleRage(UHCPlayer uhcPlayer) {
        BossBar bar = getBar(uhcPlayer);
        if(uhcPlayer.isAliveAndOnline() && bar != null) {
            double currentValue = getBattleRage(uhcPlayer);
            bar.setProgress(currentValue);
            int percentage = (int) Math.round((getDamageIncrease(uhcPlayer) - 1) * 100);
            bar.setTitle(getBarTitle() + ChatColor.GRAY + " +" + ChatColor.DARK_AQUA + ChatColor.BOLD + percentage + ChatColor.GRAY + "% урона");
        }
    }

    private boolean isHostileMob(LivingEntity entity) {
        return entity instanceof Monster ||
                entity instanceof Slime ||
                entity instanceof Shulker ||
                entity instanceof Ghast ||
                entity instanceof Boss ||
                entity instanceof Hoglin;
    }

    private boolean slimeSizeAllowed(LivingEntity entity) {
        if(entity instanceof Slime slime) {
            return slime.getSize() > 1;
        }
        return true;
    }

    @EventHandler
    public void increaseDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof LivingEntity victim && event.getDamager() instanceof Player attacker && hasClass(attacker)) {
            UHCPlayer uhcAttacker = PlayerManager.asUHCPlayer(attacker);
            if(uhcAttacker != null) {
                double damageIncrease = getDamageIncrease(uhcAttacker);
                event.setDamage(event.getDamage() * damageIncrease);

                double battleRage = getBattleRage(uhcAttacker);
                if(battleRage > 0) {
                    int particlesToShow = (int) (battleRage * 25D);
                    ParticleUtils.createParticlesAround(victim, Particle.REDSTONE, Color.fromRGB(100, 0, 0), particlesToShow);
                    float volume = (float) (battleRage + 0.5);
                    float pitch = 0.65F - (float) (battleRage * 0.15);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, volume, pitch);
                }
            }
        }
    }

    @EventHandler
    public void markNaturalSpawnedMobs(CreatureSpawnEvent event) {
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            LivingEntity entity = event.getEntity();
            entity.setMetadata("natural", new FixedMetadataValue(UHCPlugin.instance, true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void battleRageIncrease(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if(killer != null && hasClass(killer) && isHostileMob(entity) && slimeSizeAllowed(entity) && entity.hasMetadata("natural")) {
            UHCPlayer uhcKiller = PlayerManager.asUHCPlayer(killer);
            if(uhcKiller != null) {
                double battleRage = getBattleRage(uhcKiller);
                setBattleRage(uhcKiller, MathUtils.clamp(battleRage + BATTLE_RAGE_PER_KILL, 0, 1));
                updateBattleRage(uhcKiller);
            }
        }
    }

    @Override
    public String getBarTitle() {
        return ChatColor.RED + "" + ChatColor.BOLD + "Battle Rage";
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SOLID;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.RED;
    }

}

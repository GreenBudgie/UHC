package ru.greenbudgie.classes;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.*;
import ru.greenbudgie.event.GameStartEvent;
import ru.greenbudgie.event.UHCPlayerRejoinEvent;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.ItemInfo;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class ClassDemon extends BarHolderUHCClass {

    private final Map<UHCPlayer, Double> soulFlame = new HashMap<>(); //From 0 to 1
    private final double soulFlameBurn = 1 / 6D; //How much soul flame progress to burn per hit
    private final double soulFramePerBarProtection = 0.1; //Percentage of damage to absorb per one bar

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Демон";
    }

    @Override
    public ItemInfo[] getAdvantages() {
        return new ItemInfo[] {
                new ItemInfo("Огнестойкость на всю игру"),
                new ItemInfo("Пиглины дружелюбны").note("Не распространяется на зомби-пиглинов и брутов"),
                new ItemInfo("С кварцевой руды также падает редстоун").extra("1-2 шт. за руду").note("Чар на удачу не влияет на количество"),
                new ItemInfo("С блейзов падают палки со 100% шансом, а с визер-скелетов падают адские бородавки."),
                new ItemInfo("Шкала Soul Flame, наполняется при получении урона в аду. При получении урона тратится одно деление шкалы и этот урон немного снижается. Если тебя атаковали, то нападающий поджигается.")
                        .extra("За 1 хп полученного урона шкала наполняется на 1/12. Всего у шкалы 6 делений. Одно деление шкалы равно 10% поглощаемого урона. Значит, максимальное поглощение - 60%")
                        .example("Ты получил 6хп урона и наполнил шкалу на 3 заряда. Тебя атаковали на 10хп. Атакующий поджегся, а ты получил всего 7хп урона.")
        };
    }

    @Override
    public ItemInfo[] getDisadvantages() {
        return new ItemInfo[] {
                new ItemInfo("Получаемый урон в обычном мире увеличен на 25%")
                        .note("Во время дезматча урон не увеличивается"),
                new ItemInfo("Темные артефакты не выбиваются в аду")
        };
    }

    @Override
    public void onUpdate(UHCPlayer uhcPlayer) {
        if(TaskManager.isSecUpdated() && (UHC.state.isBeforeDeathmatch() || UHC.state == GameState.DEATHMATCH) && uhcPlayer.isAliveAndOnline()) {
            Player player = uhcPlayer.getPlayer();
            if(!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
            }
        }
    }

    /**
     * Gets the player soul flame value, or 0 if not found
     */
    private double getSoulFlame(UHCPlayer uhcPlayer) {
        return soulFlame.getOrDefault(uhcPlayer, 0D);
    }

    public void setSoulFlame(UHCPlayer uhcPlayer, double flame) {
        soulFlame.put(uhcPlayer, flame);
    }

    private int getSoulFlamePercentageProtection(UHCPlayer uhcPlayer) {
        return (int) (getSoulFlameRealProtection(uhcPlayer) * 100);
    }

    private double getSoulFlameRealProtection(UHCPlayer uhcPlayer) {
        int barsFilled = (int) ((1 / soulFlameBurn) * getSoulFlame(uhcPlayer));
        return barsFilled * soulFramePerBarProtection;
    }

    @Override
    public ItemStack[] getStartItems() {
        ItemStack totem = CustomItems.infernalTotem.getItemStack();
        totem.setAmount(3);
        return new ItemStack[] {totem};
    }

    @Override
    public Material getItemToShow() {
        return Material.WEEPING_VINES;
    }

    @EventHandler
    @Override
    public void onGameStart(GameStartEvent event) {
        super.onGameStart(event);
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            updateSoulFlame(uhcPlayer, 0);
        }
    }

    @Override
    public void onPlayerRejoin(UHCPlayerRejoinEvent event) {
        super.onPlayerRejoin(event);
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        if(uhcPlayer.getPlayer() != null) {
            updateSoulFlame(uhcPlayer, getSoulFlame(uhcPlayer));
        }
    }

    public void updateSoulFlame(UHCPlayer uhcPlayer, double previousValue) {
        BossBar bar = getBar(uhcPlayer);
        if(uhcPlayer.isAliveAndOnline() && bar != null) {
            double currentValue = getSoulFlame(uhcPlayer);
            for(double i = 0; i <= 1; i += soulFlameBurn) {
                if(currentValue >= i && previousValue < i) {
                    ParticleUtils.createParticlesOutlineSphere(uhcPlayer.getLocation(), 2.5, Particle.FLAME, null, 50);
                    uhcPlayer.getLocation().getWorld().playSound(uhcPlayer.getLocation(), Sound.ITEM_TOTEM_USE, 0.7f, 1.7F);
                    break;
                }
            }
            bar.setProgress(currentValue);
            bar.setTitle(getBarTitle() + ChatColor.GRAY + " -" +
                    ChatColor.GOLD + ChatColor.BOLD + getSoulFlamePercentageProtection(uhcPlayer) +
                    ChatColor.GRAY + "% " +
                    ChatColor.GOLD + "урона");
        }
    }


    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if(killer != null && hasClass(killer)) {
            if(entity instanceof Blaze) {
                if(event.getDrops().stream().noneMatch(item -> item.getType() == Material.BLAZE_ROD))
                    event.getDrops().add(new ItemStack(Material.BLAZE_ROD));
            }
            if(entity instanceof WitherSkeleton) {
                event.getDrops().add(new ItemStack(Material.NETHER_WART));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void burnAttacker(EntityDamageByEntityEvent event) {
        LivingEntity attacker = FightHelper.getDamagerOrShooter(event);
        if(!event.isCancelled() && event.getEntity() instanceof Player victim && attacker != null && hasClass(victim)) {
            UHCPlayer uhcVictim = PlayerManager.asUHCPlayer(victim);
            double flame = getSoulFlame(uhcVictim);
            if(uhcVictim != null && attacker.getFireTicks() <= 0 && flame >= soulFlameBurn) {
                ParticleUtils.createParticlesAround(victim, Particle.FLAME, null, 10);
                ParticleUtils.createParticlesAround(attacker, Particle.FLAME, null, 10);
                attacker.getWorld().playSound(attacker.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
                attacker.setFireTicks(30 + (int) (flame * 40));
            }
        }
    }

    @EventHandler
    public void breakQuartz(BlockBreakEvent event) {
        if(event.getBlock().getType() == Material.NETHER_QUARTZ_ORE && event.isDropItems() && hasClass(event.getPlayer())) {
            ParticleUtils.createParticlesInside(event.getBlock(), Particle.REDSTONE, Color.fromRGB(200, 0, 0), 9);
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

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if(!event.isCancelled() && event.getFinalDamage() > 0 && event.getEntity() instanceof Player player && hasClass(player)) {
            UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
            double flame = getSoulFlame(uhcPlayer);
            double protection = getSoulFlameRealProtection(uhcPlayer);
            event.setDamage(event.getDamage() - (event.getDamage() * protection));
            setSoulFlame(uhcPlayer, MathUtils.clamp(flame - soulFlameBurn, 0, 1));
            updateSoulFlame(uhcPlayer, flame);
            if(player.getWorld() == WorldManager.getGameMap()) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 0.8f, 0.5f);
                event.setDamage(event.getDamage() * 1.25);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void damageMonitor(EntityDamageEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof Player player && hasClass(player)) {
            UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
            if(uhcPlayer != null && player.getWorld() == WorldManager.getGameMapNether()) {
                double finalDamage = event.getFinalDamage();
                double currentFlame = getSoulFlame(uhcPlayer);
                setSoulFlame(uhcPlayer, MathUtils.clamp(currentFlame + finalDamage / 12, 0, 1));
                updateSoulFlame(uhcPlayer, currentFlame);
            }
        }
    }

    @Override
    public String getBarTitle() {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Soul Flame";
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SEGMENTED_6;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.RED;
    }

}

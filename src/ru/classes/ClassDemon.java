package ru.classes;

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
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.event.GameStartEvent;
import ru.event.UHCPlayerRejoinEvent;
import ru.items.CustomItems;
import ru.util.ItemInfo;
import ru.util.MathUtils;
import ru.util.ParticleUtils;

import java.util.HashMap;
import java.util.Map;

public class ClassDemon extends BarHolderUHCClass {

    private final Map<UHCPlayer, Double> soulFlame = new HashMap<>(); //From 0 to 1
    private final double soulFlameBurn = 1 / 6D; //How much soul flame progress to burn per hit

    @Override
    public String getName() {
        return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Демон";
    }

    @Override
    public ItemInfo[] getAdvantages() {
        return new ItemInfo[] {
                new ItemInfo("Урон от огня, магмы и лавы снижен в 2 раза"),
                new ItemInfo("Пиглины дружелюбны").note("Не распространяется на зомби-пиглинов и брутов"),
                new ItemInfo("С кварцевой руды также падает редстоун").extra("1-2 шт. за руду").note("Чар на удачу не влияет на количество"),
                new ItemInfo("Увеличен шанс дропа предметов с адских мобов. Также, с визер-скелетов падают адские бородавки.")
                        .extra("С блейзов падают палки со 100% шансом. С адских слизней падает слизь с 50% шансом."),
                new ItemInfo("Шкала Soul Flame, наполняется при получении урона от огня. Когда тебя атакуют, тратится одно деление шкалы и нападающий поджигается.")
                        .extra("1 ед. урона = 1/12 шкалы; Значит, шкала наполнится полностью при получении урона в 12 хп.")
                        .note("К урону от огня относится также урон от магмы и лавы. Поджигаются только атакующие игроки, на мобов не работает.")
                        .example("Ты получил 6хп урона от лавы и наполнил шкалу на 3 заряда. Тебя атаковали. Атакующий поджегся, у тебя осталось 2 заряда.")
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

    /**
     * Gets the player soul flame value, or 0 if not found
     */
    private double getSoulFlame(UHCPlayer uhcPlayer) {
        return soulFlame.getOrDefault(uhcPlayer, 0D);
    }

    private void setSoulFlame(UHCPlayer uhcPlayer, double flame) {
        soulFlame.put(uhcPlayer, flame);
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

    private void updateSoulFlame(UHCPlayer uhcPlayer, double previousValue) {
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
            int usesRemaining = (int) ((1 / soulFlameBurn) * currentValue);
            bar.setTitle(getBarTitle() + ChatColor.GRAY + " x" + ChatColor.GOLD + ChatColor.BOLD + usesRemaining);
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
            if(entity instanceof MagmaCube) {
                if(event.getDrops().stream().noneMatch(item -> item.getType() == Material.MAGMA_CREAM) && MathUtils.chance(50))
                    event.getDrops().add(new ItemStack(Material.MAGMA_CREAM));
            }
            if(entity instanceof WitherSkeleton) {
                event.getDrops().add(new ItemStack(Material.NETHER_WART));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void burnAttacker(EntityDamageByEntityEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker && hasClass(victim)) {
            UHCPlayer uhcVictim = PlayerManager.asUHCPlayer(victim);
            double flame = getSoulFlame(uhcVictim);
            if(uhcVictim != null && attacker.getFireTicks() == 0 & flame >= soulFlameBurn) {
                ParticleUtils.createParticlesAround(victim, Particle.FLAME, null, 10);
                ParticleUtils.createParticlesAround(attacker, Particle.FLAME, null, 10);
                attacker.getWorld().playSound(attacker.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
                attacker.setFireTicks(60 + (int) (flame * 80));
                setSoulFlame(uhcVictim, MathUtils.clamp(flame - soulFlameBurn, 0, 1));
                updateSoulFlame(uhcVictim, flame);
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

    private boolean isFireDamage(EntityDamageEvent.DamageCause cause) {
        return cause == EntityDamageEvent.DamageCause.FIRE ||
                cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                cause == EntityDamageEvent.DamageCause.LAVA ||
                cause == EntityDamageEvent.DamageCause.HOT_FLOOR;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damage(EntityDamageEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof Player player && hasClass(player)) {
            if(isFireDamage(event.getCause())) {
                event.setDamage(event.getDamage() * 0.5);
            }
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
            if(uhcPlayer != null && isFireDamage(event.getCause())) {
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

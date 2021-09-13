package ru.classes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.event.GameInitializeEvent;
import ru.event.GameStartEvent;
import ru.event.UHCPlayerRejoinEvent;
import ru.items.CustomItems;
import ru.main.UHCPlugin;
import ru.util.ItemInfo;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.TaskManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ClassBerserk extends BarHolderUHCClass implements RecipeHolderClass {

    private final Map<UHCPlayer, Double> battleRage = new HashMap<>(); //From 0 to 1
    private final int MAX_MOBS_TO_KILL = 50;
    private final double BATTLE_RAGE_PER_KILL = 1D / MAX_MOBS_TO_KILL;
    private final double MAX_DAMAGE_INCREASE = 1.3;

    @Override
    public String getName() {
        return ChatColor.RED + "" + ChatColor.BOLD + "Берсерк";
    }

    @Override
    public ItemInfo[] getAdvantages() {
        return new ItemInfo[] {
                new ItemInfo("20 сердец при старте игры"),
                new ItemInfo("Гнилая плоть и сырое мясо безвредны"),
                new ItemInfo("Ты начинаешь быстрее атаковать, когда ешь сырое мясо")
                        .extra("Выдается спешка III на 10 секунд, что увеличивает скорость атаки на 30%"),
                new ItemInfo("Шкала Battle Rage: увеличивается урон при убийстве враждебных мобов")
                        .note("Не работает на мобов из спавнера")
                        .extra("Максимальный буст урона равен 30%, для этого нужно убить 50 мобов")
                        .explanation("Буст урона сохраняется на всю игру и никуда не пропадает со временем")
        };
    }

    @Override
    public ItemInfo[] getDisadvantages() {
        return new ItemInfo[] {
                new ItemInfo("Может носить только кожаную броню, которая также может быть скрафчена из гнилой плоти")
                        .note("Другую броню надеть можно, но при первом же получении урона она мгновенно сломается"),
                new ItemInfo("Нельзя атаковать мечами"),
                new ItemInfo("Эффекты регенерации в полтора раза слабее")
                        .example("Золотые яблоки будут восстанавливать 3 хп вместо 4-х")
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
        ItemStack shard = CustomItems.ancientShard.getItemStack();
        shard.setAmount(3);
        return new ItemStack[] {shard};
    }

    @Override
    public Material getItemToShow() {
        return Material.IRON_AXE;
    }

    @EventHandler
    public void gameInit(GameInitializeEvent event) {
        for(UHCPlayer uhcPlayer : getPlayersWithClass()) {
            uhcPlayer.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            UHC.heal(uhcPlayer.getPlayer());
        }
    }

    @EventHandler
    @Override
    public void onGameStart(GameStartEvent event) {
        super.onGameStart(event);
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            updateBattleRage(uhcPlayer);
        }
    }

    @Override
    @EventHandler
    public void onPlayerRejoin(UHCPlayerRejoinEvent event) {
        super.onPlayerRejoin(event);
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
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

    private final Material[] rawMeat = new Material[] {
            Material.PORKCHOP, Material.BEEF, Material.CHICKEN,
            Material.RABBIT, Material.MUTTON, Material.ROTTEN_FLESH,
            Material.COD, Material.SALMON, Material.TROPICAL_FISH};

    @EventHandler
    public void rawMeatBonus(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if(hasClass(player)) {
            ItemStack item = event.getItem();
            if(Stream.of(rawMeat).anyMatch(meat -> item.getType() == meat)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 10, 2));
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 0.5F);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 1, 0.5F);
                TaskManager.invokeLater(() -> {
                    player.removePotionEffect(PotionEffectType.HUNGER);
                });
            }
        }
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

    private boolean immunity = false;

    @EventHandler
    public void reduceRegen(EntityPotionEffectEvent event) {
        if(immunity) {
            immunity = false;
            return;
        }
        if(event.getCause() != EntityPotionEffectEvent.Cause.COMMAND) {
            if(event.getAction() == EntityPotionEffectEvent.Action.ADDED || event.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
                if(event.getEntity() instanceof Player player && hasClass(player)) {
                    PotionEffect effect = event.getNewEffect();
                    if(effect != null && effect.getType().equals(PotionEffectType.REGENERATION)) {
                        event.setCancelled(true);
                        PotionEffect halfEffect = new PotionEffect(effect.getType(), (int) (effect.getDuration() / 1.3),
                                effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
                        immunity = true;
                        player.addPotionEffect(halfEffect);
                    }
                }
            }
        }
    }

    private final Material[] allowedArmor = new Material[] {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    };

    @EventHandler
    public void armorBreakOnDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player player && hasClass(player)) {
            ItemStack[] armor = player.getInventory().getArmorContents();
            for(ItemStack armorItem : armor) {
                if(armorItem == null || armorItem.getType() == Material.AIR) continue;
                if(Stream.of(allowedArmor).noneMatch(allowedArmor -> armorItem.getType() == allowedArmor)) {
                    armorItem.setAmount(0);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.5F);
                    ParticleUtils.createParticlesAround(player, Particle.SMOKE_LARGE, null, 4);
                }
            }
        }
    }

    private final Material[] swords = new Material[] {
            Material.WOODEN_SWORD, Material.STONE_SWORD,
            Material.IRON_SWORD, Material.DIAMOND_SWORD,
            Material.GOLDEN_SWORD, Material.NETHERITE_SWORD };

    @EventHandler(priority = EventPriority.LOW)
    public void noSwordAttack(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player attacker && hasClass(attacker)) {
            ItemStack item = attacker.getInventory().getItemInMainHand();
            if(Stream.of(swords).anyMatch(sword -> item.getType() == sword)) {
                item.setAmount(0);
                attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 0.7F);
                event.setCancelled(true);
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
                Sound sound = Math.random() < 0.5 ? Sound.ENTITY_VINDICATOR_AMBIENT : Sound.ENTITY_VINDICATOR_CELEBRATE;
                killer.getWorld().playSound(killer.getLocation(), sound, 1, 0.5F);
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

    private ItemStack armorWithColor(Material armor) {
        ItemStack item = new ItemStack(armor);
        ItemMeta meta = item.getItemMeta();
        if(meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(Color.fromRGB(84, 79, 49));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void registerRecipe(ShapedRecipe recipe, String... shape) {
        recipe.shape(shape);
        recipe.setIngredient('l', Material.ROTTEN_FLESH);
        recipe.setIngredient('-', Material.AIR);
    }

    @Override
    public Recipe[] getClassRecipes() {
        ShapedRecipe helmet = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_helmet"),
                armorWithColor(Material.LEATHER_HELMET));
        registerRecipe(helmet, "lll", "l-l", "---");

        ShapedRecipe helmet2 = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_helmet2"),
                armorWithColor(Material.LEATHER_HELMET));
        registerRecipe(helmet2, "---", "lll", "l-l");

        ShapedRecipe chestplate = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_chestplate"),
                armorWithColor(Material.LEATHER_CHESTPLATE));
        registerRecipe(chestplate, "l-l", "lll", "lll");

        ShapedRecipe leggings = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_leggings"),
                armorWithColor(Material.LEATHER_LEGGINGS));
        registerRecipe(leggings, "lll", "l-l", "l-l");

        ShapedRecipe boots = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_boots"),
                armorWithColor(Material.LEATHER_BOOTS));
        registerRecipe(boots, "---", "l-l", "l-l");

        ShapedRecipe boots2 = new ShapedRecipe(
                new NamespacedKey(UHCPlugin.instance, "berserk_boots2"),
                armorWithColor(Material.LEATHER_BOOTS));
        registerRecipe(boots2, "l-l", "l-l", "---");
        
        return new Recipe[] {helmet, helmet2, chestplate, leggings, boots, boots2};
    }
}

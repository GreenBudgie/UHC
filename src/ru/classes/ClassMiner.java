package ru.classes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.GameState;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.event.*;
import ru.items.CustomItems;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.TaskManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ClassMiner extends BarHolderUHCClass {

    //Fatigue is now a value between 0 and 1, where 1 is the start value
    private Map<UHCPlayer, Double> fatigue = new HashMap<>();

    private final float PLAYER_DEFAULT_WALK_SPEED = 0.2F;
    private final double MIN_SPEED_REDUCTION = 1;
    private final double MAX_SPEED_REDUCTION = 0.8;
    private final double MIN_DAMAGE_REDUCTION = 0.9;
    private final double MAX_DAMAGE_REDUCTION = 0.7;
    private final int NEEDED_COPPER = 50;
    private final double BONUS_PER_COPPER = 1D / NEEDED_COPPER;

    private Material[] ORES = new Material[] {
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS
    };
    private Material[] INSTRUMENTS = new Material[] {
            Material.WOODEN_PICKAXE,
            Material.WOODEN_SHOVEL,
            Material.WOODEN_AXE,
            Material.WOODEN_HOE,
            Material.STONE_PICKAXE,
            Material.STONE_SHOVEL,
            Material.STONE_AXE,
            Material.STONE_HOE,
            Material.IRON_PICKAXE,
            Material.IRON_SHOVEL,
            Material.IRON_AXE,
            Material.IRON_HOE,
            Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SHOVEL,
            Material.DIAMOND_AXE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_PICKAXE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_AXE,
            Material.NETHERITE_HOE,
            Material.GOLDEN_PICKAXE,
            Material.GOLDEN_SHOVEL,
            Material.GOLDEN_AXE,
            Material.GOLDEN_HOE,
            Material.SHEARS,
            Material.FLINT_AND_STEEL
    };

    @Override
    public String getName() {
        return ChatColor.GRAY + "" + ChatColor.BOLD + "Шахтер";
    }

    @Override
    public String[] getAdvantages() {
        return new String[] {
                "Спешка I на всю игру",
                "Все инструменты более прочные",
                "Больше опыта при добыче руды",
                "Предмет: маяк, указывающий на расположение алмазов либо древних обломков"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "При вскапывании любой руды ты начинаешь светиться",
                "Шкала Miner Fatigue (усталость). Ты наносишь меньше урона и медленнее передвигаешься. Добыча меди улучшает эти характеристики."
        };
    }

    @Override
    public void onUpdate(UHCPlayer uhcPlayer) {
        if(TaskManager.isSecUpdated() && (UHC.state.isInGame() || UHC.state == GameState.DEATHMATCH) && uhcPlayer.isAliveAndOnline()) {
            Player player = uhcPlayer.getPlayer();
            if(!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0));
            }
        }
    }

    @EventHandler
    @Override
    public void onGameStart(GameStartEvent event) {
        super.onGameStart(event);
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            updateFatigueEffects(uhcPlayer);
        }
    }

    @EventHandler
    @Override
    public void onPlayerLeave(UHCPlayerLeaveEvent event) {
        super.onPlayerLeave(event);
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        if(uhcPlayer.getPlayer() != null) {
            uhcPlayer.getPlayer().setWalkSpeed(PLAYER_DEFAULT_WALK_SPEED);
        }
    }

    @EventHandler
    @Override
    public void onPlayerDeath(UHCPlayerDeathEvent event) {
        super.onPlayerDeath(event);
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        if(uhcPlayer.getPlayer() != null) {
            uhcPlayer.getPlayer().setWalkSpeed(PLAYER_DEFAULT_WALK_SPEED);
        }
    }

    @EventHandler
    @Override
    public void onPlayerRejoin(UHCPlayerRejoinEvent event) {
        super.onPlayerRejoin(event);
        UHCPlayer uhcPlayer = event.getUHCPlayer();
        if(uhcPlayer.getPlayer() != null) {
            updateFatigueEffects(uhcPlayer);
        }
    }

    @EventHandler
    @Override
    public void onGameEnd(GameEndEvent event) {
        super.onGameEnd(event);
        for(UHCPlayer uhcPlayer : getAliveOnlinePlayersWithClass()) {
            if(uhcPlayer.getPlayer() != null) {
                uhcPlayer.getPlayer().setWalkSpeed(PLAYER_DEFAULT_WALK_SPEED);
            }
        }
    }

    @Override
    public ItemStack[] getStartItems() {
        ItemStack totem = CustomItems.terraTracer.getItemStack();
        totem.setAmount(4);
        return new ItemStack[] {totem};
    }

    @Override
    public Material getItemToShow() {
        return Material.DIAMOND_PICKAXE;
    }

    private double getFatigue(UHCPlayer uhcPlayer) {
        return fatigue.getOrDefault(uhcPlayer, 1D);
    }

    private void setFatigue(UHCPlayer uhcPlayer, double fatigue) {
        this.fatigue.put(uhcPlayer, fatigue);
    }

    private double getDamageReduction(UHCPlayer uhcPlayer) {
        double currentFatigue = getFatigue(uhcPlayer);
        return (MIN_DAMAGE_REDUCTION - MAX_DAMAGE_REDUCTION) * (1 - currentFatigue) + MAX_DAMAGE_REDUCTION;
    }

    private double getSpeedReduction(UHCPlayer uhcPlayer) {
        double currentFatigue = getFatigue(uhcPlayer);
        double halfFatigue = MathUtils.clamp((currentFatigue - 0.5) * 2, 0, 1);
        return (MIN_SPEED_REDUCTION - MAX_SPEED_REDUCTION) * (1 - halfFatigue) + MAX_SPEED_REDUCTION;
    }

    private void updateFatigueEffects(UHCPlayer uhcPlayer) {
        BossBar bar = getBar(uhcPlayer);
        if(uhcPlayer.isAliveAndOnline() && bar != null) {
            double currentFatigue = getFatigue(uhcPlayer);
            bar.setProgress(currentFatigue);
            double speedReduction = getSpeedReduction(uhcPlayer);
            int damagePercentage = (int) Math.round(((1 - getDamageReduction(uhcPlayer)) * 100));
            int speedPercentage = (int) Math.round(((1 - speedReduction) * 100));
            bar.setTitle(getBarTitle() +
                    ChatColor.GRAY + " -" + ChatColor.GOLD + ChatColor.BOLD + damagePercentage + ChatColor.GRAY + "% урона" +
                    ChatColor.DARK_GRAY + ", " +
                    ChatColor.GRAY + "-" + ChatColor.DARK_AQUA + ChatColor.BOLD + speedPercentage + ChatColor.GRAY + "% скорости");
            Player player = uhcPlayer.getPlayer();
            player.setWalkSpeed((float) (PLAYER_DEFAULT_WALK_SPEED * speedReduction));
        }
    }

    @EventHandler
    public void reduceDamage(EntityDamageByEntityEvent event) {
        if(!event.isCancelled() && event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player attacker && hasClass(attacker)) {
            UHCPlayer uhcAttacker = PlayerManager.asUHCPlayer(attacker);
            if(uhcAttacker != null) {
                double reduction = getDamageReduction(uhcAttacker);
                event.setDamage(event.getDamage() * reduction);
            }
        }
    }

    @EventHandler
    public void oreMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(hasClass(player) && (block.getType() == Material.COPPER_ORE || block.getType() == Material.DEEPSLATE_COPPER_ORE)) {
            event.setDropItems(false);
            UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
            if(uhcPlayer != null) {
                double currentFatigue = getFatigue(uhcPlayer);
                setFatigue(uhcPlayer, MathUtils.clamp(currentFatigue - BONUS_PER_COPPER, 0, 1));
                updateFatigueEffects(uhcPlayer);
            }
        }
        if(hasClass(player) && Stream.of(ORES).anyMatch(type -> type == block.getType())) {
            event.setExpToDrop((int) (event.getExpToDrop() * 1.5));
            ParticleUtils.createParticlesInside(block, Particle.SPELL_MOB, Color.WHITE, 5);
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.5f, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 0));
        }
    }

    @EventHandler
    public void makeItemMoreDurable(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        if(hasClass(player) && Stream.of(INSTRUMENTS).anyMatch(type -> type == event.getItem().getType())) {
            if(Math.random() < 0.5) event.setCancelled(true);
        }
    }

    @Override
    public String getBarTitle() {
        return ChatColor.GRAY + "" + ChatColor.BOLD + "Miner Fatigue";
    }

    @Override
    public BarStyle getBarStyle() {
        return BarStyle.SOLID;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.WHITE;
    }
}

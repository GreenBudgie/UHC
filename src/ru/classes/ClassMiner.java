package ru.classes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.items.CustomItems;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.TaskManager;

import java.util.stream.Stream;

public class ClassMiner extends UHCClass {

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
                "Предмет: маяк, указывающий на расположение алмазов либо древних обломков"
        };
    }

    @Override
    public String[] getDisadvantages() {
        return new String[] {
                "При вскапывании любой руды ты начинаешь светиться"
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

    @EventHandler
    public void glowOnOreMine(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if(hasClass(player) && Stream.of(ORES).anyMatch(type -> type == block.getType())) {
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

}

package ru.greenbudgie.items;

import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.item.ItemInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class CustomItemLaserCutter extends RequesterCustomItem implements Listener {

	private static final int LASER_BEAM_DISTANCE = 8;
	private static final Set<Material> FUEL = Sets.newHashSet(Material.COAL, Material.CHARCOAL);
    private static final Material ACCELERATOR = Material.COPPER_INGOT;

    private static final int MAX_BURN_COOLDOWN = 60 * 20;
    private static final int ACCELERATE_TICKS = 30 * 20;
    private static final int MAX_USE_DELAY_TICKS_DEFAULT = 6;
    private static final int MAX_USE_DELAY_TICKS_ACCELERATED = 3;

    private static final Sound BURN_SOUND = Sound.ITEM_FIRECHARGE_USE;
    private static final Sound LASER_SOUND = Sound.BLOCK_FIRE_AMBIENT;
    private static final Sound ACCELERATE_SOUND = Sound.BLOCK_RESPAWN_ANCHOR_CHARGE;

    private static final float ARM_DISTANCE = 0.75F;
    private static final double ARM_ANGLE = -Math.PI / 4;
    private static final float ARM_HEIGHT_RELATIVE_TO_EYES = -0.7F;
    private static final float PARTICLE_SPREAD = 0.15F;
    private static final double MIN_PARTICLE_DENSITY = 0.5;
    private static final double MAX_PARTICLE_DENSITY = 1;
    private static final Particle DEFAULT_LASER_PARTICLE = Particle.SMALL_FLAME;
    private static final Particle ACCELERATED_LASER_PARTICLE = Particle.SOUL_FIRE_FLAME;

	private static final Set<Material> allowedMaterials = Sets.newHashSet(
			Material.STONE,
			Material.COBBLESTONE,
			Material.COBBLED_DEEPSLATE,
			Material.ANDESITE,
			Material.DIORITE,
			Material.GRANITE,
			Material.TUFF,
			Material.DEEPSLATE,
			Material.NETHERRACK,
			Material.BLACKSTONE,
			Material.BASALT,
            Material.GRAVEL,
            Material.SANDSTONE
    );

    private final Map<Player, Integer> useDelays = new HashMap<>();

	public String getName() {
		return GOLD + "" + BOLD + "Laser Cutter";
	}

	public Material getMaterial() {
		return Material.AMETHYST_SHARD;
	}

    @Override
    public void update() {
        for (Player player : useDelays.keySet()) {
            updateUseDelay(player);
        }
    }

    @Override
	public void onUseRight(Player player, ItemStack item, PlayerInteractEvent e) {
        laserCut(player, e.getHand());
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Генерирует лазерный луч и позволяет крайне быстро уничтожать " +
				"любую каменную породу. Руды оставляет нетронутыми. Для работы необходим уголь, обычный или древесный.")
				.extra("Сжигает уголь куждую минуту работы. Также, скорость добычи может быть ускорена за счет " +
						"проводника - меди. Если она есть в инвентаре, то каждые 30 секунд будет потрачена " +
						"1 единица, а скорость добычи ускорена в два раза на это время! " +
                        "Радиус действия - 8 блоков.")
				.note("Лучше использовать без предметов (щита или факелов) в другой руке! " +
                        "К камню также относится адский камень, сланец, андезит, диорит и т.д.");
	}

	@Override
	public int getRedstonePrice() {
		return 80;
	}

	@Override
	public int getLapisPrice() {
		return 32;
	}

    private void laserCut(Player player, EquipmentSlot hand) {
        boolean isUsingNow = isUsingNow(player);
        boolean isBurningFuel = isBurningFuel(player);
        ItemStack fuel = null;
        if (!isUsingNow && !isBurningFuel) {
            fuel = getAnyFuel(player);
            if (fuel == null) {
                return;
            }
        }

        RayTraceResult result = player.rayTraceBlocks(LASER_BEAM_DISTANCE, FluidCollisionMode.NEVER);
        Block hitBlock = Optional.ofNullable(result).map(RayTraceResult::getHitBlock).orElse(null);

        if (isUsingNow) {
            playUseEffect(player, hand, hitBlock);
            return;
        }

        if (!isBurningFuel) {
            burnFuel(player, fuel);
        }
        if (!isAccelerated(player)) {
            tryAccelerate(player);
        }
        playUseEffect(player, hand, hitBlock);
        setMaxUseDelay(player);

        if (result == null) {
            return;
        }
        Block block = result.getHitBlock();
        if (block == null) {
            return;
        }
        if (!allowedMaterials.contains(block.getType())) {
            return;
        }
        breakBlock(player, block);
    }

    private void breakBlock(Player player, Block block) {
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(blockBreakEvent);
        if (blockBreakEvent.isCancelled()) {
            return;
        }
        player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        block.breakNaturally();
        ParticleUtils.createParticlesInside(block, Particle.SMOKE_NORMAL, null, 5);
    }

    private boolean isAccelerated(Player player) {
        return player.getCooldown(ACCELERATOR) > 0;
    }

    private void tryAccelerate(Player player) {
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (item.getType() != ACCELERATOR) {
                continue;
            }
            player.getWorld().playSound(
                    player.getLocation(),
                    ACCELERATE_SOUND,
                    1F,
                    2F
            );
            player.setCooldown(ACCELERATOR, ACCELERATE_TICKS);
            item.setAmount(item.getAmount() - 1);
            return;
        }
    }

    private boolean isBurningFuel(Player player) {
        for (Material fuel : FUEL) {
            if (player.getCooldown(fuel) > 0) {
                return true;
            }
        }
        return false;
    }

    private void burnFuel(Player player, @Nonnull ItemStack fuel) {
        player.getWorld().playSound(
                player.getLocation(),
                BURN_SOUND,
                1F,
                1F
        );
        for (Material fuelMaterial : FUEL) {
            player.setCooldown(fuelMaterial, MAX_BURN_COOLDOWN);
        }
        fuel.setAmount(fuel.getAmount() - 1);
    }

    @Nullable
    private ItemStack getAnyFuel(Player player) {
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (FUEL.contains(item.getType())) {
                return item;
            }
        }
        return null;
    }

    private void updateUseDelay(Player player) {
        int useDelayTicks = getUseDelayTicks(player);
        if (useDelayTicks <= 0) {
            useDelays.remove(player);
            return;
        }
        useDelays.put(player, useDelayTicks - 1);
    }

    private void setMaxUseDelay(Player player) {
        useDelays.put(player, getMaxUseDelayTicks(player));
    }

    private boolean isUsingNow(Player player) {
        return useDelays.containsKey(player);
    }

    private int getUseDelayTicks(Player player) {
        return useDelays.getOrDefault(player, getMaxUseDelayTicks(player));
    }

    private int getMaxUseDelayTicks(Player player) {
        if (isAccelerated(player)) {
            return MAX_USE_DELAY_TICKS_ACCELERATED;
        }
        return MAX_USE_DELAY_TICKS_DEFAULT;
    }

    private void playUseEffect(Player player, EquipmentSlot hand, @Nullable Block pointingBlock) {
        Location playerEyeLocation = player.getEyeLocation();
        double maxLaserBeamDistance;
        if (pointingBlock == null) {
            maxLaserBeamDistance = LASER_BEAM_DISTANCE;
        } else {
            maxLaserBeamDistance = playerEyeLocation.distance(pointingBlock.getLocation());
        }
        Vector lookVector = playerEyeLocation.getDirection();
        Vector lookVectorNoHeight = lookVector.clone().setY(0).normalize();
        double armAngle = hand == EquipmentSlot.HAND ? ARM_ANGLE : -ARM_ANGLE;
        Vector armVector = lookVectorNoHeight.multiply(ARM_DISTANCE).rotateAroundY(armAngle);
        Location armLocation = playerEyeLocation.clone()
                .add(armVector)
                .add(0, ARM_HEIGHT_RELATIVE_TO_EYES, 0);
        Location pointingLocation = playerEyeLocation.clone().add(lookVector.multiply(maxLaserBeamDistance));
        ParticleUtils.createLine(
                armLocation.add(getRandomParticleSpreadVector()),
                pointingLocation,
                isAccelerated(player) ? ACCELERATED_LASER_PARTICLE : DEFAULT_LASER_PARTICLE,
                getRandomParticleDensity(),
                null
        );
        player.getWorld().playSound(
                playerEyeLocation,
                LASER_SOUND,
                1F,
                (float) MathUtils.randomRangeDouble(1.7, 2)
        );
    }

    private double getRandomParticleDensity() {
        return MathUtils.randomRangeDouble(MIN_PARTICLE_DENSITY, MAX_PARTICLE_DENSITY);
    }

    private Vector getRandomParticleSpreadVector() {
        return new Vector(
                getRandomParticleSpread(),
                getRandomParticleSpread(),
                getRandomParticleSpread()
        );
    }

    private double getRandomParticleSpread() {
        return MathUtils.randomRangeDouble(-PARTICLE_SPREAD, PARTICLE_SPREAD);
    }

}

package ru.greenbudgie.items;

import com.google.common.collect.Sets;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.item.ItemInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class CustomItemLaserCutter extends RequesterCustomItem implements Listener {

	private static final int LASER_BEAM_DISTANCE = 8;
    private static final Material FUEL = Material.COPPER_INGOT;

    private static final int MAX_BURN_COOLDOWN = 30 * 20;
    private static final int MAX_USE_DELAY_TICKS = 6;

    private static final Sound BURN_SOUND = Sound.BLOCK_RESPAWN_ANCHOR_CHARGE;
    private static final Sound LASER_SOUND = Sound.BLOCK_FIRE_AMBIENT;

    private static final float ARM_DISTANCE = 0.75F;
    private static final double ARM_ANGLE = -Math.PI / 4;
    private static final float ARM_HEIGHT_RELATIVE_TO_EYES = -0.7F;
    private static final float PARTICLE_SPREAD = 0.15F;
    private static final double MIN_PARTICLE_DENSITY = 0.5;
    private static final double MAX_PARTICLE_DENSITY = 1;
    private static final Particle LASER_PARTICLE = Particle.SMALL_FLAME;

	private static final Set<Material> allowedMaterials = Sets.newHashSet(
			Material.STONE,
			Material.COBBLESTONE,
			Material.MOSSY_COBBLESTONE,
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
		return new ItemInfo("Генерирует лазерный луч протяженностью 8 блоков и позволяет крайне " +
                "быстро уничтожать любую каменную породу в радиусе 3x3. Руды оставляет нетронутыми. " +
                "Для работы необходимы медные слитки.")
				.extra("Использует медный слиток каждые 30 секунд работы. Не имеет прочности.")
				.note("Лучше использовать без предметов (щита или факелов) в другой руке! " +
                        "К камню также относится адский камень, сланец, андезит, диорит и т.д.");
	}

	@Override
	public int getRedstonePrice() {
		return 80;
	}

	@Override
	public int getLapisPrice() {
		return 24;
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
        playUseEffect(player, hand, hitBlock);
        setMaxUseDelay(player);

        if (result == null || hitBlock == null) {
            return;
        }
        if (!allowedMaterials.contains(hitBlock.getType())) {
            return;
        }
        BlockFace hitBlockFace = Optional.ofNullable(result.getHitBlockFace()).orElse(BlockFace.EAST);
        breakBlock(player, hitBlock, hitBlockFace);
    }

    private void breakBlock(Player player, Block centerBlock, BlockFace face) {
        Location location = centerBlock.getLocation();
        Region region = new Region(
                location.clone().add(
                        face.getModX() == 0 ? -1 : 0,
                        face.getModY() == 0 ? -1 : 0,
                        face.getModZ() == 0 ? -1 : 0
                ),
                location.clone().add(
                        face.getModX() == 0 ? 1 : 0,
                        face.getModY() == 0 ? 1 : 0,
                        face.getModZ() == 0 ? 1 : 0
                )
        );
        List<Block> toBreak = region.getBlocksInside().stream()
                .filter(currentBlock -> allowedMaterials.contains(currentBlock.getType()))
                .filter(currentBlock -> currentBlock != centerBlock)
                .toList();
        for (Block blockToBreak : toBreak) {
            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockToBreak, player);
            Bukkit.getPluginManager().callEvent(blockBreakEvent);
            if (blockBreakEvent.isCancelled()) {
                continue;
            }
            player.getWorld().playEffect(blockToBreak.getLocation(), Effect.STEP_SOUND, blockToBreak.getType());
            blockToBreak.breakNaturally();
        }
    }

    private boolean isBurningFuel(Player player) {
        return player.getCooldown(FUEL) > 0;
    }

    private void burnFuel(Player player, @Nonnull ItemStack fuel) {
        player.getWorld().playSound(
                player.getLocation(),
                BURN_SOUND,
                1F,
                1F
        );
        player.setCooldown(FUEL, MAX_BURN_COOLDOWN);
        fuel.setAmount(fuel.getAmount() - 1);
    }

    @Nullable
    private ItemStack getAnyFuel(Player player) {
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            if (item.getType() == FUEL) {
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
        useDelays.put(player, MAX_USE_DELAY_TICKS);
    }

    private boolean isUsingNow(Player player) {
        return useDelays.containsKey(player);
    }

    private int getUseDelayTicks(Player player) {
        return useDelays.getOrDefault(player, MAX_USE_DELAY_TICKS);
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
                LASER_PARTICLE,
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

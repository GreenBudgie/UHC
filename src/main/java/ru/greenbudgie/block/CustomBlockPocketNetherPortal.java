package ru.greenbudgie.block;

import org.bukkit.*;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.items.CustomItem;
import ru.greenbudgie.items.CustomItems;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.Region;

import java.util.ArrayList;
import java.util.List;

public class CustomBlockPocketNetherPortal extends CustomBlockItem {

    private static final int WIDTH = 4;
    private static final int HEIGHT = 5;

    private final UHCPlayer owner;

    private final List<Location> portalPositions = new ArrayList<>();
    private final List<Location> filledPositions = new ArrayList<>();
    private final List<Location> unfilledPositions = new ArrayList<>();
    private final Region portalRegion;

    private boolean isConstructionSucceeded = false;

    public CustomBlockPocketNetherPortal(Location location, UHCPlayer owner) {
        super(location);
        this.owner = owner;
        double dx = owner.getLocation().getX() - centerLocation.getX();
        double dz = owner.getLocation().getZ() - centerLocation.getZ();
        var frameAxis = Math.abs(dx) > Math.abs(dz) ? Axis.Z : Axis.X;

        int baseX = location.getBlockX();
        int baseY = location.getBlockY();
        int baseZ = location.getBlockZ();
        World world = location.getWorld();

        for (int w = 0; w < WIDTH; w++) {
            for (int h = 0; h < HEIGHT; h++) {
                int shift = w - (WIDTH - 2);
                int x = baseX + (frameAxis == Axis.X ? shift : 0);
                int z = baseZ + (frameAxis == Axis.Z ? shift : 0);
                Location pos = new Location(world, x, baseY + h, z);
                boolean isCorner = (w == 0 || w == WIDTH - 1) && (h == 0 || h == HEIGHT - 1);
                boolean isFrame = w == 0 || w == WIDTH - 1 || h == 0 || h == HEIGHT - 1;
                if (isCorner) {
                    continue;
                }
                if (isFrame) {
                    if (pos.getBlock().getType() != Material.OBSIDIAN) {
                        unfilledPositions.add(pos);
                    }
                } else {
                    portalPositions.add(pos);
                }
            }
        }

        int minShift = -(WIDTH - 2);
        int maxShift = 1;
        Location start = new Location(
                world,
                baseX + (frameAxis == Axis.X ? minShift : 0),
                baseY,
                baseZ + (frameAxis == Axis.Z ? minShift : 0)
        );
        Location end = new Location(
                world,
                baseX + (frameAxis == Axis.X ? maxShift : 0),
                baseY + HEIGHT - 1,
                baseZ + (frameAxis == Axis.Z ? maxShift : 0)
        );
        portalRegion = new Region(start, end);
    }

    @Override
    public boolean isUnbreakable() {
        return true;
    }

    @Override
    public boolean removeIfRealBlockNotPresent() {
        return true;
    }

    @Override
    public void onUpdate() {
        if (ticksPassed % 2 == 0) {
            ParticleUtils.createParticlesInsideRegion(portalRegion, Particle.DUST, 5, Color.PURPLE);
        }

        if (ticksPassed % 5 != 0) {
            return;
        }

        if (unfilledPositions.isEmpty()) {
            if (portalPositions.stream().anyMatch(pos -> !pos.getBlock().isEmpty())) {
                dropAndRemove();
                return;
            }

            var firePosition = location.add(0, 1, 0);
            firePosition.getBlock().setType(Material.FIRE);
            firePosition.getWorld().playSound(firePosition, Sound.ITEM_FIRECHARGE_USE, 1f, 0.5f);
            ParticleUtils.createParticlesInsideRegion(portalRegion, Particle.FLAME, 20, null);
            isConstructionSucceeded = true;
            remove();
            return;
        }

        var randomUnfilledPosition = MathUtils.choose(unfilledPositions);

        if (!randomUnfilledPosition.getBlock().isEmpty()) {
            dropAndRemove();
            return;
        }

        randomUnfilledPosition.getBlock().setType(Material.OBSIDIAN);
        randomUnfilledPosition.getWorld().playSound(randomUnfilledPosition, Sound.BLOCK_STONE_PLACE, 1f, (float)MathUtils.randomRangeDouble(0.8f, 1f));
        unfilledPositions.remove(randomUnfilledPosition);
        filledPositions.add(randomUnfilledPosition);
    }

    @Override
    public CustomItem getRepresentingItem() {
        return CustomItems.pocketNetherPortal;
    }

    @Override
    public boolean doReplaceBlockOnRemove() {
        return !isConstructionSucceeded;
    }

    @Override
    public void onRemove() {
        if (isConstructionSucceeded) {
            return;
        }

        owner.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Не удалось построить портал: место не расчищено");
        for (Location filledPosition : filledPositions) {
            var block = filledPosition.getBlock();
            filledPosition.getWorld().playEffect(filledPosition, Effect.STEP_SOUND, block.getType());
            block.setType(Material.AIR);
        }
        ParticleUtils.createParticlesInsideRegion(portalRegion, Particle.LARGE_SMOKE, 20, null);
    }
}

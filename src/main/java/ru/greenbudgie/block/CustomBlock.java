package ru.greenbudgie.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.WorldHelper;

import java.util.List;

/**
 * Represents a custom block in a world in the game.
 * Note: this block really represents one of the blocks in the world,
 * rather than working like custom items.
 *
 * There are two concepts: block registry and real block.
 * Block registry is a CustomBlock in a list in CustomBlockManager.
 * The real block is bound to this block's location block in the real world.
 *
 * Custom blocks DO NOT drop any item on remove by default.
 */
public abstract class CustomBlock implements Listener {

    protected final Location location;
    /**
     * A location that is centered inside the block.
     * Usually used to make some sort of effects.
     */
    protected final Location centerLocation;
    protected boolean doRemove = false;
    protected long ticksPassed = 0;

    public CustomBlock(Location location) {
        this.location = location;
        this.centerLocation = location.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
        location.getBlock().setType(getMaterial());
        Bukkit.getPluginManager().registerEvents(this, UHCPlugin.instance);
        CustomBlockManager.getCustomBlocks().add(this);
        onCreate();
    }

    /**
     * Whether this block must stay in place even if tried to break by hand or exploded
     */
    public boolean isUnbreakable() {
        return false;
    }

    /**
     * Whether to remove the registry if the real block have disappeared for any reason
     */
    public boolean removeIfRealBlockNotPresent() {
        return true;
    }

    /**
     * Returns whether this block is immune to explosions.
     * If it's not, the real block and the registry will be removed in case of explosion.
     */
    public boolean isExplosionProof() {
        return true;
    }

    /**
     * Gets the location of this block in the world
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the real block this custom block represents.
     */
    public Block getBlock() {
        return location.getBlock();
    }

    /**
     * Gets the material of this block.
     * The material represents the real block type.
     */
    public abstract Material getMaterial();

    /**
     * Called every tick when block updates
     */
    public void onUpdate() {}

    /**
     * Called whenever the player interacts with the block
     */
    public void onInteract(PlayerInteractEvent event) {}

    /**
     * Called when a player breaks this block.
     * This method will not be called if block is about to remove
     * or when it destroyed by some reason, only when a player breaks it.
     * Note that by default custom blocks drop no item when removed.
     */
    public void onBreak(BlockBreakEvent event) {}

    /**
     * Called when the block explodes.
     * This will not be called if the block is explosion immune.
     */
    public void onExplode() {}

    /**
     * Called whenever this block is about to be destroyed and further removed
     * by natural reasons like explosion or player block break.
     */
    public void onDestroy() {}

    /**
     * Called whenever this block is about to remove.
     * This will be called even if the block is exploded/broken by player.
     */
    public void onRemove() {}

    /**
     * Called every tick to update the block
     */
    protected final void update() {
        if(removeIfRealBlockNotPresent() && !hasRealBlock()) {
            remove();
        } else {
            onUpdate();
            ticksPassed++;
        }
    }

    /**
     * Removes the block from the world.
     * This method removes the registry and the real block.
     */
    public final void remove() {
        onRemove();
        location.getBlock().setType(Material.AIR);
        HandlerList.unregisterAll(this);
        doRemove = true;
    }

    /**
     * Returns whether the real block is still on place.
     *
     * It is very important to keep track on the real block,
     * because it can be removed without the registry removed.
     * In optimization and bug-safe purposes it does not happen automatically.
     */
    public boolean hasRealBlock() {
        return getBlock().getType() == getMaterial();
    }

    /**
     * Called whenever a block is created in the world.
     * Literally, the last constructor statement.
     */
    public void onCreate() {}

    /**
     * Returns whether this block represents the given real block
     */
    public boolean equals(Block block) {
        return WorldHelper.compareIntLocations(block.getLocation(), location);
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        if(equals(event.getBlock())) {
            if(isUnbreakable()) {
                event.setCancelled(true);
            } else {
                event.setDropItems(false);
                event.setExpToDrop(0);
                onBreak(event);
                onDestroy();
                remove();
            }
        }
    }

    /**
     * Preventing the block to be moved by piston is crucial.
     * If the real block moves, the registry can now not represent it.
     */
    @EventHandler
    public void noPistonMove(BlockPistonExtendEvent event) {
        for(Block block : event.getBlocks()) {
            if(equals(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void noPistonMove(BlockPistonRetractEvent event) {
        for(Block block : event.getBlocks()) {
            if(equals(block)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private void handleExplosion(List<Block> blocks) {
        if(isExplosionProof() || isUnbreakable()) {
            blocks.removeIf(this::equals);
        } else {
            if(blocks.stream().anyMatch(this::equals)) {
                //We need to prevent the block to drop item by default
                //So, removing it manually:
                blocks.removeIf(this::equals);
                onExplode();
                onDestroy();
                remove();
            }
        }
    }

    @EventHandler
    public void explosion(BlockExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    @EventHandler
    public void explosion(EntityExplodeEvent event) {
        handleExplosion(event.blockList());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if(event.hasBlock() && equals(event.getClickedBlock())) {
            onInteract(event);
        }
    }

}

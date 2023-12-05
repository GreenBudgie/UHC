package ru.greenbudgie.drop;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.List;
import java.util.Set;

public abstract class ChestBasedDrop extends Drop {

    @Override
    public void drop() {
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                for(int z = -1; z <= 1; z++) {
                    Block block = location.clone().add(x, y, z).getBlock();
                    block.setType(getCasing());
                }
            }
        }
        Block chestBlock = location.getBlock();
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();
        Inventory inv = chest.getBlockInventory();
        Set<Integer> slotsToFill = Sets.newHashSet();
        for(int i = 0; i < inv.getSize(); i++) {
            slotsToFill.add(i);
        }
        int items = MathUtils.randomRange(getMinFillers() + getMainItemsCount(), getMaxFillers() + getMainItemsCount());
        for(int i = 0; i < items; i++) {
            int slot = MathUtils.choose(slotsToFill);
            slotsToFill.remove(slot);
            inv.setItem(slot, i < getMainItemsCount() ? Drops.getRandomDrop() : getRandomFiller());
        }
        location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 1F, 0.5F);
        ParticleUtils.createParticlesInRange(location, 1.5, Particle.FLAME, null, 40);
        for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
            p.sendTitle(" ", getSpawnMessage(), 5, 40, 20);
            p.sendMessage(getChatDropCoordinatesInfo());
            p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5F, 0.5F);
        }
    }

    protected int getMainItemsCount() {
        return 1;
    }

    protected abstract int getMinFillers();

    protected abstract int getMaxFillers();

    protected abstract Material getCasing();

    protected final ItemStack getRandomFiller() {
        return MathUtils.choose(getFillers());
    }

    public abstract List<ItemStack> getFillers();

    @Override
    public void update() {
        if(TaskManager.isSecUpdated()) {
            if(timer <= 0) {
                drop();
                setup();
            } else {
                ParticleUtils.createParticlesInRange(location, 1.5, Particle.SMOKE_NORMAL, null, 10);
                timer--;
            }
        }
    }

}
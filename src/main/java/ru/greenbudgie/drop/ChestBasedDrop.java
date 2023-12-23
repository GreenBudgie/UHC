package ru.greenbudgie.drop;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.drop.marker.ChestBasedDropMarker;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.TaskManager;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public abstract class ChestBasedDrop extends Drop {

    private Region dropRegion;

    @Override
    public void setup() {
        super.setup();
        dropRegion = new Region(
                location.clone().add(-1, -1, -1),
                location.clone().add(1, 1, 1)
        );
    }

    @Override
    public void drop() {
        for (Block blockInside : dropRegion.getBlocksInside()) {
            blockInside.setType(getCasing());
        }
        Block chestBlock = location.getBlock();
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();
        Inventory inv = chest.getBlockInventory();
        Set<Integer> slotsToFill = Sets.newHashSet();
        for(int i = 0; i < inv.getSize(); i++) {
            slotsToFill.add(i);
        }
        int fillerNumber = MathUtils.randomRange(getMinFillers(), getMaxFillers());
        List<WeightedItem> fillers = getFillers().getRandomElementsWeighted(fillerNumber);
        List<WeightedItem> mainDrops = Drops.getWeightedDropsList().getRandomElementsWeighted(getMainItemsCount());
        List<WeightedItem> mainItemsAndFillers = Stream.concat(fillers.stream(), mainDrops.stream()).toList();
        for (WeightedItem item : mainItemsAndFillers) {
            int slot = MathUtils.choose(slotsToFill);
            slotsToFill.remove(slot);
            inv.setItem(slot, item.getItem());
        }
        location.getWorld().playSound(location, Sound.ITEM_FIRECHARGE_USE, 1F, 0.5F);
        ParticleUtils.createParticlesOnRegionEdges(dropRegion, Particle.FLAME, 4, null);
        for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
            p.sendTitle(" ", getSpawnMessage(), 5, 40, 20);
            p.sendMessage(getChatDropCoordinatesInfo());
            p.playSound(p.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5F, 0.5F);
        }
        if (currentMarker != null) {
            currentMarker.setDropped();
        }
    }

    protected int getMainItemsCount() {
        return 1;
    }

    protected abstract int getMinFillers();

    protected abstract int getMaxFillers();

    protected abstract Material getCasing();

    public abstract WeightedItemList getFillers();

    @Override
    public void update() {
        if (!TaskManager.isSecUpdated()) {
            return;
        }
        if(timer <= 0) {
            drop();
            setup();
        } else {
            ParticleUtils.createParticlesOnRegionEdges(dropRegion, Particle.SMOKE_NORMAL, 4, null);
            timer--;
        }
    }

    @Override
    public ChestBasedDropMarker createMarker() {
        return new ChestBasedDropMarker(this);
    }

}

package ru.greenbudgie.drop;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.drop.marker.AirDropMarker;
import ru.greenbudgie.main.UHCPlugin;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.TaskManager;
import ru.greenbudgie.util.item.ItemUtils;
import ru.greenbudgie.util.weighted.WeightedEnchantedItem;
import ru.greenbudgie.util.weighted.WeightedEnchantment;
import ru.greenbudgie.util.weighted.WeightedItem;
import ru.greenbudgie.util.weighted.WeightedItemList;

import static org.bukkit.ChatColor.*;

public class AirDrop extends Drop {

    private static final ItemStack healingPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Regeneration")
            .withColor(Color.fromRGB(255, 182, 243))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.REGENERATION).seconds(35).amplifier(0).build()
            ).build();
    private static final ItemStack strengthPotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Dominance")
            .withColor(Color.fromRGB(100, 0, 0))
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.STRENGTH).minutes(2).amplifier(0).build(),
                    new PotionEffectBuilder(PotionEffectType.RESISTANCE).minutes(2).build()
            ).build();
    private static final ItemStack damagePotion = ItemUtils.potionBuilder()
            .withName(WHITE + "Potion of Death")
            .splash()
            .withColor(Color.BLACK)
            .withEffects(
                    new PotionEffectBuilder(PotionEffectType.INSTANT_DAMAGE).amplifier(1).build(),
                    new PotionEffectBuilder(PotionEffectType.WITHER).seconds(13).build()
            ).build();

    private static final WeightedItemList weightedDrops = new WeightedItemList(
            WeightedItem.builder(healingPotion).build(),
            WeightedItem.builder(strengthPotion).build(),
            WeightedItem.builder(damagePotion).build(),
            WeightedItem.builder(Material.GOLDEN_APPLE).amount(3, 4).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_BOOTS)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(1, 2).build()
                    ).weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.FEATHER_FALLING).level(2, 4).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_LEGGINGS)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(1, 2).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.FIRE_PROTECTION).level(1, 2).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_CHESTPLATE)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(1, 2).build()
                    ).weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.BLAST_PROTECTION).level(1, 2).build(),
                            WeightedEnchantment.builder(Enchantment.THORNS).level(1).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_HELMET)
                    .alwaysEnchant(
                            WeightedEnchantment.builder(Enchantment.PROTECTION).level(1, 2).build()
                    )
                    .weightedEnchantments(
                            WeightedEnchantment.builder(Enchantment.PROJECTILE_PROTECTION).level(1, 2).build()
                    ).number(0, 1).build(),

            WeightedEnchantedItem.item(Material.BOW).alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.POWER).level(1, 2).build(),
                    WeightedEnchantment.builder(Enchantment.INFINITY).build()
            ).build(),

            WeightedEnchantedItem.item(Material.DIAMOND_SWORD).alwaysEnchant(
                    WeightedEnchantment.builder(Enchantment.SHARPNESS).level(3, 4).build()
            ).build()
    );

    private final int MAX_DROP_HEIGHT = 100;
    private double height = 2.5, dropHeight = MAX_DROP_HEIGHT;

    @Override
    public String getName() {
        return AQUA + "" + BOLD + "Аирдроп";
    }

    @Override
    public int getDefaultDropDelay() {
        return 5 * 60;
    }

    @Override
    public int getFirstDropDelay() {
        return 0;
    }

    @Override
    public ChatColor getMarkerColor() {
        return AQUA;
    }

    @Override
    public void drop() {
        Item item = location.getWorld().dropItem(location, itemToDrop);
        item.setGlowing(true);
        item.setPickupDelay(1);
        item.setMetadata("airdrop", new FixedMetadataValue(UHCPlugin.instance, true));
        location.getWorld().playSound(location, Sound.ENTITY_ITEM_PICKUP, 1F, 0.5F);
        location.getWorld().playSound(location, Sound.BLOCK_WOOL_BREAK, 1.5F, 0.5F);
        ParticleUtils.createParticlesInsideSphere(location, 3, Particle.DUST, Color.WHITE, 40);
        for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
            p.sendTitle(" ", getSpawnMessage(), 10, 40, 20);
            p.sendMessage(getChatDropCoordinatesInfo());
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5F, 1.5F);
        }
        announceItemInChat(false);
        if (currentMarker != null) {
            currentMarker.setDropped();
        }
    }

    @Override
    public Location getRandomLocation() {
        int size = ((int) WorldManager.getActualMapSize()) / 2 - 10;
        int x = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockX() - size,
                WorldManager.spawnLocation.getBlockX() + size);
        int z = MathUtils.randomRange(
                WorldManager.spawnLocation.getBlockZ() - size,
                WorldManager.spawnLocation.getBlockZ() + size);
        int y = WorldManager.getGameMap().getHighestBlockYAt(x, z) + 1;
        return new Location(WorldManager.getGameMap(), x, y, z);
    }

    @Override
    public void setup() {
        super.setup();
        dropHeight = MAX_DROP_HEIGHT;
    }

    @Override
    public void update() {
        super.update();

        if(timer <= 0 && TaskManager.isSecUpdated()) {
            drop();
            setup();
            return;
        }
        if(TaskManager.tick % 2 == 0) {
            height -= 0.05;
            if(height < 0) {
                height = 2.5;
            }
            double radius = 1.8;
            double angle = height * Math.PI * 2;
            Location l = location.clone().add(
                    Math.sin(angle) * radius,
                    height,
                    Math.cos(angle) * radius);
            Location l2 = location.clone().add(
                    Math.sin(angle + Math.PI) * radius,
                    height,
                    Math.cos(angle + Math.PI) * radius);
            ParticleUtils.createParticle(l, Particle.CLOUD, null);
            ParticleUtils.createParticle(l2, Particle.CLOUD, null);
        }
        final int initDrop = 5;
        if(timer < initDrop) {
            dropHeight -= (double) MAX_DROP_HEIGHT / (initDrop * 20.0);
            double radius = 1;
            double angle = (MAX_DROP_HEIGHT / dropHeight) * Math.PI * 20;
            Location l = location.clone().add(Math.sin(angle) * radius, dropHeight - 1, Math.cos(angle) * radius);
            Location l2 = location.clone().add(Math.sin(angle + Math.PI) * radius, dropHeight - 1, Math.cos(angle + Math.PI) * radius);
            ParticleUtils.createParticle(l, Particle.CLOUD, null);
            ParticleUtils.createParticle(l2, Particle.CLOUD, null);
            if(TaskManager.tick % 5 == 0) {
                location.getWorld().playSound(l, Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);
            }
        }
        if(TaskManager.isSecUpdated()) {
            timer--;
        }
    }

    @Override
    public AirDropMarker createMarker() {
        return new AirDropMarker(this);
    }

    @Override
    public WeightedItemList getWeightedItemList() {
        return weightedDrops;
    }

    @Override
    public Material getRepresentingItem() {
        return Material.PHANTOM_MEMBRANE;
    }

}

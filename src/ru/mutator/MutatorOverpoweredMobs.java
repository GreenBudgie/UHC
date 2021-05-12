package ru.mutator;

import io.netty.util.internal.MathUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.WorldManager;
import ru.util.ItemUtils;
import ru.util.MathUtils;
import ru.util.WorldHelper;

import java.util.function.Consumer;

public class MutatorOverpoweredMobs extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.CRIMSON_ROOTS;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Сверхсильные Мобы";
	}

	@Override
	public String getDescription() {
		return "Враждебные мобы становятся по-настоящему опасными!";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.babyZombies;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void spawn(CreatureSpawnEvent e) {
		if(!e.isCancelled() && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
			LivingEntity entity = e.getEntity();
			EntityType entityType = e.getEntityType();
			if(entityType == EntityType.CREEPER) {
				Creeper creeper = (Creeper) entity;
				creeper.setMaxFuseTicks((int) (creeper.getMaxFuseTicks() / MathUtils.randomRangeDouble(1, 1.5)));
				creeper.setExplosionRadius((int) (creeper.getExplosionRadius() * MathUtils.randomRangeDouble(1, 1.5)));
			}
			if(entityType == EntityType.SKELETON) {
				EntityEquipment equipment = entity.getEquipment();
				if(equipment != null && MathUtils.chance(50)) {
					ItemStack bow = equipment.getItemInMainHand();
					if(bow.getType() == Material.BOW && bow.getEnchantments().isEmpty()) {
						if(MathUtils.chance(60)) bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
						if(MathUtils.chance(30)) bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
						if(MathUtils.chance(12)) bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
					}
				}
			}
			if(entityType == EntityType.SPIDER) {
				if(!entity.hasPotionEffect(PotionEffectType.INVISIBILITY) && MathUtils.chance(50)) {
					entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
				}
			}
			if(entity instanceof Zombie) {
				Zombie zombie = (Zombie) entity;
				if(MathUtils.chance(50)) zombie.setBaby(true);
				EntityEquipment equipment = entity.getEquipment();
				if(equipment != null && MathUtils.chance(50)) {
					ItemStack sword = new ItemStack(MathUtils.chance(70) ? Material.STONE_SWORD : Material.IRON_SWORD);
					if(MathUtils.chance(50)) sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
					if(MathUtils.chance(25)) sword.addEnchantment(Enchantment.KNOCKBACK, 1);
					if(MathUtils.chance(10)) sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
					equipment.setItemInMainHand(sword);
				}
			}
			if(entity instanceof Zombie || entity instanceof Skeleton) {
				EntityEquipment equipment = entity.getEquipment();
				if(equipment != null) {
					if(MathUtils.chance(50)) {
						ItemStack item = new ItemStack(MathUtils.chance(30) ? Material.IRON_HELMET : Material.CHAINMAIL_HELMET);
						if(MathUtils.chance(20)) item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
						entity.getEquipment().setHelmet(item);
					}
					if(MathUtils.chance(50)) {
						ItemStack item = new ItemStack(MathUtils.chance(30) ? Material.IRON_CHESTPLATE : Material.CHAINMAIL_CHESTPLATE);
						if(MathUtils.chance(20)) item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
						entity.getEquipment().setChestplate(item);
					}
					if(MathUtils.chance(50)) {
						ItemStack item = new ItemStack(MathUtils.chance(30) ? Material.IRON_LEGGINGS : Material.CHAINMAIL_LEGGINGS);
						if(MathUtils.chance(20)) item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
						entity.getEquipment().setLeggings(item);
					}
					if(MathUtils.chance(50)) {
						ItemStack item = new ItemStack(MathUtils.chance(30) ? Material.IRON_BOOTS : Material.CHAINMAIL_BOOTS);
						if(MathUtils.chance(20)) item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
						entity.getEquipment().setBoots(item);
					}
					equipment.setBootsDropChance(0);
					equipment.setChestplateDropChance(0);
					equipment.setLeggingsDropChance(0);
					equipment.setHelmetDropChance(0);
					equipment.setItemInMainHandDropChance(0);
				}
			}
			//Increasing speed for all entities
			if(WorldHelper.isBadMob(entityType) && entityType != EntityType.CREEPER) {
				if(!entity.hasPotionEffect(PotionEffectType.SPEED) && MathUtils.chance(50)) {
					entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
				}
			}
		}
	}

}

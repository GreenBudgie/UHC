package ru.items;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.FightHelper;
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;
import ru.event.UHCPlayerDeathEvent;
import ru.main.UHCPlugin;
import ru.util.ParticleUtils;
import ru.util.TaskManager;
import ru.util.WorldHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomItemUnderworldEgg extends ClassCustomItem implements Listener {

	public String getName() {
		return ChatColor.RED + "" + ChatColor.BOLD + "Call of the Underworld";
	}

	public Material getMaterial() {
		return Material.EGG;
	}

	@Override()
	public void onUseRight(Player player, ItemStack item, PlayerInteractEvent event) {
		if(isEquals(item) && !player.hasCooldown(item.getType())) {
			if(player.getWorld().getPVP()) {
				TaskManager.invokeLater(() -> player.setCooldown(item.getType(), 20 * 20));
			} else {
				event.setCancelled(true);
				event.setUseItemInHand(Event.Result.DENY);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Призывает помощников из-под земли в том месте, куда попало при броске. Появляются зомби и скелеты, атакующие других игроков. Нельзя использовать до ПВП.";
	}

	private void spawnMob(Location center, EntityType type, Player owner) {
		Monster monster = (Monster) center.getWorld().spawnEntity(center, type);
		monster.setMetadata("necromancer_owner", new FixedMetadataValue(UHCPlugin.instance, owner.getName()));
		WorldHelper.chorusTeleport(monster, 3, false);
		monster.getWorld().strikeLightningEffect(monster.getLocation());
		monster.setCustomName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Undead Warrior");
		monster.setCustomNameVisible(true);
		EntityEquipment equipment = monster.getEquipment();
		if(equipment != null) {
			equipment.setHelmet(new ItemStack(Material.GOLDEN_HELMET));
			if(monster instanceof Zombie) {
				equipment.setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));
			}
		}
		monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
	}

	@EventHandler
	public void targetFixes(EntityTargetLivingEntityEvent event) {
		if(event.getEntity() instanceof LivingEntity entity) {
			if(entity.hasMetadata("necromancer_owner")) {
				if(!(event.getTarget() instanceof Player target)) {
					event.setCancelled(true); //Cancel if targets not a player
					return;
				}
				if(target.getGameMode() == GameMode.SPECTATOR) {
					event.setCancelled(true); //Cancel if targets a spectator (vanilla bug fix)
					return;
				}
				List<MetadataValue> values = entity.getMetadata("necromancer_owner");
				if(!values.isEmpty()) {
					String necromancerOwner = values.get(0).asString();
					if(target.getName().equals(necromancerOwner)) {
						event.setCancelled(true); //Cancel if targets the necromancer
						return;
					}
					UHCPlayer uhcTarget = PlayerManager.asUHCPlayer(target);
					if(uhcTarget != null) {
						UHCPlayer teammate = uhcTarget.getTeammate();
						if(teammate != null && teammate.getNickname().equals(necromancerOwner)) {
							event.setCancelled(true); //Cancel if targets the necromancer's teammate
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void removeTargetOnDeath(UHCPlayerDeathEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		Player player = uhcPlayer.getPlayer();
		if(player != null) {
			World world = player.getWorld();
			for(LivingEntity entity : world.getLivingEntities()) {
				if(entity instanceof Monster monster && monster.hasMetadata("necromancer_owner")) {
					if(monster.getTarget() == player) {
						monster.setTarget(null);
					}
				}
			}
		}
	}

	private void setCustomDamager(Player victim, Monster damager) {
		if(damager.hasMetadata("necromancer_owner")) {
			List<MetadataValue> values = damager.getMetadata("necromancer_owner");
			if(!values.isEmpty()) {
				String necromancerOwner = values.get(0).asString();
				UHCPlayer owner = PlayerManager.asUHCPlayer(necromancerOwner);
				if(owner != null && owner.isAlive()) {
					FightHelper.setDamager(victim, owner, 40, "убил мобами");
				}
			}
		}
	}

	@EventHandler
	public void monsterArrowDamage(ProjectileHitEvent event) {
		if(event.getHitEntity() instanceof Player victim && event.getEntity().getShooter() instanceof Monster shooter) {
			setCustomDamager(victim, shooter);
		}
	}

	@EventHandler
	public void monsterDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player victim && event.getDamager() instanceof Monster damager) {
			setCustomDamager(victim, damager);
		}
	}

	@EventHandler
	public void throwEgg(ProjectileLaunchEvent event) {
		if(event.getEntity() instanceof Egg egg && egg.getShooter() instanceof Player player && isEquals(egg.getItem())) {
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 2);
			ParticleUtils.createParticlesAround(player, Particle.LAVA, null, 20);
		}
	}

	@EventHandler
	public void eggLand(ProjectileHitEvent event) {
		if(event.getEntity() instanceof Egg egg && egg.getShooter() instanceof Player player && isEquals(egg.getItem())) {
			UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
			if(uhcPlayer != null) {
				Location spawnLocation = egg.getLocation();
				ParticleUtils.createParticlesOutlineSphere(spawnLocation, 5, Particle.SMOKE_LARGE, null, 40);
				spawnMob(spawnLocation, EntityType.ZOMBIE, player);
				spawnMob(spawnLocation, EntityType.HUSK, player);
				spawnMob(spawnLocation, EntityType.SKELETON, player);
				spawnMob(spawnLocation, EntityType.STRAY, player);
			}
		}
	}

}

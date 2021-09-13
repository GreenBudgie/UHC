package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHCPlayer;
import ru.util.ItemInfo;
import ru.event.GameEndEvent;
import ru.util.ParticleUtils;

import java.util.HashSet;
import java.util.Set;

public class CustomItemAncientShard extends ClassCustomItem implements Listener {

	private Set<UHCPlayer> affectedPlayers = new HashSet<>();

	public String getName() {
		return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Ancient Shard";
	}

	public Material getMaterial() {
		return Material.AMETHYST_SHARD;
	}

	@Override
	public void onUseRight(Player player, ItemStack item, PlayerInteractEvent e) {
		UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
		if(uhcPlayer != null) {
			item.setAmount(item.getAmount() - 1);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 9));
			ParticleUtils.createParticlesOutlineSphere(player.getLocation(), 2, Particle.REDSTONE, Color.fromRGB(214, 37, 152), 50);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_AMBIENT, 1F, 0.5F);
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_FALL, 1F, 0.5F);
			affectedPlayers.add(uhcPlayer);
		}
	}

	@EventHandler
	public void noPotionRemove(EntityPotionEffectEvent event) {
		if(event.getEntity() instanceof Player player &&
				(event.getAction() == EntityPotionEffectEvent.Action.CLEARED
						|| event.getAction() == EntityPotionEffectEvent.Action.REMOVED
						|| event.getAction() == EntityPotionEffectEvent.Action.CHANGED)) {
			PotionEffect effect = event.getOldEffect();
			if(effect != null && effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
				UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
				if(uhcPlayer != null && affectedPlayers.contains(uhcPlayer)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void absorbDamage(EntityDamageEvent event) {
		if(!event.isCancelled() && event.getEntity() instanceof Player player) {
			UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
			if(uhcPlayer != null && affectedPlayers.contains(uhcPlayer)) {
				ParticleUtils.createParticlesOutlineSphere(player.getLocation(), 2, Particle.REDSTONE, Color.fromRGB(214, 37, 152), 90);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_AMBIENT, 1F, 0.5F);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1.8F, 0.5F);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 1, false, false, false));
				event.setCancelled(true);
				affectedPlayers.remove(uhcPlayer);
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			}
		}
	}

	@EventHandler
	public void clearOnGameEnd(GameEndEvent event) {
		affectedPlayers.clear();
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("После использования гарантирует, что первый полученный урон будет полностью поглощен")
				.explanation("Эффект действует, пока не будет получен любой урон")
				.example("Ты использовал предмет. Через 20 минут ты случайно упал в каньон, но весь полученный от падения урон был отменен. Больше эффект не действует.");
	}

}

package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.util.item.ItemInfo;

import java.util.HashSet;
import java.util.Set;

public class CustomItemHarmlessPearl extends RequesterCustomItem implements Listener {

	public static Set<Player> harmlessPearls = new HashSet<>();

	public String getName() {
		return ChatColor.LIGHT_PURPLE + "Harmless Pearl";
	}

	public Material getMaterial() {
		return Material.ENDER_PEARL;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		if(item != null && item.getType() == Material.ENDER_PEARL && !p.hasCooldown(item.getType())) {
			if(isEquals(item)) {
				harmlessPearls.add(p);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_EYE_LAUNCH, 1F, 1.5F);
			} else {
				if(harmlessPearls.contains(p)) harmlessPearls.remove(p);
			}
		}
	}

	@EventHandler
	public void removeDamage(PlayerTeleportEvent e) {
		if(e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Player p = e.getPlayer();
			if(harmlessPearls.contains(p)) {
				e.setCancelled(true);
				p.setNoDamageTicks(1);
				p.teleport(e.getTo());
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 0.5F);
				harmlessPearls.remove(p);
			}
		}
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Перл, не наносящий урона при телепортации");
	}

	@Override
	public int getRedstonePrice() {
		return 40;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

}

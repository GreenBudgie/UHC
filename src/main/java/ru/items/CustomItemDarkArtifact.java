package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.main.UHCPlugin;

public class CustomItemDarkArtifact extends CustomItem implements Listener {

	@Override
	public String getName() {
		return ChatColor.DARK_RED + "" + ChatColor.BOLD + "Темный Артефакт";
	}

	@Override
	public Material getMaterial() {
		return Material.BLACK_DYE;
	}

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		e.setUseItemInHand(Event.Result.DENY);
		e.setUseInteractedBlock(Event.Result.DENY);
		e.setCancelled(true);
	}

	@EventHandler
	public void noInteract(PlayerInteractEntityEvent e) {
		if(CustomItems.darkArtifact.isEquals(e.getPlayer().getInventory().getItem(e.getHand()))) {
			e.setCancelled(true);
		}
	}

}

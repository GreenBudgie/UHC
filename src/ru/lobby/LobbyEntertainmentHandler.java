package ru.lobby;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.UHC.PlayerHandler;
import ru.UHC.WorldManager;
import ru.util.WorldHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Handles misc. entertainments in lobby (e.g. music discs)
 */
public class LobbyEntertainmentHandler implements Listener {

	private static Map<Player, Jukebox> lastUsedJukebox = new HashMap<>();

	private boolean isInLobby(Player player) {
		return PlayerHandler.isInLobby(player);
	}

	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(isInLobby(player)) {
			Block block = e.getClickedBlock();
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.JUKEBOX && e.getHand() == EquipmentSlot.HAND) {
				Jukebox jukebox = (Jukebox) e.getClickedBlock().getState();
				if(jukebox.isPlaying()) {
					WorldHelper.spawnParticle(jukebox.getLocation().clone().add(0.5, 1.2, 0.5), Particle.SMOKE_LARGE, null);
					jukebox.getWorld().playSound(jukebox.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 0.5F, 0.5F);
					jukebox.setPlaying(null);
					jukebox.setRecord(null);
					jukebox.update();
				} else {
					lastUsedJukebox.put(player, jukebox);
					Inventory inv = Bukkit.createInventory(player, 18, ChatColor.YELLOW + "Выбери пластинку");
					inv.addItem(Stream.of(Material.values()).filter(Material::isRecord).map(ItemStack::new).toArray(ItemStack[]::new));
					player.openInventory(inv);
				}
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void invClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(isInLobby(p)) {
			ItemStack item = e.getCurrentItem();
			if(e.getView().getTitle().equalsIgnoreCase(ChatColor.YELLOW + "Выбери пластинку")) {
				Jukebox jukebox = lastUsedJukebox.get(p);
				if(item != null && item.getType() != Material.AIR) {
					jukebox.setRecord(item);
					jukebox.setPlaying(item.getType());
					jukebox.update();
					WorldHelper.spawnParticle(jukebox.getLocation().clone().add(0.5, 1.2, 0.5), Particle.NOTE, null);
					p.closeInventory();
				}
				e.setCancelled(true);
			}
		}
	}

}

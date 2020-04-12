package ru.requester;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ru.util.MathUtils;
import ru.util.TaskManager;
import ru.util.WorldHelper;

public class RequestedItem {

	private Location location;
	private ItemStack item;
	private int timeToDrop;
	private int droppingTimer = 60;
	private boolean dropping = false;
	private boolean done = false;
	private ArmorStand info;
	private ArmorStand timer;

	public RequestedItem(Location loc, ItemStack item) {
		this.location = loc.clone().add(0, 1.5, 0);
		this.item = item;
		this.timeToDrop = 30;
		info = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		hideStand(info);
		info.setCustomName(ChatColor.AQUA + "Запрос" + ChatColor.GRAY + ": " + item.getItemMeta().getDisplayName());
		timer = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, -0.3, 0), EntityType.ARMOR_STAND);
		hideStand(timer);
		timer.setCustomName(ChatColor.AQUA + "" + timeToDrop);
	}

	private void hideStand(ArmorStand stand) {
		stand.setGravity(false);
		stand.setMarker(true);
		stand.setVisible(false);
		stand.setCustomNameVisible(true);
	}

	public boolean isDone() {
		return done;
	}

	public void deleteStands() {
		if(info != null && info.isValid()) info.remove();
		if(timer != null && timer.isValid()) timer.remove();
	}

	public void update() {
		if(timeToDrop <= 0) {
			if(!dropping) {
				dropping = true;
				timer.remove();
				info.setCustomName(ChatColor.GREEN + "Предмет прибывает...");
				location.getWorld().playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 0.1F, 0.5F);
			}
			droppingTimer--;
			Location l = location.clone().add(0, droppingTimer - 2, 0);
			if(l.getY() <= l.getWorld().getMaxHeight()) {
				WorldHelper.spawnParticle(l, Particle.CLOUD, Color.WHITE);
				if(MathUtils.chance(60 - droppingTimer)) {
					l.getWorld().playSound(l, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5F, 1.5F);
				}
				if(droppingTimer != 0) {
					WorldHelper.spawnParticlesOutlineSphere(l, (60 - droppingTimer) / 40.0, Particle.FLAME, null, 30 - (droppingTimer / 2));
				}

			}
			if(droppingTimer <= 0) {
				info.remove();
				Location bottom = l.clone().add(0, -2, 0);
				bottom.getWorld().dropItemNaturally(location, item);
				WorldHelper.spawnParticlesInsideSphere(bottom, 3, Particle.LAVA, null, 50);
				WorldHelper.spawnParticlesInsideSphere(bottom, 3, Particle.EXPLOSION_LARGE, null, 50);
				bottom.getWorld().playSound(bottom, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1F, 0.5F);
				bottom.getWorld().playSound(bottom, Sound.ENTITY_GENERIC_EXPLODE, 1F, 0.4F);
				done = true;
			}
		} else {
			if(TaskManager.isSecUpdated()) {
				timeToDrop--;
				timer.setCustomName(ChatColor.DARK_AQUA + "" + timeToDrop);
				location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.1F, timeToDrop % 2 == 0 ? 1F : 0.8F);
			}
		}
	}

}

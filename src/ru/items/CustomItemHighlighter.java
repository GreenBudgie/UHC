package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.util.ItemInfo;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemHighlighter extends RequesterCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Highlighter";
	}

	public Material getMaterial() {
		return Material.FEATHER;
	}

	@Override
	public void onUseRight(Player user, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		boolean found = false;
		UHCPlayer uhcUser = PlayerManager.asUHCPlayer(user);
		UHCPlayer uhcTeammate = uhcUser.getTeammate();
		for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
			Location currentLocation = uhcCurrentPlayer.getLocation();
			if(currentLocation == null) continue;
			if(user.getWorld() == currentLocation.getWorld() &&
					uhcUser != uhcCurrentPlayer &&
					(uhcTeammate == null || uhcTeammate != uhcCurrentPlayer) &&
					user.getLocation().distance(currentLocation) < 128) {
				if(uhcCurrentPlayer.isOnline()) {
					uhcCurrentPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 240, 0));
				} else {
					if(uhcCurrentPlayer.getGhost() != null) {
						uhcCurrentPlayer.getGhost().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 240, 0));
					}
				}
				user.sendMessage(ChatColor.AQUA + "Найден " +
						ChatColor.GOLD + uhcCurrentPlayer.getNickname() +
						ChatColor.GRAY + " (" +
						ChatColor.AQUA + (int) user.getLocation().distance(currentLocation) +
						ChatColor.GRAY + ")");
				found = true;
			}
		}
		if(!found) user.sendMessage(ChatColor.RED + "В радиусе 128 блоков никого нет");
		ParticleUtils.createParticlesInsideSphere(user.getLocation(), 3, Particle.END_ROD, Color.WHITE, 35);
		user.playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При использовании подсвечивает всех игроков в радиусе 128 блоков и выводит их список")
				.note("Ты и твой тиммейт не будут подсвечены");
	}

	@Override
	public int getRedstonePrice() {
		return 32;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

}

package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.util.ParticleUtils;
import ru.util.WorldHelper;

public class CustomItemHighlighter extends RequesterCustomItem {

	public String getName() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Highlighter";
	}

	public Material getMaterial() {
		return Material.FEATHER;
	}

	//TODO Highlight offline players
	@Override
	public void onUseRight(Player user, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		boolean found = false;
		Player teammate = PlayerManager.getTeammate(user);
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			if(user.getWorld() == currentPlayer.getWorld() &&
					user != currentPlayer &&
					(teammate == null || teammate != currentPlayer) &&
					user.getLocation().distance(currentPlayer.getLocation()) < 128) {
				currentPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 240, 0));
				user.sendMessage(ChatColor.AQUA + "Найден " + ChatColor.GOLD + currentPlayer.getName() + ChatColor.YELLOW + " (" + ChatColor.AQUA + (int) user.getLocation()
						.distance(currentPlayer.getLocation()) + ChatColor.YELLOW + ")");
				found = true;
			}
		}
		if(!found) user.sendMessage(ChatColor.RED + "В радиусе 128 блоков никого нет");
		ParticleUtils.createParticlesInsideSphere(user.getLocation(), 3, Particle.END_ROD, Color.WHITE, 35);
		user.playSound(user.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
	}

	@Override
	public String getDescription() {
		return "При использовании подсвечивает всех игроков в радиусе 128 блоков и выводит их список";
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

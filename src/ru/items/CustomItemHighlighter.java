package ru.items;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

	@Override
	public void onUseRight(Player p, ItemStack item, PlayerInteractEvent e) {
		item.setAmount(item.getAmount() - 1);
		boolean found = false;
		for(Player pl : UHC.players) {
			Player teammate = UHC.getTeammate(p);
			if(p.getWorld() == pl.getWorld() && p != pl && (teammate == null || teammate != pl) && p.getLocation().distance(pl.getLocation()) < 128) {
				pl.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 240, 0));
				p.sendMessage(ChatColor.AQUA + "Найден " + ChatColor.GOLD + pl.getName() + ChatColor.YELLOW + " (" + ChatColor.AQUA + (int) p.getLocation()
						.distance(pl.getLocation()) + ChatColor.YELLOW + ")");
				found = true;
			}
		}
		if(!found) p.sendMessage(ChatColor.RED + "В радиусе 128 блоков никого нет");
		ParticleUtils.createParticlesInsideSphere(p.getLocation(), 3, Particle.END_ROD, Color.WHITE, 35);
		p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1.5F);
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

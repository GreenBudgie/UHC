package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.UHC.Landmine;
import ru.UHC.UHC;

public class CustomItemLandmine extends RequesterCustomItem {

	public String getName() {
		return ChatColor.DARK_RED + "Landmine";
	}

	public Material getMaterial() {
		return Material.END_PORTAL_FRAME;
	}

	@Override
	public void onPlace(Player p, Block b, ItemStack item, BlockPlaceEvent e) {
		if(UHC.state != GameState.DEATHMATCH) {
			b.getWorld().playSound(b.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1F, 0.5F);
			UHC.landmines.add(new Landmine(p, b.getLocation()));
		}
	}

	@Override
	public String getDescription() {
		return "ћина; активируетс€ при приближении врага к ней на рассто€нии 5 блоков с задержкой в 1,5 секунды";
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 0;
	}

}

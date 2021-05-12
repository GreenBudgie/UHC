package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.UHC.GameState;
import ru.UHC.TerraTracer;
import ru.UHC.UHC;

public class CustomItemTerraTracer extends RequesterCustomItem {

	public String getName() {
		return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Terra Tracer";
	}

	public Material getMaterial() {
		return Material.BEACON;
	}

	@Override
	public void onPlace(Player p, Block b, ItemStack item, BlockPlaceEvent e) {
		if(UHC.state != GameState.DEATHMATCH && !e.isCancelled()) {
			TerraTracer.putTerraTracer(b.getLocation());
		}
	}

	@Override
	public String getDescription() {
		return "При установке на поверхности анализирует почву. Появляющиеся цветные частицы символизируют количество и тип руд, которые могут быть найдены под землей в радиусе его действия (16 блоков). Так можно понять, есть ли рядом алмазы и стоит ли здесь копать.";
	}

	@Override
	public int getRedstonePrice() {
		return 100;
	}

	@Override
	public int getLapisPrice() {
		return 8;
	}

}

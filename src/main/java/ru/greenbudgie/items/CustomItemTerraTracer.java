package ru.greenbudgie.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.block.CustomBlockTerraTracer;
import ru.greenbudgie.util.item.ItemInfo;

public class CustomItemTerraTracer extends ClassCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Terra Tracer";
	}

	public Material getMaterial() {
		return Material.BEACON;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("При установке указывает на ближайшее расположение алмазов или древних осколков")
				.extra("Появляется стрелка из частиц, ведущая к руде. Действует, пока руда не вскопана.")
				.note("Если рядом нет руды, то маяк можно будет использовать снова");
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockTerraTracer(location);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return false;
	}

}

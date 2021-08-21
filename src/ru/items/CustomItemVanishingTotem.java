package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockKnockoutTotem;
import ru.block.CustomBlockVanishingTotem;

public class CustomItemVanishingTotem extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.WHITE + "" + ChatColor.BOLD +"Vanishing Aura Totem";
	}

	public Material getMaterial() {
		return Material.DIORITE_WALL;
	}

	@Override
	public String getDescription() {
		return "Тотем очищающей ауры. Испаряет любые чужие снаряды (стрелы, фаерболы, брошенные зелья...) в радиусе 6 блоков. Действует 30 секунд. Одноразовый.";
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 12;
	}

	@Override
	public void placeBlock(Location location, Player owner) {
		new CustomBlockVanishingTotem(location, owner);
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}

package ru.items;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.block.CustomBlockPulsatingTotem;
import ru.util.ItemInfo;

public class CustomItemPulsatingTotem extends RequesterCustomItem implements BlockHolder {

	public String getName() {
		return ChatColor.WHITE + "" + ChatColor.BOLD + "Pulsating Aura Totem";
	}

	public Material getMaterial() {
		return Material.DIORITE_WALL;
	}

	@Override
	public ItemInfo getDescription() {
		return new ItemInfo("Тотем пульсирующей ауры. Отталкивает любые чужие снаряды.")
				.extra("Действует 1 минуту в радиусе 6 блоков, затем - исчезает")
				.note("К снарядам относятся стрелы, фаерболы, трезубцы, снежки и т.д. Действует на арене.")
				.example("Ты устанавливаешь тотем и можешь спокойно расстреливать других игроков, пока он будет тебя защищать от их снарядов");
	}

	@Override
	public int getRedstonePrice() {
		return 64;
	}

	@Override
	public int getLapisPrice() {
		return 6;
	}

	@Override
	public boolean placeBlock(Location location, Player owner) {
		new CustomBlockPulsatingTotem(location, owner);
		return true;
	}

	@Override
	public boolean canPlaceOnDeathmatch() {
		return true;
	}
}

package ru.mutator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.UHC.UHC;

public class MutatorDirtIsLava extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.MAGMA_BLOCK;
	}

	@Override
	public String getName() {
		return "����� - ��� �����";
	}

	@Override
	public String getDescription() {
		return "��� ����� � ����� ��� �������� ������������ � �����";
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void update() {
		if(UHC.state.isInGame()) {
			for(Player player : UHC.players) {
				Location l = player.getLocation();
				Block block = player.getWorld().getBlockAt(l.getBlockX(), (int) (l.getY() - 0.1), l.getBlockZ());
				Material blockType = block.getType();
				if(player.isOnGround() && (blockType == Material.DIRT || blockType == Material.COARSE_DIRT || blockType == Material.GRASS_BLOCK
						|| blockType == Material.GRASS_PATH || blockType == Material.FARMLAND || blockType == Material.PODZOL)) {
					block.setType(Material.MAGMA_BLOCK);
				}
			}
		}
	}
}

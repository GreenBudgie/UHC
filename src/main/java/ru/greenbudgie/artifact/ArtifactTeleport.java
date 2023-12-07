package ru.greenbudgie.artifact;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.util.WorldHelper;

import javax.annotation.Nullable;

public class ArtifactTeleport extends Artifact {

	@Override
	public String getName() {
		return "Переброска";
	}

	@Override
	public String getDescription() {
		return "Телепортирует всех игроков в случайную точку в радиусе 80 блоков";
	}

	@Override
	public int getStartingPrice() {
		return 14;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		for(UHCPlayer uhcCurrentPlayer : PlayerManager.getAlivePlayers()) {
			if(uhcCurrentPlayer.isOnline()) {
				WorldHelper.chorusTeleport(uhcCurrentPlayer.getPlayer(), 80);
			} else {
				if(uhcCurrentPlayer.getGhost() != null) {
					WorldHelper.chorusTeleport(uhcCurrentPlayer.getGhost(), 80);
				}
			}
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.CHORUS_FRUIT;
	}

	@Override
	public boolean canBeUsedOnClosedArena() {
		return false;
	}
}

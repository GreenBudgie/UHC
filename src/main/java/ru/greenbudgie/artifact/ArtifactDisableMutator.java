package ru.greenbudgie.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.GameType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.MutatorManager;
import ru.greenbudgie.util.MathUtils;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactDisableMutator extends Artifact {

	@Override
	public String getName() {
		return ChatColor.RED + "Менее Дикая Игра";
	}

	@Override
	public String getDescription() {
		return "Убирает случайный мутатор. Не сработает, если никаких мутаторов не активировано, либо нельзя деактивировать ни один из них.";
	}

	@Override
	public int getStartingPrice() {
		return 12;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		if(!GameType.getType().allowsMutators()) return false;
		List<Mutator> mutators = MutatorManager.getMutatorsForDeactivation();
		if(mutators.size() > 0) {
			if(player != null) {
				player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.8F, 1.2F);
			}
			Mutator mutator = MathUtils.choose(mutators);
			for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				currentPlayer.sendMessage(ChatColor.YELLOW + "Был деактивирован мутатор: " + ChatColor.LIGHT_PURPLE + mutator.getName());
			}
			mutator.deactivate();
			return true;
		}
		return false;
	}

	@Override
	public Material getType() {
		return Material.GUNPOWDER;
	}

}

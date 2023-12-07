package ru.greenbudgie.artifact;

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

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

public class ArtifactDisableMutator extends Artifact {

	@Override
	public String getName() {
		return "Менее Дикая Игра";
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
			Mutator mutator = MathUtils.choose(mutators);
			for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
				String message = padSymbols(GRAY + "Был деактивирован мутатор " + LIGHT_PURPLE + mutator.getName());
				currentPlayer.sendMessage(message);
				currentPlayer.playSound(currentPlayer.getLocation(), Sound.ITEM_TOTEM_USE, 0.8F, 1.2F);
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

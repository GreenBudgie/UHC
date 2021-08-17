package ru.artifact;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;

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
		return 13;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(@Nullable Player player) {
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
		} else if(player != null) {
			player.sendMessage(ChatColor.RED + "Невозможно деактивировать ни один мутатор!");
		}
	}

	@Override
	public Material getType() {
		return Material.GUNPOWDER;
	}

}

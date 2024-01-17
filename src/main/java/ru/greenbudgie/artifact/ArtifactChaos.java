package ru.greenbudgie.artifact;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.configuration.GameType;
import ru.greenbudgie.mutator.manager.MutatorManager;
import ru.greenbudgie.util.MathUtils;

import javax.annotation.Nullable;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class ArtifactChaos extends Artifact {

	@Override
	public String getName() {
		return "Хаос";
	}

	@Override
	public String getDescription() {
		return "Активируется три случайных неконфликтующих артефакта";
	}

	@Override
	public int getStartingPrice() {
		return 22;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 0.5F;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		List<Artifact> toAdd = Lists.newArrayList(
				ArtifactManager.timeLeap,
				ArtifactManager.drop,
				ArtifactManager.teleport,
				ArtifactManager.randomEffect,
				ArtifactManager.hunger,
				ArtifactManager.request
		);
		toAdd.add(MathUtils.choose(ArtifactManager.health, ArtifactManager.damage));
		boolean canAddMutator = false;
		boolean canRemoveMutator = false;
		if(MutatorManager.activeMutators.size() < 6 && GameType.getType().allowsMutators()) canAddMutator = true;
		if(!MutatorManager.getMutatorsForDeactivation().isEmpty() && GameType.getType().allowsMutators()) canRemoveMutator = true;
		if(canAddMutator && canRemoveMutator) {
			toAdd.add(MathUtils.choose(ArtifactManager.mutator, ArtifactManager.disableMutator));
		} else {
			if(canAddMutator) toAdd.add(ArtifactManager.mutator);
			if(canRemoveMutator) toAdd.add(ArtifactManager.disableMutator);
		}
		StringBuilder addedInfo = new StringBuilder();
		for(int i = 0; i < 3; i++) {
			Artifact artifact = MathUtils.choose(toAdd);
			artifact.onUse(player);
			addedInfo.append(RED + "" + BOLD + artifact.getName()).append(i == 2 ? "" : (DARK_GRAY + ", "));
			toAdd.remove(artifact);
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			String message = padSymbols(GRAY + "Были призваны артефакты " + addedInfo);
			currentPlayer.sendMessage(message);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.RABBIT_FOOT;
	}

	@Override
	public boolean canBeUsedOnArena() {
		return false;
	}

	@Override
	public boolean canBeUsedByMutator() {
		return false;
	}
}

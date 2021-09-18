package ru.artifact;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.UHC.GameType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;
import ru.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArtifactRandom extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_PURPLE + "Хаос";
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
		return 0;
	}

	@Override
	public boolean onUse(@Nullable Player player) {
		List<Artifact> toAdd = Lists.newArrayList(ArtifactManager.timeLeap, ArtifactManager.drop, ArtifactManager.teleport, ArtifactManager.time,
				ArtifactManager.hunger, ArtifactManager.request);
		toAdd.add(MathUtils.choose(ArtifactManager.health, ArtifactManager.damage));
		boolean canAddMutator = false;
		boolean canRemoveMutator = false;
		if(MutatorManager.activeMutators.size() < 6 && GameType.getType().allowsMutators()) canAddMutator = true;
		if(MutatorManager.getMutatorsForDeactivation().size() > 0 && GameType.getType().allowsMutators()) canRemoveMutator = true;
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
			addedInfo.append(artifact.getName()).append(i == 2 ? "" : (ChatColor.DARK_GRAY + ", "));
			toAdd.remove(artifact);
		}
		for(Player currentPlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			currentPlayer.sendMessage(ChatColor.YELLOW + "Были активированы артефакты: " + addedInfo);
		}
		return true;
	}

	@Override
	public Material getType() {
		return Material.RABBIT_FOOT;
	}

}

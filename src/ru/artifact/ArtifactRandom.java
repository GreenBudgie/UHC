package ru.artifact;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.UHC.UHC;
import ru.mutator.Mutator;
import ru.mutator.MutatorManager;
import ru.util.MathUtils;
import ru.util.WorldHelper;

import java.util.ArrayList;
import java.util.List;

public class ArtifactRandom extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_PURPLE + "����";
	}

	@Override
	public String getDescription() {
		return "������������ ��� ��������� ��������������� ���������";
	}

	@Override
	public int getPrice() {
		return 20;
	}

	@Override
	public void onUse(Player p) {
		List<Artifact> toAdd = Lists.newArrayList(ArtifactManager.timeLeap, ArtifactManager.airdrop, ArtifactManager.teleport, ArtifactManager.time);
		toAdd.add(MathUtils.choose(ArtifactManager.health, ArtifactManager.damage));
		boolean canAddMutator = false;
		boolean canRemoveMutator = false;
		if(MutatorManager.activeMutators.size() < 6) canAddMutator = true;
		if(MutatorManager.getMutatorsForDeactivation().size() > 0) canRemoveMutator = true;
		if(canAddMutator && canRemoveMutator) {
			toAdd.add(MathUtils.choose(ArtifactManager.mutator, ArtifactManager.disableMutator));
		} else {
			if(canAddMutator) toAdd.add(ArtifactManager.mutator);
			if(canRemoveMutator) toAdd.add(ArtifactManager.disableMutator);
		}
		StringBuilder addedInfo = new StringBuilder();
		for(int i = 0; i < 3; i++) {
			Artifact artifact = MathUtils.choose(toAdd);
			artifact.onUse(p);
			addedInfo.append(artifact.getName()).append(i == 2 ? "" : (ChatColor.DARK_GRAY + ", "));
			toAdd.remove(artifact);
		}
		p.sendMessage(ChatColor.YELLOW + "���� ������������ ���������: " + addedInfo);
	}

	@Override
	public Material getType() {
		return Material.RABBIT_FOOT;
	}

}

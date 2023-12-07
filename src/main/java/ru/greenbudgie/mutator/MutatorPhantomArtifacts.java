package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.UHC.ArenaManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.artifact.Artifact;
import ru.greenbudgie.artifact.ArtifactManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.List;

public class MutatorPhantomArtifacts extends Mutator {

	private int timer = 0;

	@Override
	public Material getItemToShow() {
		return Material.BLACK_CARPET;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Фантомные Артефакты";
	}

	@Override
	public String getDescription() {
		return "Иногда будут самостоятельно активироваться случайные артефакты";
	}

	@Override
	public void onChoose() {
		reset();
	}

	private void reset() {
		timer = MathUtils.randomRange(250, 500);
	}

	@Override
	public void update() {
		if(UHC.state.isBeforeDeathmatch() || UHC.state.isDeathmatch()) {
			if(TaskManager.isSecUpdated()) {
				if(timer > 0) {
					timer--;
				} else {
					List<Artifact> availableArtifacts = getAvailableArtifacts();
					Artifact artifact = MathUtils.choose(availableArtifacts);
					artifact.use(null);
					reset();
				}
			}
		}
	}

	private List<Artifact> getAvailableArtifacts() {
		List<Artifact> availableArtifacts = ArtifactManager.artifacts
				.stream()
				.filter(Artifact::canBeUsedByMutator)
				.toList();
		if (!UHC.state.isDeathmatch()) {
			return availableArtifacts;
		}
		List<Artifact> arenaArtifacts = availableArtifacts.stream().filter(Artifact::canBeUsedOnArena).toList();
		boolean isArenaOpen = ArenaManager.getCurrentArena().isOpen();
		if (isArenaOpen) {
			return arenaArtifacts;
		}
		return arenaArtifacts.stream().filter(Artifact::canBeUsedOnClosedArena).toList();
	}

}

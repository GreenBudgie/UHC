package ru.greenbudgie.mutator;

import org.bukkit.Material;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.artifact.Artifact;
import ru.greenbudgie.artifact.ArtifactManager;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

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
		if(UHC.state.isInGame()) {
			if(TaskManager.isSecUpdated()) {
				if(timer > 0) {
					timer--;
				} else {
					Artifact artifact = MathUtils.choose(ArtifactManager.artifacts);
					artifact.use(null);
					reset();
				}
			}
		}
	}

}

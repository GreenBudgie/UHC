package ru.mutator;

import org.bukkit.Material;
import ru.UHC.UHC;
import ru.artifact.Artifact;
import ru.artifact.ArtifactManager;
import ru.util.MathUtils;
import ru.util.TaskManager;

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
		return "��������� ���������";
	}

	@Override
	public String getDescription() {
		return "������ ����� �������������� �������������� ��������� ���������";
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

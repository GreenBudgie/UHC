package ru.mutator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.TaskManager;

import java.util.List;
import java.util.stream.Collectors;

public class MutatorOneForAll extends Mutator implements Listener {

	private Mutator activatedMutator = null;
	private int timer = 0;

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public Material getItemToShow() {
		return Material.PURPLE_DYE;
	}

	@Override
	public String getName() {
		return "Один На Всех";
	}

	@Override
	public String getDescription() {
		return "Активирует случайные мутаторы, и через некоторое время деактивирует их";
	}

	@Override
	public void onChoose() {
		reset();
	}

	public void reset() {
		activatedMutator = null;
		timer = MathUtils.randomRange(60, 120); //60 120
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void onDeactivate() {
		if(activatedMutator != null) {
			activatedMutator.deactivate();
		}
	}

	@Override
	public void update() {
		if(UHC.state.isInGame()) {
			if(TaskManager.isSecUpdated()) {
				timer--;
				if(timer <= 0) {
					if(activatedMutator == null) {
						List<Mutator> mutators = MutatorManager.getNonConflictingInactiveMutators().stream()
								.filter(mutator -> mutator.canBeAddedFromArtifact() && mutator.canBeDeactivatedByArtifact()).collect(Collectors.toList());
						activatedMutator = MathUtils.choose(mutators);
						activatedMutator.activate(false, null);
						timer = MathUtils.randomRange(400, 600);
						for(Player p : UHC.getInGamePlayers()) {
							p.sendTitle("", ChatColor.GOLD + "Добавлен мутатор: " + ChatColor.LIGHT_PURPLE + activatedMutator.getName(), 5, 40, 15);
							p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);
						}
					} else { //FIXME NuLL
						for(Player p : UHC.getInGamePlayers()) {
							p.sendTitle("", ChatColor.GOLD + "Деактивирован мутатор: " + ChatColor.LIGHT_PURPLE + activatedMutator.getName(), 5, 40, 15);
							p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
						}
						activatedMutator.deactivate();
						reset();
					}
				}
			}
		}
	}

}

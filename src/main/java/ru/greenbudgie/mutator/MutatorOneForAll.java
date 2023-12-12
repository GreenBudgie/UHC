package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.List;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

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
		timer = MathUtils.randomRange(5, 5); //60 120
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
		if (!UHC.state.isBeforeDeathmatch()) {
			return;
		}
		if (!TaskManager.isSecUpdated()) {
			return;
		}
		timer--;
		if (timer > 0) {
			return;
		}
		if(activatedMutator == null) {
			List<Mutator> mutators = MutatorManager.getNonConflictingInactiveMutators().stream()
					.filter(mutator -> mutator.canBeAddedFromArtifact() && mutator.canBeDeactivatedByArtifact()).toList();
			activatedMutator = MutatorManager.haste;
			activatedMutator.activate(false, null);
			timer = MathUtils.randomRange(5, 5);
			for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
				p.sendTitle(" ", GOLD + "Добавлен мутатор: " + LIGHT_PURPLE + activatedMutator.getName(), 5, 40, 15);
				p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);
			}
		} else {
			for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
				p.sendTitle(" ", GOLD + "Деактивирован мутатор: " + LIGHT_PURPLE + activatedMutator.getName(), 5, 40, 15);
				p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
			}
			activatedMutator.deactivate();
			reset();
		}
	}

}

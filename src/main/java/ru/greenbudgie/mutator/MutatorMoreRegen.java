package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.util.EffectProcess;

public class MutatorMoreRegen extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.GHAST_TEAR;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Больше Регена!";
	}

	@Override
	public String getDescription() {
		return "Эффекты регенерации от любых источников длятся в два раза дольше";
	}

	@EventHandler
	public void increaseRegen(EntityPotionEffectEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!(event.getEntity() instanceof Player player)) {
			return;
		}
		if (EffectProcess.doIgnore(player, this)) {
			return;
		}
		if (!PlayerManager.isPlaying(player)) {
			return;
		}
		if (event.getAction() != EntityPotionEffectEvent.Action.ADDED
				&& event.getAction() != EntityPotionEffectEvent.Action.CHANGED) {
			return;
		}
		PotionEffect effect = event.getNewEffect();
		if (effect == null || !effect.getType().equals(PotionEffectType.REGENERATION)) {
			return;
		}
		event.setCancelled(true);
		EffectProcess.ignoreCurrentTick(player, this);
		player.addPotionEffect(new PotionEffect(
				PotionEffectType.REGENERATION,
				effect.getDuration() * 2,
				effect.getAmplifier(),
				effect.isAmbient(),
				effect.hasParticles(),
				effect.hasIcon()));
	}

}

package ru.mutator;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;

public class MutatorDangerWater extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.WATER_BUCKET;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Токсичная Вода";
	}

	@Override
	public String getDescription() {
		return "Вода накладывает эффект отравления при соприкосновении с ней";
	}

	@Override
	public boolean conflictsWithClasses() {
		return true;
	}

	@Override
	public void update() {
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			if(((CraftPlayer) p).getHandle().isInWater()) {
				PotionEffect effect = p.getPotionEffect(PotionEffectType.POISON);
				if(effect == null || effect.getDuration() <= 20) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 0));
				}
			}
		}
	}

	@Override
	public void onPlayerLeave(Player player) {
		if(((CraftPlayer) player).getHandle().isInWater()) {
			UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(player);
			if(uhcPlayer != null) {
				player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.DROWNING, 100));
				uhcPlayer.killOnLeave();
			}
		}
	}
}

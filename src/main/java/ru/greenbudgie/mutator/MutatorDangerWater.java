package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.UHCPlayerLeaveEvent;

public class MutatorDangerWater extends Mutator implements Listener {

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
	public void update() {
		for(Player p : PlayerManager.getAliveOnlinePlayers()) {
			if(p.isInWater()) {
				PotionEffect effect = p.getPotionEffect(PotionEffectType.POISON);
				if(effect == null || effect.getDuration() <= 20) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 0));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(UHCPlayerLeaveEvent event) {
		UHCPlayer uhcPlayer = event.getUHCPlayer();
		Player player = uhcPlayer.getPlayer();
		if(player.isInWater()) {
			player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.DROWNING, 100));
			uhcPlayer.killOnLeave();
		}
	}

}

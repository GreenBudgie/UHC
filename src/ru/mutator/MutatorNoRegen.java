package ru.mutator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import ru.UHC.UHC;

public class MutatorNoRegen extends Mutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.DEAD_BUSH;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Без Регена";
	}

	@Override
	public String getDescription() {
		return "Эффекты регенерации и моментального здоровья больше не накладываются ни при каких условиях";
	}

	@Override
	public void update() {
		for(Player p : UHC.players) {
			if(p.hasPotionEffect(PotionEffectType.REGENERATION)) {
				p.removePotionEffect(PotionEffectType.REGENERATION);
			}
		}
	}

	@EventHandler
	public void noSplash(PotionSplashEvent e) {
		if(e.getPotion().getEffects().stream()
				.anyMatch(effect -> PotionEffectType.REGENERATION.equals(effect.getType()) || PotionEffectType.HEAL.equals(effect.getType()))) {
			e.getPotion().getWorld().playSound(e.getPotion().getLocation(), Sound.ENTITY_PLAYER_BURP, 1F, 1F);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noSplash(LingeringPotionSplashEvent e) {
		if(e.getEntity().getEffects().stream()
				.anyMatch(effect -> PotionEffectType.REGENERATION.equals(effect.getType()) || PotionEffectType.HEAL.equals(effect.getType()))) {
			e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_PLAYER_BURP, 1F, 1F);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void noDrink(PlayerItemConsumeEvent e) {
		if(e.getItem().getType() == Material.POTION) {
			PotionMeta meta = (PotionMeta) e.getItem().getItemMeta();
			if(meta.hasCustomEffect(PotionEffectType.REGENERATION) || meta.hasCustomEffect(PotionEffectType.HEAL)
					|| meta.getBasePotionData().getType() == PotionType.REGEN || meta.getBasePotionData().getType() == PotionType.INSTANT_HEAL) {
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP, 1F, 1F);
				e.getItem().setType(Material.GLASS_BOTTLE);
				e.setCancelled(true);
			}
		}
	}

}

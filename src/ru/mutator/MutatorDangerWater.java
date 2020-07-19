package ru.mutator;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;

public class MutatorDangerWater extends Mutator {

	@Override
	public Material getItemToShow() {
		return Material.WATER_BUCKET;
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
		for(Player p : UHC.players) {
			if(((CraftPlayer) p).getHandle().isInWater()) {
				PotionEffect effect = p.getPotionEffect(PotionEffectType.POISON);
				if(effect == null || effect.getDuration() <= 20) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 0), true);
				}
			}
		}
	}

}

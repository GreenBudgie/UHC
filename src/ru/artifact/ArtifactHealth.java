package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.WorldHelper;

public class ArtifactHealth extends Artifact {

	@Override
	public String getName() {
		return ChatColor.GREEN + "Добродетель";
	}

	@Override
	public String getDescription() {
		return "Выдает всем игрокам эффект регенерации, восстанавливая 2 сердца. Использовавшему артефакт игроку восстанавливает 1.5 сердца.";
	}

	@Override
	public int getStartingPrice() {
		return 14;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(Player p) {
		for(Player player : UHC.players) {
			if(player != p) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 2), true);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 38, 2), true);
			}
			WorldHelper.spawnParticlesInRange(player.getLocation(), 3, Particle.HEART, null, 15);
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1F, 1F);
		}
	}

	@Override
	public Material getType() {
		return Material.RED_DYE;
	}

}

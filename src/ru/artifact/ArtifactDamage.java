package ru.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.WorldHelper;

public class ArtifactDamage extends Artifact {

	@Override
	public String getName() {
		return ChatColor.DARK_GRAY + "Злодеяние";
	}

	@Override
	public String getDescription() {
		return "Дамажит всех игроков на 2 сердца. Использовавшего игрока дамажит на 2.5 сердца. Умереть нельзя.";
	}

	@Override
	public int getStartingPrice() {
		return 16;
	}

	@Override
	public float getPriceIncreaseAmount() {
		return 1;
	}

	@Override
	public void onUse(Player p) {
		for(Player player : UHC.players) {
			if(player != p) {
				double damage = MathUtils.clamp(4, 0, player.getHealth() - 1);
				player.damage(damage);
			} else {
				double damage = MathUtils.clamp(5, 0, player.getHealth() - 1);
				player.damage(damage);
			}
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 0.5F);
			WorldHelper.spawnParticlesInRange(player.getLocation(), 3, Particle.SMOKE_LARGE, null, 15);
		}
	}

	@Override
	public Material getType() {
		return Material.FERMENTED_SPIDER_EYE;
	}

}

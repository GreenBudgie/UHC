package ru.mutator;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.TaskManager;

import java.util.List;

public class MutatorTotems extends ItemBasedMutator implements Listener {

	@Override
	public Material getItemToShow() {
		return Material.TOTEM_OF_UNDYING;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public String getName() {
		return "Бессмертные";
	}

	@Override
	public String getDescription() {
		return "Всем игрокам выдается тотем бессмертия, но эффект регенерации от него не такой мощный";
	}

	@Override
	public boolean isOnlyPreGame() {
		return false;
	}

	@Override
	public List<ItemStack> getItemsToAdd() {
		return Lists.newArrayList(new ItemStack(Material.TOTEM_OF_UNDYING));
	}

	@EventHandler
	public void resurrect(EntityResurrectEvent e) {
		if(!e.isCancelled() && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(UHC.isPlaying(p)) {
				TaskManager.invokeLater(() -> {

					PotionEffect regen = p.getPotionEffect(PotionEffectType.REGENERATION);
					if(regen != null) {
						p.addPotionEffect(new PotionEffect(regen.getType(), regen.getDuration() / 2, regen.getAmplifier() / 2, regen.isAmbient(), regen.hasParticles(),
								regen.hasIcon()), true);
					}

				});
			}
		}
	}

}

package ru.greenbudgie.mutator;

import com.google.common.collect.Lists;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.mutator.base.BossBarHolderMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class MutatorImmunity extends BossBarHolderMutator {

	private final int maxImmunityTime = 3 * 60;
	private int immunityTime = maxImmunityTime;
	private ImmunitySource immunity = null;

	public MutatorImmunity() {
		super(Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID));
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.SUPPORTING;
	}

	@Override
	public Material getItemToShow() {
		return Material.IRON_CHESTPLATE;
	}

	@Override
	public String getName() {
		return "Иммунитет";
	}

	@Override
	public String getDescription() {
		return "Каждые 3 минуты выдается иммунитет к случайному источнику урона";
	}

	@Override
	public void onChoose() {
		super.onChoose();
		changeImmunityAndReset();
		updateBar();
	}

	private void updateBar() {
		if(immunity != null) {
			bar.setTitle(getBarTitle());
		}
		bar.setProgress((double) immunityTime / maxImmunityTime);
	}

	private String getBarTitle() {
		if(immunity == null) return "";
		return AQUA + "Иммунитет" + GRAY + ": " + DARK_AQUA + BOLD + immunity.description;
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		immunity = null;
	}

	private void changeImmunityAndReset() {
		immunityTime = maxImmunityTime;
		chooseImmunity();
	}

	private void chooseImmunity() {
		List<ImmunitySource> availableImmunities = Lists.newArrayList(ImmunitySource.values());
		if(immunity != null) availableImmunities.remove(immunity);
		immunity = MathUtils.choose(availableImmunities);
	}

	@Override
	public void update() {
		if(TaskManager.isSecUpdated()) {
			immunityTime--;
			if(immunityTime <= 0) {
				changeImmunityAndReset();
			}
			updateBar();
		}
	}

	@EventHandler
	public void absorbDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player player && PlayerManager.isPlaying(player) &&
				immunity != null && immunity.doAbsorb(event.getCause())) {
			ParticleUtils.createParticlesOutlineSphere(player.getEyeLocation(), 1.7, Particle.REDSTONE, Color.AQUA, 20);
			player.getWorld().playSound(player.getLocation(), Sound.ITEM_HOE_TILL, 0.3F, 0.5F);
			event.setCancelled(true);
		}
	}

	private enum ImmunitySource {

		MELEE("Ближний Бой", EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK),
		PROJECTILE("Снаряды", EntityDamageEvent.DamageCause.PROJECTILE),
		FIRE("Огонь, Лава, Магма",
				EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK,
				EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.HOT_FLOOR),
		FALL("Падение", EntityDamageEvent.DamageCause.FALL),
		EXPLOSION("Взрывы", EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);

		final List<EntityDamageEvent.DamageCause> sources;
		final String description;

		ImmunitySource(String description, EntityDamageEvent.DamageCause... sources) {
			this.description = description;
			this.sources = Arrays.asList(sources);
		}

		boolean doAbsorb(EntityDamageEvent.DamageCause cause) {
			return this.sources.contains(cause);
		}

	}

}

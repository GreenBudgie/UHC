package ru.mutator;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.TaskManager;
import ru.util.WorldHelper;

import java.util.HashSet;
import java.util.Set;

public class MutatorStop extends Mutator implements Listener {

	private int cooldown = 0;
	private int timeToStop = 3;
	private int timeToRemoveStop = 3;
	private boolean isStopped = false;
	private Set<Player> intruders = new HashSet<>();

	@Override
	public Material getItemToShow() {
		return Material.BARRIER;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Замри!";
	}

	@Override
	public String getDescription() {
		return "При появлении надписи \"Замри...\" нужно срочно перестать двигаться. Любому нарушителю выдается эффект иссушения";
	}

	private int getRandomCooldown() {
		return MathUtils.randomRange(25, 110);
	}

	@Override
	public void onChoose() {
		reset();
	}

	private void reset() {
		isStopped = false;
		intruders.clear();
		timeToStop = 3;
		timeToRemoveStop = 3;
		cooldown = getRandomCooldown();
	}

	@Override
	public void update() {
		if(cooldown <= 0) {
			if(timeToStop <= 0) {
				if(TaskManager.isSecUpdated()) {
					if(!isStopped) {
						for(Player p : UHC.getInGamePlayers()) {
							p.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Замри!", "", 0, 100, 0);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1F);
						}
						isStopped = true;
					} else {
						if(timeToRemoveStop <= 0) {
							for(Player p : UHC.getInGamePlayers()) {
								p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Можно идти!", "", 0, 30, 10);
								p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1.5F);
							}
							reset();
						} else {
							timeToRemoveStop--;
						}
					}
				}
			} else {
				if(TaskManager.tick % 10 == 0) {
					ChatColor color = ChatColor.BLACK;
					switch(timeToStop) {
					case 3:
						color = ChatColor.DARK_GRAY;
						break;
					case 2:
						color = ChatColor.GRAY;
						break;
					case 1:
						color = ChatColor.WHITE;
						break;
					}
					for(Player p : UHC.getInGamePlayers()) {
						String dots = StringUtils.repeat(".", 4 - timeToStop);
						p.sendTitle(color + "Замри" + dots, color + String.valueOf(timeToStop), 0, 30, 0);
						p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, (4 - timeToStop) * 0.2F, (4 - timeToStop) * 0.2F + 1F);
					}
					timeToStop--;
				}
			}
		} else {
			if(TaskManager.isSecUpdated()) {
				cooldown--;
			}
		}
	}

	@EventHandler
	public void handleMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(isStopped && e.getTo() != null && UHC.isPlaying(p) && !intruders.contains(p) && !WorldHelper.compareLocations(e.getFrom(), e.getTo())) {
			intruders.add(p);
			p.playSound(p.getLocation(), Sound.ENTITY_PHANTOM_DEATH, 1F, 0.7F);
			p.sendTitle(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Ты пошевелился!", "", 0, 100, 0);
			ParticleUtils.createParticlesAround(p, Particle.SMOKE_LARGE, null, 15);
			p.damage(2);
			p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, false, false));
		}
	}

}

package ru.mutator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.UHC.GameState;
import ru.UHC.UHC;
import ru.util.MathUtils;
import ru.util.TaskManager;

import java.util.HashMap;
import java.util.Map;

public class MutatorOxygen extends Mutator implements Listener {

	private Map<Player, Double> oxygen = new HashMap<>();
	private Map<Player, BossBar> bars = new HashMap<>();

	@Override
	public Material getItemToShow() {
		return Material.DRAGON_BREATH;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Кислородное Голодание";
	}

	@Override
	public String getDescription() {
		return "Теперь у игроков появляется шкала кислорода. Под землей и в горах кислород тратится. При падении до 0 можно начать умирать!";
	}

	@Override
	public void onChoose() {
		for(Player p : UHC.players) {
			oxygen.put(p, 100.0);
			createBar(p);
		}
	}

	public void createBar(Player p) {
		BossBar bar = Bukkit.createBossBar(ChatColor.GRAY + "Кислород", BarColor.WHITE, BarStyle.SOLID);
		bar.setProgress(1);
		bar.setColor(BarColor.WHITE);
		bar.addPlayer(p);
		bar.setVisible(true);
		bars.put(p, bar);
	}

	public void unregister(Player p) {
		BossBar bar = getBar(p);
		if(bar != null) {
			bar.setVisible(false);
			bar.removeAll();
			bars.remove(p);
		}
		oxygen.remove(p);
	}

	public BossBar getBar(Player p) {
		return bars.get(p);
	}

	private double getOxygen(Player p) {
		return oxygen.getOrDefault(p, -1.0);
	}

	private double getChange(Player p) {
		int y = p.getLocation().getBlockY();
		if(y < 40) {
			return -MathUtils.clamp(1 - y / 40.0, 0, 1);
		} else if(y > 100) {
			return -MathUtils.clamp((y - 100) / 155.0, 0, 1);
		} else return MathUtils.clamp((-1 / 900.0 * y * y) + (7 / 45.0 * y) - (40 / 9.0), 0, 1);
	}

	@Override
	public void onDeactivate() {
		for(Player p : bars.keySet()) {
			BossBar bar = bars.get(p);
			bar.setVisible(false);
			bar.removeAll();
		}
		bars.clear();
		oxygen.clear();
	}

	@Override
	public void update() {
		if(TaskManager.isSecUpdated()) {
			if(UHC.state.isInGame()) {
				for(Player p : UHC.players) {
					double change = getChange(p);
					double oxygen = MathUtils.clamp(getOxygen(p) + change, 0, 100);
					this.oxygen.replace(p, oxygen);
					BossBar bar = getBar(p);
					String info;
					if(change >= 0) {
						info = ChatColor.GREEN + " +" + MathUtils.decimal(change * 10, 1);
					} else {
						info = ChatColor.RED + " " + MathUtils.decimal(change * 10, 1);
					}
					bar.setTitle(ChatColor.GRAY + "Кислород" + info);
					bar.setProgress(oxygen / 100);
					if(!bar.isVisible()) bar.setVisible(true);
					if(oxygen >= 20) {
						bar.setColor(BarColor.WHITE);
					} else if(oxygen < 20) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0));
						bar.setColor(BarColor.RED);
					}
					if(oxygen == 0) {
						p.damage(1);
					}
				}
			} else {
				for(Player p : UHC.players) {
					BossBar bar = getBar(p);
					bar.setVisible(false);
				}
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

}

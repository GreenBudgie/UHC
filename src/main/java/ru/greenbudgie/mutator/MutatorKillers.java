package ru.greenbudgie.mutator;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.SpectatorLeaveEvent;
import ru.greenbudgie.event.UHCPlayerDeathEvent;
import ru.greenbudgie.event.UHCPlayerLeaveEvent;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.*;

import javax.annotation.Nullable;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class MutatorKillers extends Mutator implements Listener {

	private static final int TIME_TO_KILL = 40 * 60;
	private static final int FAILURE_DAMAGE = 16;
	private static final int SUCCESS_HEAL = 16;
	private static final PotionEffect SUCCESS_EFFECT = new PotionEffectBuilder(PotionEffectType.REGENERATION)
			.seconds(10)
			.amplifier(2)
			.build();

	private int timer;
	private boolean registerDeaths = true;

	private final Map<Player, BossBar> bossBars = new HashMap<>();
	private final Map<UHCPlayer, UHCPlayer> killerToVictim = new HashMap<>();

	@Override
	public Material getItemToShow() {
		return Material.DIAMOND_SWORD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Киллеры";
	}

	@Override
	public String getDescription() {
		return "Каждому игроку выдается задание - убить другого определенного игрока за 40 минут. " +
				"Игроки всегда видят координаты своих жертв. Если ты успел убить игрока или он умер самостоятельно, " +
				"то ты регенерируешь 8 сердец. Если же задание было провалено - теряешь 8 сердец. " +
				"От этого можно умереть! Нельзя деактивировать артефактом.";
	}

	@Override
	public void onChoose() {
		registerDeaths = true;
		selectKillersAndVictims();
		resetTimer();
	}

	@Override
	public void onDeactivate() {
		reset();
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

	@Override
	public boolean canBeDeactivatedByArtifact() {
		return false;
	}

	@Override
	public void update() {
		if (TaskManager.isSecUpdated()) {
			timer--;
			if (timer <= 0) {
				timeEnd();
				return;
			}
		}
		if (TaskManager.ticksPassed(4)) {
			updateBars();
		}
	}

	@Override
	public boolean containsBossBar() {
		return true;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	private void selectKillersAndVictims() {
		List<UHCPlayer> remainingVictims = new ArrayList<>(PlayerManager.getPlayers());
		for (UHCPlayer killer : PlayerManager.getPlayers()) {
			List<UHCPlayer> availableVictims = remainingVictims.stream()
					.filter(currentVictim -> currentVictim != killer)
					.filter(currentVictim -> currentVictim != killer.getTeammate())
					.toList();
			if (availableVictims.isEmpty()) {
				continue;
			}
			UHCPlayer victim = MathUtils.choose(availableVictims);
			killerToVictim.put(killer, victim);
			remainingVictims.remove(victim);
		}
	}

	@Nullable
	private UHCPlayer getKillerByVictim(UHCPlayer victim) {
		return killerToVictim.entrySet().stream()
				.filter(entry -> entry.getValue() == victim)
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse(null);
	}

	private void resetTimer() {
		timer = TIME_TO_KILL;
	}

	private void timeEnd() {
		registerDeaths = false;
		List<UHCPlayer> failedKillers = new ArrayList<>();
		for (UHCPlayer killer : killerToVictim.keySet()) {
			UHCPlayer victim = killerToVictim.get(killer);
			if (victim.isAlive()) {
				failedKillers.add(killer);
			}
		}
		failedKillers.forEach(this::failure);
		deactivate();
	}

	private void success(UHCPlayer killer) {
		UHCPlayer victim = killerToVictim.get(killer);
		if (killer.isOnline()) {
			Player player = killer.getPlayer();
			player.addPotionEffect(SUCCESS_EFFECT);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1F, 0.7F);
			player.sendTitle(
					"",
					GOLD + victim.getNickname() + GREEN + BOLD + " погиб, ты справился!",
					10,
					60,
					20
			);
			ParticleUtils.createParticlesAround(player, Particle.REDSTONE, Color.GREEN, 20);
			return;
		}
		killer.addOfflineHealth(SUCCESS_HEAL);
	}

	private void failure(UHCPlayer killer) {
		UHCPlayer victim = killerToVictim.get(killer);
		if (killer.isOnline()) {
			Player player = killer.getPlayer();
			player.damage(FAILURE_DAMAGE);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PILLAGER_HURT, 1F, 0.5F);
			player.sendTitle(
					"",
					RED + "" + BOLD + "Ты не успел убить " + GOLD + victim.getNickname(),
					10,
					60,
					20
			);
			ParticleUtils.createParticlesAround(player, Particle.REDSTONE, Color.RED, 20);
			return;
		}
		killer.addOfflineHealth(-FAILURE_DAMAGE);
	}

	private void reset() {
		bossBars.values().forEach(bar -> {
			bar.setVisible(false);
			bar.removeAll();
		});
		bossBars.clear();
		killerToVictim.clear();
	}

	private void updateBars() {
		for (Player player : PlayerManager.getAliveOnlinePlayers()) {
			BossBar bar = bossBars.computeIfAbsent(player, this::createDefaultBossBar);
			updateBarForPlayer(player, bar);
		}
		for (Player spectator : PlayerManager.getSpectators()) {
			BossBar bar = bossBars.computeIfAbsent(spectator, this::createDefaultBossBar);
			updateBarForSpectator(bar);
		}
	}

	private void updateBarForPlayer(Player player, BossBar bar) {
		bar.setProgress(MathUtils.clamp(timer / (double) TIME_TO_KILL, 0, 1));
		setBarNameForPlayer(player, bar);
	}

	private void updateBarForSpectator(BossBar bar) {
		bar.setProgress(MathUtils.clamp(timer / (double) TIME_TO_KILL, 0, 1));
		setBarNameForSpectator(bar);
	}

	private void setBarNameForPlayer(Player player, BossBar bar) {
		String timeInfo = DARK_GRAY + " (" + DARK_AQUA + BOLD +
				MathUtils.formatTime(timer) + DARK_GRAY + ")";
		UHCPlayer killer = Objects.requireNonNull(PlayerManager.asUHCPlayer(player));
		UHCPlayer victim = killerToVictim.get(killer);
		if (victim == null) {
			bar.setTitle(RED + "" + BOLD + "Нет жертвы" + timeInfo);
			return;
		}
		String prefix = RED + "" + BOLD + "Жертва" + GRAY + ": ";
		if (!victim.isAlive()) {
			String victimInfo = DARK_RED + "" + STRIKETHROUGH + victim.getNickname();
			bar.setTitle(prefix + victimInfo + timeInfo);
			return;
		}
		String victimInfo = GOLD + victim.getNickname();
		String locationInfo = LocationFormatter.formatToWithDistanceAndArrow(
				player.getLocation(),
				victim.getLocation(),
				DARK_AQUA,
				WHITE,
				AQUA,
				DARK_GRAY,
				AQUA,
				true
		);
		bar.setTitle(prefix + victimInfo + " " + locationInfo + timeInfo);
	}

	private void setBarNameForSpectator(BossBar bar) {
		String timeInfo = DARK_AQUA + "" + BOLD + MathUtils.formatTime(timer);
		bar.setTitle(RED + "" + BOLD + "До окончания охоты" + GRAY + ": " + timeInfo);
	}

	private BossBar createDefaultBossBar(Player player) {
		BossBar bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		bar.setVisible(true);
		bar.addPlayer(player);
		return bar;
	}

	@EventHandler
	public void playerLeave(UHCPlayerLeaveEvent event) {
		removeBossBar(event.getUHCPlayer().getPlayer());
	}

	@EventHandler
	public void spectatorLeave(SpectatorLeaveEvent event) {
		removeBossBar(event.getPlayer());
	}

	@EventHandler
	public void victimDeath(UHCPlayerDeathEvent event) {
		if (!registerDeaths) {
			return;
		}
		UHCPlayer player = event.getUHCPlayer();
		UHCPlayer killer = getKillerByVictim(player);
		if (killer == null) {
			return;
		}
		success(killer);
	}

	private void removeBossBar(Player player) {
		if (bossBars.containsKey(player)) {
			BossBar bar = bossBars.get(player);
			bar.removeAll();
			bar.setVisible(false);
			bossBars.remove(player);
		}
	}

}

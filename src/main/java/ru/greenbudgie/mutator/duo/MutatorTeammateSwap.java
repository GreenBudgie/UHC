package ru.greenbudgie.mutator.duo;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.PlayerTeam;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.SpectatorJoinEvent;
import ru.greenbudgie.event.SpectatorLeaveEvent;
import ru.greenbudgie.event.UHCPlayerLeaveEvent;
import ru.greenbudgie.event.UHCPlayerRejoinEvent;
import ru.greenbudgie.mutator.Mutator;
import ru.greenbudgie.mutator.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import java.util.List;
import java.util.Objects;

import static org.bukkit.ChatColor.*;

public class MutatorTeammateSwap extends Mutator implements Listener {

	// +3 seconds to make it off-sync with the game timer
	private final static int MAX_TIMER = 303;
	private final BossBar bar = Bukkit.createBossBar(
			"",
			BarColor.WHITE,
			BarStyle.SOLID
	);;
	private int timer = MAX_TIMER;

	@Override
	public Material getItemToShow() {
		return Material.ENDER_PEARL;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.INNOCENT;
	}

	@Override
	public String getName() {
		return "Вперемешку";
	}

	@Override
	public String getDescription() {
		return "Каждые 5 минут все тиммейты меняются местами";
	}

	@Override
	public boolean isDuoOnly() {
		return true;
	}

	@Override
	public boolean containsBossBar() {
		return true;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void onChoose() {
		for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
			bar.addPlayer(player);
		}
		bar.setVisible(true);
		timer = MAX_TIMER;
		updateBar();
	}

	@Override
	public void onDeactivate() {
		bar.removeAll();
		bar.setVisible(false);
	}

	@Override
	public void update() {
		if (!TaskManager.isSecUpdated()) {
			return;
		}
		updateBar();
		if (timer > 0) {
			timer--;
			return;
		}
		swapPlayers();
		timer = MAX_TIMER;
	}

	private void swapPlayers() {
		for (PlayerTeam team : PlayerManager.getAliveTeams()) {
			if (team.isOneOrNoneAlive()) {
				continue;
			}
			UHCPlayer player1 = team.getPlayer1();
			UHCPlayer player2 = team.getPlayer2();
			Location player1Location = player1.getLocation().clone();
			Location player2Location = player2.getLocation().clone();
			player1.teleport(player2Location);
			player2.teleport(player1Location);
			List<LivingEntity> playersOrGhosts = List.of(
					Objects.requireNonNull(player1.getPlayerOrGhost()),
                    Objects.requireNonNull(player2.getPlayerOrGhost())
			);
			for (LivingEntity entity : playersOrGhosts) {
				ParticleUtils.createParticlesAround(entity, Particle.REDSTONE, Color.PURPLE, 30);
				entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1.5F);
			}
		}
	}

	private void updateBar() {
		bar.setTitle(AQUA + "До смены тиммейтов" + GRAY + ": " + DARK_AQUA + BOLD + MathUtils.formatTime(timer));
		bar.setProgress(timer / (double) MAX_TIMER);
	}

	@EventHandler
	public void playerLeave(UHCPlayerLeaveEvent event) {
		bar.removePlayer(event.getUHCPlayer().getPlayer());
	}

	@EventHandler
	public void playerRejoin(UHCPlayerRejoinEvent event) {
		bar.addPlayer(event.getUHCPlayer().getPlayer());
	}

	@EventHandler
	public void spectatorJoin(SpectatorJoinEvent event) {
		bar.addPlayer(event.getPlayer());
	}

	@EventHandler
	public void spectatorLeave(SpectatorLeaveEvent event) {
		bar.removePlayer(event.getPlayer());
	}

}

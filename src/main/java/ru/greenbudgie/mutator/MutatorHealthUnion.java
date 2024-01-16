package ru.greenbudgie.mutator;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.PlayerTeam;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.UHCPlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

public class MutatorHealthUnion extends Mutator implements Listener {

	private final Map<PlayerTeam, Double> previousTeamHealth = new HashMap<>();

	@Override
	public Material getItemToShow() {
		return Material.BEETROOT;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.CRITICAL;
	}

	@Override
	public String getName() {
		return "Единый Организм";
	}

	@Override
	public String getDescription() {
		return "Два тиммейта разделяют свое здоровье! Когда один получает урон, второму также наносится этот урон. " +
				"Также работает и с эффектами регенерации. Это значит, что у союзников всегда одинаковое количество " +
				"здоровья, и если один умрет - то заберет с собой и второго! Если игрок стартует без тиммейта, то на " +
				"него этот эффект не распространяется.";
	}

	@Override
	public boolean conflictsWith(Mutator another) {
		return another == MutatorManager.damageBound;
	}

	@Override
	public boolean isDuoOnly() {
		return true;
	}

	@Override
	public void onChoose() {
		for (PlayerTeam team : PlayerManager.getAliveTeams()) {
			if (shouldSkipTeamProcessing(team)) {
				continue;
			}
			UHCPlayer player1 = team.getPlayer1();
			UHCPlayer player2 = team.getPlayer2();
			double player1Health = player1.getRealOrOfflineHealth();
			double player2Health = player2.getRealOrOfflineHealth();
			if (player1Health == player2Health) {
				continue;
			}
			double averageHealth = (player1Health + player2Health) / 2.0;
			setHealth(player1, averageHealth);
			setHealth(player2, averageHealth);
		}
		updatePreviousHealth();
	}

	@Override
	public void onDeactivate() {
		previousTeamHealth.clear();
	}

	@Override
	public void update() {
		for (PlayerTeam team : PlayerManager.getAliveTeams()) {
			if (shouldSkipTeamProcessing(team)) {
				continue;
			}
			UHCPlayer player1 = team.getPlayer1();
			UHCPlayer player2 = team.getPlayer2();
			double player1Health = player1.getRealOrOfflineHealth();
			double player2Health = player2.getRealOrOfflineHealth();
			if (player1Health == player2Health) {
				continue;
			}
			double previousTeamHealth = this.previousTeamHealth.get(team);
			// If both players' health has changed at the same tick, apply the average health
			if (player1Health != previousTeamHealth && player2Health != previousTeamHealth) {
				double averageHealth = (player1Health + player2Health) / 2.0;
				setHealth(player1, averageHealth);
				setHealth(player2, averageHealth);
				continue;
			}
			// If player 1 health has changed, set player 2 health to this value
			if (player1Health != previousTeamHealth) {
				setHealth(player2, player1Health);
				continue;
			}
			// If player 2 health has changed, set player 1 health to this value
            setHealth(player1, player2Health);
        }
		updatePreviousHealth();
	}

	private void setHealth(UHCPlayer player, double health) {
		if (player.isAliveAndOnline()) {
			player.getPlayer().setHealth(Math.max(0, health));
			return;
		}
		player.setOfflineHealth(health);
	}

	private void updatePreviousHealth() {
		for (PlayerTeam team : PlayerManager.getAliveTeams()) {
			if (shouldSkipTeamProcessing(team)) {
				continue;
			}
			previousTeamHealth.put(team, team.getPlayer1().getRealOrOfflineHealth());
		}
	}

	@EventHandler
	public void onPlayerDeath(UHCPlayerDeathEvent event) {
		UHCPlayer player = event.getUHCPlayer();
		UHCPlayer teammate = player.getTeammate();
		if (teammate != null && teammate.isAlive()) {
			EntityDamageEvent damageCause = player.isOnline() ?
					player.getPlayer().getLastDamageCause() :
					player.getGhost().getLastDamageCause();
			teammate.getPlayer().setLastDamageCause(damageCause);
			teammate.kill();
		}
	}

	private boolean shouldSkipTeamProcessing(PlayerTeam team) {
		return !team.isDual() || !team.allPlayersAlive();
	}

}

package ru.greenbudgie.mutator;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffectType;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.UHCPlayerDeathEvent;
import ru.greenbudgie.mutator.base.BossBarHolderMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.PotionEffectBuilder;
import ru.greenbudgie.util.TaskManager;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.bukkit.ChatColor.*;

public class MutatorBlackMark extends BossBarHolderMutator implements Listener {

	private final static int MAX_TIMER = 60 * 30;

	private int timer = MAX_TIMER;
	private UHCPlayer blackMarkHolder;
	private boolean changeHolder = true;

	public MutatorBlackMark() {
		super(Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID));
	}

	@Override
	public Material getItemToShow() {
		return Material.ECHO_SHARD;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Черная Метка";
	}

	@Override
	public String getDescription() {
		return "Черная метка - это проклятие, которое убивает своего носителя через 30 минут от первого его " +
				"появления в игре. Сначала метку получает случайный игрок. " +
				"Ее можно передать другому игроку, подойдя к нему вплотную и нажав ПКМ. " +
				"Однако, если убить игрока с черной меткой, то ты получишь ее проклятие! " +
				"Если же ее носитель умрет сам, то метка наложится на ближайшего к нему игрока. " +
				"Когда 30 минут пройдет и носитель умрет, этот мутатор деактивируется." +
				"Не может быть деактивирован мутатором!";
	}

	@Override
	public void onChoose() {
		super.onChoose();
		changeHolder = true;
		giveMarkToRandomPlayer();
		timer = MAX_TIMER;
		updateBar();
	}

	@Override
	public void update() {
		if (!TaskManager.isSecUpdated()) {
			return;
		}
		updateBar();
		if (blackMarkHolder.isOnline()) {
			Player player = blackMarkHolder.getPlayer();
			player.addPotionEffect(
					new PotionEffectBuilder(PotionEffectType.SPEED).seconds(3).build()
			);
		}
 		if (timer > 0) {
			timer--;
			return;
		}
		killPlayer();
		deactivate();
	}

	@Override
	public boolean canBeAddedFromArtifact() {
		return false;
	}

	@Override
	public boolean canBeDeactivatedByArtifact() {
		return false;
	}

	private void killPlayer() {
		for (Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			inGamePlayer.sendMessage(
					DARK_RED + "" + BOLD + "Черная метка убила " +
							GOLD + blackMarkHolder.getNickname()
			);
		}
		changeHolder = false;
		blackMarkHolder.kill();
	}

	private void giveBlackMarkTo(UHCPlayer uhcPlayer) {
		if (blackMarkHolder != null) {
			if (blackMarkHolder.isOnline()) {
				blackMarkHolder.getPlayer().addPotionEffect(
						new PotionEffectBuilder(PotionEffectType.SPEED).seconds(3).amplifier(1).build()
				);
			}
			ParticleUtils.createParticlesAround(
					blackMarkHolder.getPlayerOrGhost(),
					Particle.CLOUD,
					null,
					20
			);
		}
		Objects.requireNonNull(uhcPlayer.getLocation().getWorld()).playSound(
				uhcPlayer.getLocation(),
				Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,
				0.8F,
				1F
		);
		if (uhcPlayer.isOnline()) {
			uhcPlayer.getPlayer().addPotionEffect(
					new PotionEffectBuilder(PotionEffectType.BLINDNESS).seconds(2).build()
			);
		}
		ParticleUtils.createParticlesAround(
				uhcPlayer.getPlayerOrGhost(),
				Particle.SQUID_INK,
				null,
				20
		);
		for (Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
			inGamePlayer.sendMessage(
					DARK_RED + "" + BOLD + "Черная метка наложена на " +
							GOLD + uhcPlayer.getNickname()
			);
		}
		blackMarkHolder = uhcPlayer;
	}

	private void giveMarkToRandomPlayer() {
		UHCPlayer randomMarkHolder = getRandomPlayerExceptHolder();
		if (randomMarkHolder == null) {
			deactivate();
			return;
		}
		giveBlackMarkTo(randomMarkHolder);
	}

	@Nullable
	private UHCPlayer getRandomPlayerExceptHolder() {
		List<UHCPlayer> players = PlayerManager.getAlivePlayers().stream()
				.filter(uhcPlayer -> uhcPlayer != blackMarkHolder)
				.toList();
		if (players.isEmpty()) {
			return null;
		}
		return MathUtils.choose(players);
	}

	private void giveMarkToClosestPlayer() {
		Location holderLocation = blackMarkHolder.getLocation();
		Comparator<UHCPlayer> distanceComparator = Comparator.comparingDouble(uhcPlayer ->
				uhcPlayer.getLocation().distanceSquared(holderLocation)
		);
		UHCPlayer closestPlayer = PlayerManager.getAlivePlayers().stream()
				.filter(uhcPlayer -> uhcPlayer != blackMarkHolder)
				.filter(uhcPlayer -> uhcPlayer.getLocation().getWorld() == holderLocation.getWorld())
				.min(distanceComparator)
				.orElse(getRandomPlayerExceptHolder());
		if (closestPlayer == null) {
			deactivate();
			return;
		}
		giveBlackMarkTo(closestPlayer);
	}

	private void updateBar() {
		bar.setTitle(
				DARK_RED + "" + BOLD + "Черная метка на " + GOLD + blackMarkHolder.getNickname() +
						DARK_GRAY + " (" +
						DARK_AQUA + BOLD + MathUtils.formatTime(timer) +
						DARK_GRAY + ")"
		);
		bar.setProgress(timer / (double) MAX_TIMER);
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!UHC.state.isGameActive()) {
			return;
		}
		UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(event.getPlayer());
		if (uhcPlayer == null) {
			return;
		}
		if (blackMarkHolder != uhcPlayer) {
			return;
		}
		Entity clickedEntity = event.getRightClicked();
		if (clickedEntity instanceof Player clickedPlayer) {
			UHCPlayer clickedUhcPlayer = PlayerManager.asUHCPlayer(clickedPlayer);
			if (clickedUhcPlayer == null) {
				return;
			}
			if (!clickedUhcPlayer.isAlive()) {
				return;
			}
			giveBlackMarkTo(clickedUhcPlayer);
		}
		if (!(clickedEntity instanceof ArmorStand armorStand)) {
			return;
		}
		UHCPlayer clickedUhcPlayer = PlayerManager.getPlayerFromGhost(armorStand);
		if (clickedUhcPlayer == null) {
			return;
		}
		giveBlackMarkTo(clickedUhcPlayer);
	}

	@EventHandler
	public void onPlayerDeath(UHCPlayerDeathEvent event) {
		if (!changeHolder) {
			return;
		}
		if (event.getUHCPlayer() != blackMarkHolder) {
			return;
		}
		if (event.getKiller() != null) {
			giveBlackMarkTo(event.getKiller());
			return;
		}
		giveMarkToClosestPlayer();
	}

}

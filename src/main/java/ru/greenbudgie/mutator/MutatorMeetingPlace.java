package ru.greenbudgie.mutator;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.UHC.WorldManager;
import ru.greenbudgie.mutator.base.BossBarHolderMutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.Region;
import ru.greenbudgie.util.TaskManager;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public class MutatorMeetingPlace extends BossBarHolderMutator {

	private Location meetingLoc;
	private final int maxCooldown = 840;
	private int cooldown = maxCooldown;
	private int meetingDelay = 10;
	private boolean isMeeting = false;
	private double prevBorderSize = -1;
	private final int radius = 16;

	private final Set<UHCPlayer> safePlayers = new HashSet<>();

	public MutatorMeetingPlace() {
		super(Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SOLID));
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public Material getItemToShow() {
		return Material.LIGHT_WEIGHTED_PRESSURE_PLATE;
	}

	@Override
	public String getName() {
		return "Место Сбора";
	}

	@Override
	public String getDescription() {
		return "Каждые 14 минут все игроки должны собраться в центре карты (не обязательно на поверхности), иначе будут убиты. В аду безопасно!";
	}

	private String getPos() {
		return DARK_GRAY + "(" + AQUA + meetingLoc.getBlockX() + WHITE + ", " + AQUA + meetingLoc.getBlockZ() + DARK_GRAY + ")";
	}

	public boolean isSafe(Location location) {
		if(location.getWorld() != WorldManager.getGameMap()) return true;
		return location.getX() < meetingLoc.getX() + radius &&
				location.getX() > meetingLoc.getX() - radius &&
				location.getZ() < meetingLoc.getZ() + radius &&
				location.getZ() > meetingLoc.getZ() - radius;
	}

	@Override
	public void onChoose() {
		super.onChoose();
		meetingLoc = WorldManager.spawnLocation.clone().add(0, 2, 0);
		reset();
		updateBar();
		safePlayers.clear();
		for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
			Location location = uhcPlayer.getLocation();
			if(location != null && isSafe(location)) {
				safePlayers.add(uhcPlayer);
			}
		}
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		WorldBorder border = WorldManager.getGameMap().getWorldBorder();
		if(prevBorderSize != -1) {
			border.setSize(prevBorderSize);
		}
		border.setWarningTime(0);
		border.setWarningDistance(0);
		border.setDamageBuffer(0);
		border.setDamageAmount(0);
		prevBorderSize = -1;
	}

	private void reset() {
		cooldown = maxCooldown;
		meetingDelay = 10;
		isMeeting = false;
		bar.setProgress(1);
		bar.setColor(BarColor.YELLOW);
		WorldBorder border = WorldManager.getGameMap().getWorldBorder();
		border.setWarningTime(0);
		border.setWarningDistance(0);
		border.setDamageBuffer(0);
		border.setDamageAmount(0);
	}

	@Override
	public void update() {
        if (!TaskManager.isSecUpdated()) {
            return;
        }
        if (!UHC.state.isBeforeDeathmatch()) {
            bar.setVisible(false);
			return;
        }
		for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
			UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(currentPlayer);
			if(uhcPlayer != null) {
				Location location = currentPlayer.getLocation();
				if(safePlayers.contains(uhcPlayer) && !isSafe(location)) {
					currentPlayer.sendTitle(" ", DARK_RED + "" + BOLD + "> " +
							RED + "Теперь ты в опасности!" + DARK_RED + BOLD + " <",
							5, 40, 10);
					currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
					safePlayers.remove(uhcPlayer);
				} else if(!safePlayers.contains(uhcPlayer) && isSafe(location)) {
					currentPlayer.sendTitle(" ", DARK_GREEN + "" + BOLD + "> " +
									GREEN + "Теперь ты в безопасности!" + DARK_GREEN + BOLD + " <",
							5, 40, 10);
					currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 1.5F);
					safePlayers.add(uhcPlayer);
				}
			}
		}
		if(cooldown <= 0) {
			if(meetingDelay <= 0) {
				for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
					p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1F, 1F);
					p.sendTitle(GREEN + "Сбор окончен!", "", 10, 30, 10);
				}
				WorldManager.getGameMap().getWorldBorder().setSize(prevBorderSize, 10);
				reset();
			} else {
				if(!isMeeting) {
					for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
						p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
						p.sendTitle(DARK_RED + "" + BOLD + "Все на сбор!", "", 2, 30, 10);
					}
					bar.setProgress(0);
					bar.setColor(BarColor.RED);
					bar.setTitle(DARK_RED + "" + BOLD + "Сбор!");
					prevBorderSize = WorldManager.getGameMap().getWorldBorder().getSize();
					WorldBorder border = WorldManager.getGameMap().getWorldBorder();
					border.setSize(radius * 2, 5);
					border.setWarningTime(15);
					border.setWarningDistance(2);
					border.setDamageBuffer(2);
					border.setDamageAmount(0.1);
					isMeeting = true;
				}
				meetingDelay--;
				for(UHCPlayer uhcPlayer : PlayerManager.getAlivePlayers()) {
					if(!uhcPlayer.isOnline()) {
						Location location = uhcPlayer.getLocation();
						if(location != null) {
							WorldBorder border = WorldManager.getGameMap().getWorldBorder();
							if(!border.isInside(location)) uhcPlayer.killOnLeave();
						}
					}
				}
			}
		} else {
			if(cooldown == 60) {
				for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
					if(!isSafe(currentPlayer.getLocation())) {
						currentPlayer.sendTitle(" ", DARK_RED + "" + BOLD + "> " +
										RED + "Иди к центру карты, осталась минута!" + DARK_RED + BOLD + " <",
								5, 40, 10);
					}
				}
			}
			updateBar();
			cooldown--;
		}
		Region region = new Region(meetingLoc.clone().subtract(radius, 0, radius), meetingLoc.clone().add(radius, 0, radius));
		ParticleUtils.createParticlesOnRegionEdges(region, Particle.CLOUD,0.5, null);
    }

	private void updateBar() {
		bar.setTitle(GOLD + "До сбора: " + DARK_AQUA + MathUtils.formatTime(cooldown) + " " + getPos());
		bar.setProgress(cooldown / (double) maxCooldown);
	}

}

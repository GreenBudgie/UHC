package ru.mutator;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.UHC.UHCPlayer;
import ru.UHC.WorldManager;
import ru.event.SpectatorJoinEvent;
import ru.event.SpectatorLeaveEvent;
import ru.event.UHCPlayerLeaveEvent;
import ru.event.UHCPlayerRejoinEvent;
import ru.util.MathUtils;
import ru.util.ParticleUtils;
import ru.util.Region;
import ru.util.TaskManager;

import java.util.HashSet;
import java.util.Set;

public class MutatorMeetingPlace extends Mutator implements Listener {

	private Location meetingLoc;
	public BossBar bar;
	private final int maxCooldown = 600;
	private int cooldown = maxCooldown;
	private int meetingDelay = 10;
	private boolean isMeeting = false;
	private double prevBorderSize = -1;
	private final int radius = 16;

	private final Set<UHCPlayer> safePlayers = new HashSet<>();

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
		return "Каждые десять минут все игроки должны собраться в центре карты (не обязательно на поверхности), иначе будут убиты. В аду безопасно!";
	}

	private String getPos() {
		return ChatColor.DARK_GRAY + "(" + ChatColor.AQUA + meetingLoc.getBlockX() + ChatColor.WHITE + ", " + ChatColor.AQUA + meetingLoc.getBlockZ() + ChatColor.DARK_GRAY + ")";
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
		meetingLoc = WorldManager.spawnLocation.clone().add(0, 2, 0);
		bar = Bukkit.createBossBar(ChatColor.GOLD + "До сбора: " + ChatColor.DARK_AQUA + MathUtils.formatTime(cooldown) + " " + getPos(), BarColor.YELLOW, BarStyle.SEGMENTED_10);
		reset();
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
		bar.setVisible(false);
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
		for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
			bar.addPlayer(p);
		}
		bar.setVisible(true);
		WorldBorder border = WorldManager.getGameMap().getWorldBorder();
		border.setWarningTime(0);
		border.setWarningDistance(0);
		border.setDamageBuffer(0);
		border.setDamageAmount(0);
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

	@Override
	public void update() {
		if(TaskManager.isSecUpdated()) {
			if(UHC.state.isInGame()) {
				for(Player currentPlayer : PlayerManager.getAliveOnlinePlayers()) {
					UHCPlayer uhcPlayer = PlayerManager.asUHCPlayer(currentPlayer);
					if(uhcPlayer != null) {
						Location location = currentPlayer.getLocation();
						if(safePlayers.contains(uhcPlayer) && !isSafe(location)) {
							currentPlayer.sendTitle(" ", ChatColor.DARK_RED + "" + ChatColor.BOLD + "> " +
									ChatColor.RED + "Теперь ты в опасности!" + ChatColor.DARK_RED + ChatColor.BOLD + " <",
									5, 40, 10);
							currentPlayer.playSound(currentPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5F, 0.8F);
							safePlayers.remove(uhcPlayer);
						} else if(!safePlayers.contains(uhcPlayer) && isSafe(location)) {
							currentPlayer.sendTitle(" ", ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "> " +
											ChatColor.GREEN + "Теперь ты в безопасности!" + ChatColor.DARK_GREEN + ChatColor.BOLD + " <",
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
							p.sendTitle(ChatColor.GREEN + "Сбор окончен!", "", 10, 30, 10);
						}
						WorldManager.getGameMap().getWorldBorder().setSize(prevBorderSize, 10);
						reset();
					} else {
						if(!isMeeting) {
							for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
								p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1F, 1F);
								p.sendTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Все на сбор!", "", 2, 30, 10);
							}
							bar.setProgress(0);
							bar.setColor(BarColor.RED);
							bar.setTitle(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Сбор!");
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
								currentPlayer.sendTitle(" ", ChatColor.DARK_RED + "" + ChatColor.BOLD + "> " +
												ChatColor.RED + "Иди к центру карты, осталась минута!" + ChatColor.DARK_RED + ChatColor.BOLD + " <",
										5, 40, 10);
							}
						}
					}
					bar.setTitle(ChatColor.GOLD + "До сбора: " + ChatColor.DARK_AQUA + MathUtils.formatTime(cooldown) + " " + getPos());
					bar.setProgress(cooldown / (double) maxCooldown);
					cooldown--;
				}
				Region region = new Region(meetingLoc.clone().subtract(radius, 0, radius), meetingLoc.clone().add(radius, 0, radius));
				ParticleUtils.createParticlesOnRegionEdges(region, Particle.CLOUD,0.5, null);
			} else {
				bar.setVisible(false);
			}
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

}

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

public class MutatorMeetingPlace extends Mutator implements Listener {

	private Location meetingLoc;
	public BossBar bar;
	private final int maxCooldown = 600;
	private int cooldown = maxCooldown;
	private int meetingDelay = 10;
	private boolean isMeeting = false;
	private double prevBorderSize = -1;
	private final int radius = 16;

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

	@Override
	public void onChoose() {
		meetingLoc = WorldManager.spawnLocation.clone().add(0, 2, 0);
		bar = Bukkit.createBossBar(ChatColor.GOLD + "До сбора: " + ChatColor.DARK_AQUA + MathUtils.formatTime(cooldown) + " " + getPos(), BarColor.YELLOW, BarStyle.SEGMENTED_10);
		reset();
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
							border.setDamageBuffer(1);
							border.setDamageAmount(0.3);
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
	public boolean canBeHidden() {
		return false;
	}

}

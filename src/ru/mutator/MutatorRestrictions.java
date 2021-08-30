package ru.mutator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import ru.UHC.PlayerManager;
import ru.UHC.UHC;
import ru.event.SpectatorJoinEvent;
import ru.event.SpectatorLeaveEvent;
import ru.event.UHCPlayerLeaveEvent;
import ru.event.UHCPlayerRejoinEvent;
import ru.util.MathUtils;
import ru.util.TaskManager;

public class MutatorRestrictions extends Mutator implements Listener {

	private int timeToRestrict, timeToAllow;
	private BossBar bar;
	private Restriction restriction = null;

	@Override
	public Material getItemToShow() {
		return Material.REDSTONE_TORCH;
	}

	@Override
	public ThreatStatus getThreatStatus() {
		return ThreatStatus.DANGEROUS;
	}

	@Override
	public String getName() {
		return "Я Вам Запрещаю";
	}

	@Override
	public String getDescription() {
		return "Иногда запрещается делать некоторые действия: атаковать, копать, ставить блоки, шифтить и спринтить. Нарушитель получает урон.";
	}

	@Override
	public void onChoose() {
		bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_20);
		bar.setVisible(false);
		reset();
	}

	public void reset() {
		timeToRestrict = MathUtils.randomRange(80, 150);
		timeToAllow = 0;
		restriction = null;
	}

	@Override
	public boolean canBeHidden() {
		return false;
	}

	@Override
	public void onDeactivate() {
		bar.removeAll();
		bar.setVisible(false);
	}

	private void punishInterrupter(Player p) {
		p.damage(1);
		p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5F, 1F);
	}

	@Override
	public void update() {
		if(UHC.state.isInGame()) {
			if(TaskManager.isSecUpdated()) {
				if(restriction == null) {
					timeToRestrict--;
					if(timeToRestrict == 3) {
						for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
							p.sendTitle(ChatColor.RED + "Я", "", 0, 30, 10);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1F, 1F);
						}
					}
					if(timeToRestrict == 2) {
						for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
							p.sendTitle(ChatColor.RED + "Я ВАМ", "", 0, 30, 10);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.2F, 1.1F);
						}
					}
					if(timeToRestrict == 1) {
						for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
							p.sendTitle(ChatColor.RED + "Я ВАМ ЗАПРЕЩАЮ", "", 0, 30, 10);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1.2F);
						}
					}
					if(timeToRestrict <= 0) {
						Restriction restriction = MathUtils.choose(Restriction.values());
						this.restriction = restriction;
						bar.removeAll();
						bar.setTitle(ChatColor.DARK_RED + "Я ВАМ ЗАПРЕЩАЮ: " + ChatColor.RED + restriction.getDescription());
						for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
							bar.addPlayer(p);
							p.sendTitle(ChatColor.DARK_RED + "Я ВАМ ЗАПРЕЩАЮ", ChatColor.RED + restriction.getDescription(), 0, 50, 15);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 0.8F);
						}
						bar.setVisible(true);
						bar.setProgress(1);
						timeToAllow = 20;
					}
				} else {
					timeToAllow--;
					bar.setProgress(timeToAllow / 20.0);
					if(timeToAllow <= 0) {
						for(Player p : PlayerManager.getInGamePlayersAndSpectators()) {
							p.sendTitle(ChatColor.GREEN + "Я ВАМ РАЗРЕШАЮ", ChatColor.DARK_GREEN + restriction.getDescription(), 0, 50, 15);
							p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 0.8F);
						}
						restriction = null;
						bar.setVisible(false);
						reset();
					}
				}
			}
		} else {
			if(restriction != null) {
				restriction = null;
				bar.setVisible(false);
			}
		}
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

	@EventHandler
	public void place(BlockPlaceEvent e) {
		if(restriction == Restriction.PLACE && PlayerManager.isPlaying(e.getPlayer())) {
			punishInterrupter(e.getPlayer());
		}
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		if(restriction == Restriction.DIG && PlayerManager.isPlaying(e.getPlayer())) {
			punishInterrupter(e.getPlayer());
		}
	}

	@EventHandler
	public void shift(PlayerToggleSneakEvent e) {
		if(restriction == Restriction.SHIFT && PlayerManager.isPlaying(e.getPlayer()) && e.isSneaking()) {
			punishInterrupter(e.getPlayer());
		}
	}

	@EventHandler
	public void sprint(PlayerToggleSprintEvent e) {
		if(restriction == Restriction.SPRINT && PlayerManager.isPlaying(e.getPlayer()) && e.isSprinting()) {
			punishInterrupter(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void attack(EntityDamageByEntityEvent e) {
		if(restriction == Restriction.ATTACK && e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player && !e.isCancelled()
				&& e.getFinalDamage() > 0 && !e.getEntity().isInvulnerable()) {
			Player damager = (Player) e.getDamager();
			if(PlayerManager.isPlaying(damager)) {
				punishInterrupter(damager);
			}
		}
	}

	private enum Restriction {

		ATTACK("Атаковать"),
		DIG("Копать"),
		PLACE("Ставить Блоки"),
		SHIFT("Шифтить"),
		SPRINT("Спринтить");

		private String desc;

		Restriction(String desc) {
			this.desc = desc;
		}

		protected String getDescription() {
			return desc;
		}

	}

}

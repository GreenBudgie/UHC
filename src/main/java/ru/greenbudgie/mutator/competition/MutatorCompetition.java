package ru.greenbudgie.mutator.competition;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.greenbudgie.UHC.PlayerManager;
import ru.greenbudgie.UHC.UHC;
import ru.greenbudgie.UHC.UHCPlayer;
import ru.greenbudgie.event.*;
import ru.greenbudgie.mutator.base.Mutator;
import ru.greenbudgie.mutator.base.ThreatStatus;
import ru.greenbudgie.util.MathUtils;
import ru.greenbudgie.util.ParticleUtils;
import ru.greenbudgie.util.TaskManager;

import javax.annotation.Nullable;
import java.util.*;

import static org.bukkit.ChatColor.*;

public class MutatorCompetition extends Mutator implements Listener {

    private static final String PREFIX = DARK_GRAY + "[" + BLUE + BOLD + "Соревнование" + DARK_GRAY + "] ";
    private static final int TIME = 20 * 60;

    private int timer;

    private final Map<Player, BossBar> bossBars = new HashMap<>();
    private final Map<UHCPlayer, Integer> playerScore = new HashMap<>();

    private final List<CompetitionTask> tasks = List.of(
        //new CompetitionTaskEatGoldenApple(),
        //new CompetitionTaskKillSkeletons(),
        new CompetitionTaskLoseHealth()
    );

    @Nullable
    private CompetitionTask task = null;

    @Override
    public Material getItemToShow() {
        return Material.GLOW_ITEM_FRAME;
    }

    @Override
    public ThreatStatus getThreatStatus() {
        return ThreatStatus.DANGEROUS;
    }

    @Override
    public String getName() {
        return "Соревнование";
    }

    @Override
    public String getDescription() {
        return "Всем игрокам выдается задание. Игрок с лучшим показателем через 20 минут получает 3 золотых яблока, " +
                "а с худшим - умирает! Если победителей или проигравших несколько - выбирается случайный.";
    }

    @Override
    public void onChoose() {
        resetTimer();
        if (UHC.state.isGameActive()) {
            selectTaskAndAnnounce();
        }
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
    public void update() {
        if (TaskManager.isSecUpdated()) {
            timer--;
            if (timer <= 0) {
                timeEnd();
                return;
            }

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

    public void removePlayerScore(UHCPlayer player) {
        var previouslyBestPlayers = new HashSet<>(getBestScore().players);
        var previouslyWorstPlayers = new HashSet<>(getWorstScore().players);

        playerScore.remove(player);

        afterScoreUpdate(previouslyBestPlayers, previouslyWorstPlayers);
    }

    public void addPlayerScore(Player player, int score) {
        var uhcPlayer = PlayerManager.asUHCPlayer(player);
        if (uhcPlayer == null || !uhcPlayer.isAlive()) {
            return;
        }

        addPlayerScore(uhcPlayer, score);
    }

    public void addPlayerScore(UHCPlayer player, int score) {
        var newScore = getScore(player) + score;
        setPlayerScore(player, newScore);
    }

    public void setPlayerScore(UHCPlayer player, int score) {
        var previouslyBestPlayers = new HashSet<>(getBestScore().players);
        var previouslyWorstPlayers = new HashSet<>(getWorstScore().players);

        playerScore.put(player, score);

        if (player.isAlive()) {
            var alivePlayer = player.getPlayer();
            ParticleUtils.createParticlesAround(alivePlayer, Particle.END_ROD, null, 3);
            alivePlayer.playSound(alivePlayer, Sound.BLOCK_NOTE_BLOCK_FLUTE, 0.5f, (float) MathUtils.randomRangeDouble(1.1f, 1.4f));
        }

        afterScoreUpdate(previouslyBestPlayers, previouslyWorstPlayers);
    }

    private void afterScoreUpdate(Set<UHCPlayer> previouslyBestPlayers, Set<UHCPlayer> previouslyWorstPlayers) {
        var newBestPlayers = getBestScore().players;
        var newWorstPlayers = getWorstScore().players;

        var wasEqualScore = previouslyBestPlayers.size() == previouslyWorstPlayers.size()
                && previouslyBestPlayers.containsAll(previouslyWorstPlayers);

        for (var player : PlayerManager.getAlivePlayers()) {
            var wasPreviouslyBest = !wasEqualScore && previouslyBestPlayers.contains(player);
            var wasPreviouslyAbsoluteLeader = !wasEqualScore && previouslyBestPlayers.contains(player) && previouslyBestPlayers.size() == 1;
            var wasPreviouslyWorst = !wasEqualScore && previouslyWorstPlayers.contains(player);
            var isAmongBest = newBestPlayers.contains(player);
            var isAmongWorst = newWorstPlayers.contains(player);

            var isNowBest = !wasPreviouslyBest && isAmongBest;
            var isNoLongerBest = wasPreviouslyBest && !isAmongBest;
            var isNowWorst = !wasPreviouslyWorst && isAmongWorst;
            var isNoLongerWorst = wasPreviouslyWorst && !isAmongWorst;

            var isNewAbsoluteLeader = !wasPreviouslyAbsoluteLeader && isAmongBest && newBestPlayers.size() == 1;
            if (isNewAbsoluteLeader) {
                for (Player inGamePlayer : PlayerManager.getInGamePlayersAndSpectators()) {
                    inGamePlayer.sendMessage(PREFIX + GOLD + player.getNickname() + GREEN + " теперь лидирует!");
                }
            }

            var onlinePlayer = player.getPlayer();
            if (onlinePlayer == null) {
                continue;
            }

            if (isNowBest && !isNewAbsoluteLeader) {
                onlinePlayer.sendMessage(PREFIX + GREEN + "Ты теперь среди лучших!");
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.2f);
            }

            if (isNoLongerBest) {
                onlinePlayer.sendMessage(PREFIX + YELLOW + "Ты теперь не лучший!");
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.8f);
            }

            if (isNowWorst) {
                onlinePlayer.sendMessage(PREFIX + DARK_RED + BOLD + "Ты теперь среди худших!");
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.6f);
            }

            if (isNoLongerWorst) {
                onlinePlayer.sendMessage(PREFIX + YELLOW + "Ты теперь не худший!");
                onlinePlayer.playSound(onlinePlayer, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 0.8f);
            }
        }
    }

	private ScoreData getBestScore() {
        if (playerScore.isEmpty()) {
            return new ScoreData(0, new HashSet<>());
        }

        var bestScore = 0;
        var bestPlayers = new HashSet<UHCPlayer>();
        for (UHCPlayer player : playerScore.keySet()) {
            var score = getScore(player);
            if (score > bestScore) {
                bestPlayers.clear();
                bestPlayers.add(player);
                bestScore = score;
                continue;
            }

            if (score == bestScore) {
                bestPlayers.add(player);
            }
        }

		return new ScoreData(bestScore, bestPlayers);
	}

	private ScoreData getWorstScore() {
        if (playerScore.isEmpty()) {
            return new ScoreData(0, new HashSet<>());
        }

        var worstScore = Integer.MAX_VALUE;
        var worstPlayers = new HashSet<UHCPlayer>();
        for (UHCPlayer player : playerScore.keySet()) {
            var score = getScore(player);
            if (score < worstScore) {
                worstPlayers.clear();
                worstPlayers.add(player);
                worstScore = score;
                continue;
            }

            if (score == worstScore) {
                worstPlayers.add(player);
            }
        }

        return new ScoreData(worstScore, worstPlayers);
	}

    private int getScore(UHCPlayer player) {
        return playerScore.getOrDefault(player, 0);
    }

    private void resetTimer() {
        timer = TIME;
    }

    private void timeEnd() {
        var bestScore = getBestScore();
        var worstScore = getWorstScore();
        if (bestScore.score() == worstScore.score()) {
            for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                player.sendMessage(PREFIX + YELLOW + "Все набрали одинаковое количество очков, нет ни победителей, ни проигравших!");
            }

            deactivate();
            return;
        }

        var bestPlayers = bestScore.players();
        var worstPlayers = worstScore.players();

        var bestPlayer = bestPlayers.isEmpty() ? null : MathUtils.choose(bestPlayers);
        var worstPlayer = worstPlayers.isEmpty() ? null : MathUtils.choose(worstPlayers);

        if (bestPlayer != null) {
            var present = new ItemStack(Material.GOLDEN_APPLE, 3);
            var item = bestPlayer.getLocation().getWorld().dropItem(bestPlayer.getLocation(), present);
            item.setPickupDelay(0);
            for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                player.sendMessage(PREFIX + GOLD + bestPlayer.getNickname() + GREEN + BOLD + " победил!");
            }
        }

        if (worstPlayer != null) {
            worstPlayer.kill();
            for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
                player.sendMessage(PREFIX + GOLD + worstPlayer.getNickname() + DARK_RED + BOLD + " проиграл!");
            }
        }

        deactivate();
    }

    private void reset() {
        bossBars.values().forEach(bar -> {
            bar.setVisible(false);
            bar.removeAll();
        });
        bossBars.clear();

        playerScore.clear();
        if (task != null) {
            task.end();
            task = null;
        }
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
        bar.setProgress(MathUtils.clamp(timer / (double) TIME, 0, 1));
        setBarNameForPlayer(player, bar);
    }

    private void updateBarForSpectator(BossBar bar) {
        bar.setProgress(MathUtils.clamp(timer / (double) TIME, 0, 1));
        setBarNameForSpectator(bar);
    }

    private void setBarNameForPlayer(Player player, BossBar bar) {
        var splitter = DARK_GRAY + " / ";
        String timeInfo = DARK_GRAY + " (" + DARK_AQUA + BOLD +
                MathUtils.formatTime(timer) + DARK_GRAY + ")";

        UHCPlayer uhcPlayer = Objects.requireNonNull(PlayerManager.asUHCPlayer(player));
        var score = getScore(uhcPlayer);
        var bestScore = getBestScore();
        var worstScore = getWorstScore();
        var isEqualScore = bestScore.score() == worstScore.score();
        String scoreInfo;
        if (!isEqualScore && score == worstScore.score()) {
            scoreInfo = DARK_RED + "" + BOLD + score + DARK_GRAY + " (" + DARK_RED + "худший" + DARK_GRAY + ")";
        } else if (!isEqualScore && score == bestScore.score()) {
            scoreInfo = GREEN + "" + BOLD + score + DARK_GRAY + " (" + GREEN + "лучший" + DARK_GRAY + ")";
        } else {
            scoreInfo = YELLOW + "" + BOLD + score;
        }

		var fullScoreInfo = YELLOW + "Прогресс" + DARK_GRAY + ": " + scoreInfo;
		var fullBestScoreInfo = DARK_GREEN + "Лучший" + DARK_GRAY + ": " + DARK_GREEN + BOLD + bestScore.score();
		var fullWorstScoreInfo = RED + "Худший" + DARK_GRAY + ": " + RED + BOLD + worstScore.score();

        bar.setTitle(fullScoreInfo + splitter + fullBestScoreInfo + splitter + fullWorstScoreInfo + timeInfo);
    }

    private void setBarNameForSpectator(BossBar bar) {
        // TODO fix code duplication
        var splitter = DARK_GRAY + " / ";
        String timeInfo = DARK_GRAY + " (" + DARK_AQUA + BOLD +
                MathUtils.formatTime(timer) + DARK_GRAY + ")";

        var bestScore = getBestScore();
        var worstScore = getWorstScore();

        var fullBestScoreInfo = DARK_GREEN + "Лучший" + DARK_GRAY + ": " + DARK_GREEN + BOLD + bestScore.score();
        var fullWorstScoreInfo = RED + "Худший" + DARK_GRAY + ": " + RED + BOLD + worstScore.score();

        bar.setTitle(fullBestScoreInfo + splitter + fullWorstScoreInfo + timeInfo);
    }

    private BossBar createDefaultBossBar(Player player) {
        BossBar bar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
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
    public void playerDeath(UHCPlayerDeathEvent event) {
        if (event.getUHCPlayer().isOnline()) {
            removeBossBar(event.getUHCPlayer().getPlayer());
        }

        removePlayerScore(event.getUHCPlayer());
    }

    @EventHandler
    public void spectatorJoin(SpectatorJoinEvent event) {
        if (event.isJoinedFromLobby()) {
            announceTaskToPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void playerRejoin(UHCPlayerRejoinEvent event) {
        var player = event.getUHCPlayer().getPlayer();
        if (player == null) {
            return;
        }

        announceTaskToPlayer(player);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        selectTaskAndAnnounce();
    }

    private void selectTaskAndAnnounce() {
        for (UHCPlayer player : PlayerManager.getAlivePlayers()) {
            playerScore.put(player, 0);
        }

        task = MathUtils.choose(tasks);
        task.start();
        for (Player player : PlayerManager.getInGamePlayersAndSpectators()) {
            announceTaskToPlayer(player);
        }
    }

    private void announceTaskToPlayer(Player player) {
        if (task == null) {
            return;
        }

        player.sendMessage(PREFIX + DARK_GRAY + "--- " + DARK_AQUA + BOLD + task.getName() + DARK_GRAY + " ---");
        player.sendMessage(PREFIX + AQUA + task.getDescription());
    }

    private void removeBossBar(Player player) {
        if (bossBars.containsKey(player)) {
            BossBar bar = bossBars.get(player);
            bar.removeAll();
            bar.setVisible(false);
            bossBars.remove(player);
        }
    }

    private record ScoreData(int score, Set<UHCPlayer> players) {
    }

}

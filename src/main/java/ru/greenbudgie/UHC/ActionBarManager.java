package ru.greenbudgie.UHC;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import ru.greenbudgie.util.TaskManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ActionBarManager {

    private static final int UPDATE_FREQUENCY_TICKS = 2;

    private static final Map<Player, ActionBarMessage> messageByPlayer = new HashMap<>();

    /**
     * Whether is makes sense to queue a message on this tick.
     */
    public static boolean isUpdateTick() {
        return TaskManager.ticksPassed(UPDATE_FREQUENCY_TICKS);
    }

    /**
     * Immediately shows an action bar message to the player, overriding any message that have been there before.
     * Fine to use in lobby. For sending messages in-game, use queueMessage().
     * If text is null, shows nothing (no-op).
     */
    public static void showMessage(Player player, @Nullable String text) {
        if (text == null) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public static void showMessages() {
        if (!isUpdateTick()) {
            return;
        }

        messageByPlayer.forEach(ActionBarManager::showMessage);
        messageByPlayer.clear();
    }

    private static void showMessage(Player player, ActionBarMessage message) {
        showMessage(player, message.messageGenerator.get());
    }

    /**
     * Queues the message to be shown to the player in action bar. Priority should be a positive number.
     * Messages with higher priorities in the same tick take over other messages with lower priority.
     * For lower priorities messageGenerator won't even run.
     * Only queues messages on updateTicks, so check before calling.
     *
     * @param player           Player to show the message
     * @param priority         Priority of the message >= 0
     * @param messageGenerator Function to generate the message. If returns null, the message will not be shown
     */
    public static void queueMessage(Player player, int priority, Supplier<String> messageGenerator) {
        if (!isUpdateTick()) {
            return;
        }

        var existingMessage = messageByPlayer.get(player);
        if (existingMessage != null && existingMessage.priority > priority) {
            return;
        }

        messageByPlayer.put(player, new ActionBarMessage(priority, messageGenerator));
    }

    private record ActionBarMessage(int priority, Supplier<String> messageGenerator) {
    }

}

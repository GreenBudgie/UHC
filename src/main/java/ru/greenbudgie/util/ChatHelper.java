package ru.greenbudgie.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

/**
 * Helper for sending chat messages that mix legacy-formatted strings with components
 * (e.g. a {@link net.md_5.bungee.api.chat.TranslatableComponent} for a localized item name).
 */
public class ChatHelper {

    /**
     * Sends a message assembled from the given parts. Each part may be a
     * {@link String} (interpreted as legacy-formatted text) or a {@link BaseComponent}
     * (or an array of them).
     */
    public static void send(CommandSender recipient, Object... parts) {
        recipient.spigot().sendMessage(build(parts));
    }

    public static BaseComponent[] build(Object... parts) {
        ComponentBuilder builder = new ComponentBuilder();
        for (Object part : parts) {
            switch (part) {
                case BaseComponent component -> builder.append(component);
                case BaseComponent[] components -> builder.append(components);
                case null -> {}
                default -> builder.appendLegacy(part.toString());
            }
        }
        return builder.create();
    }

}

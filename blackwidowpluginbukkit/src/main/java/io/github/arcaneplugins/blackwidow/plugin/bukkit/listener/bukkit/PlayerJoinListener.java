package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Listens for PlayerJoinEvent events.
 *
 * @author stumper66
 * @since 1.1.0
 */
public final class PlayerJoinListener implements Listener {

    private final BlackWidow plugin;

    public PlayerJoinListener(
            @NotNull final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void handle(
            @NotNull final PlayerJoinEvent event
    ) {
        handleUpdateChecking(event);
    }

    private void handleUpdateChecking(
            @NotNull final PlayerJoinEvent event
    ) {
        if (!plugin().bukkitVersionChecker().getNotifyPlayers()) {
            return;
        }

        final String message = plugin().bukkitVersionChecker().getNotifyMessage();

        if (message == null) {
            return;
        }

        event.getPlayer().sendMessage(message);
    }

    @NotNull
    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }
}

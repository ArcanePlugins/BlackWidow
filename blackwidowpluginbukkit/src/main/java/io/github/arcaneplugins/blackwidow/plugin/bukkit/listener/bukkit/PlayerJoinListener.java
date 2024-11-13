package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final BlackWidow plugin;

    public PlayerJoinListener(final BlackWidow plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void handle(final PlayerJoinEvent event){
        if (!plugin.bukkitVersionChecker.getNotifyPlayers()) {
            return;
        }

        final String message = plugin.bukkitVersionChecker.getNotifyMessage();
        if (message == null){
            return;
        }

        event.getPlayer().sendMessage(message);
    }
}

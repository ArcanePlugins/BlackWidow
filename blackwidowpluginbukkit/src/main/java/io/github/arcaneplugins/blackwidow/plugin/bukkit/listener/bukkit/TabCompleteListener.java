package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TabCompleteListener implements Listener {

    private final BlackWidow plugin;

    public TabCompleteListener(
            final BlackWidow plugin
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onTabComplete(final TabCompleteEvent event) {
        //noinspection ConstantValue
        if (true) {
            //todo tmp method bypass
            return;
        }

        //noinspection unused //todo tmp
        final CommandSender sender = event.getSender();

        //noinspection unused //todo tmp
        final String buffer = event.getBuffer();

        final List<String> completions = new ArrayList<>(event.getCompletions());

        //todo debug: sender.sendMessage("buffer: " + buffer + "; completions: " + String.join(", ", completions));

        event.setCompletions(completions);
    }

    @SuppressWarnings("unused") // todo tmp
    private BlackWidow plugin() {
        return plugin;
    }


}
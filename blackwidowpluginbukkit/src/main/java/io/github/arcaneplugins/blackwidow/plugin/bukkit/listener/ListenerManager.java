/*
 * BlackWidow: Security modifications for Minecraft servers and proxies
 * Copyright (c) 2024  lokka30.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit.PlayerCommandPreprocessListener;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit.PlayerCommandSendListener;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit.PlayerJoinListener;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.paper.AsyncPlayerCommandSendListener;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public final class ListenerManager {

    private final BlackWidow plugin;
    private final Collection<Listener> listeners = new LinkedHashSet<>();

    public ListenerManager(
        final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    private void constructListeners() {
        listeners().addAll(List.of(
                new PlayerCommandPreprocessListener(plugin()),
                new PlayerJoinListener(plugin())
        ));

        listeners().add(
            plugin().usingPaper() && plugin().usePaperFeatures() ?
                new AsyncPlayerCommandSendListener(plugin()) :
                new PlayerCommandSendListener(plugin())
        );
    }

    public void load() {
        plugin().getLogger().info("Loading listeners.");
        listeners().clear();
        constructListeners();

        for (final Listener listener : listeners()) {
            plugin().getServer().getPluginManager().registerEvents(listener, plugin());
        }
    }

    public Collection<Listener> listeners() {
        return listeners;
    }

    private BlackWidow plugin() {
        return plugin;
    }

}

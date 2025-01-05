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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.bukkit;

import io.github.arcaneplugins.blackwidow.lib.cmdblocking.EvalCause;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Evaluation;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Context;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public final class PlayerCommandPreprocessListener implements Listener {

    private final BlackWidow plugin;

    public PlayerCommandPreprocessListener(
            final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            // Event is already cancelled - nothing to do!
            return;
        }

        final Player player = event.getPlayer();

        final Context context = new Context(plugin())
                .withPlayer(player)
                .withCommands(List.of(event.getMessage()));

        if (plugin().cmdBlocker().filterCmdExecution()) {
            final Evaluation eval = plugin()
                    .cmdBlocker()
                    .evalAndProcess(context, event.getMessage(), true, EvalCause.CMD_EXECUTION);

            switch (eval.policy()) {
                case ALLOW:
                    // Do nothing
                    break;
                case DENY:
                    // Prevent command execution
                    event.setCancelled(true);
                    break;
                case null, default:
                    event.setCancelled(true);
                    throw new IllegalStateException("Unexpected policy '" + eval.policy() + "', prevented " +
                            "command execution for best-practice security purposes.");
            }

            // TODO Debug log the evaluation's description.
        }
    }

    private BlackWidow plugin() {
        return plugin;
    }

}

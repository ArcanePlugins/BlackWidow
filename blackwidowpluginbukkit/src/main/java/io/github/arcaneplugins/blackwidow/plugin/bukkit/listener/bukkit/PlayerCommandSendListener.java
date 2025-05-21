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
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Policy;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Context;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public final class PlayerCommandSendListener implements Listener {

    private final BlackWidow plugin;

    public PlayerCommandSendListener(
        final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void handle(final PlayerCommandSendEvent event) {
        if (event.getCommands().isEmpty()) {
            // no commands to process - nothing to do here
            return;
        }

        final Player player = event.getPlayer();

        final Context context = new Context(plugin())
            .withPlayer(player)
            .withCommands(event.getCommands().stream().map(cmd -> "/" + cmd).toList());

        if (plugin().cmdBlocker().filterCmdSuggestion()) {
            event.getCommands().removeIf(cmd -> {
                final Evaluation eval = plugin()
                    .cmdBlocker()
                    .evalAndProcess(context, "/" + cmd, false, EvalCause.CMD_SUGGESTION);

                if (eval.policy() == null || (eval.policy() != Policy.ALLOW && eval.policy() != Policy.DENY)) {
                    event.getCommands().clear();
                    throw new IllegalStateException("Unexpected policy '" + eval.policy() + "', prevented " +
                        "all command suggestions for best-practice security purposes.");
                }

                // TODO Debug log the evaluation's description.

                return eval.policy() == Policy.DENY;
            });
        }
    }

    private BlackWidow plugin() {
        return plugin;
    }

}

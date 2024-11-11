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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.paper;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.DebugCategory;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings("UnstableApiUsage") // <-- :(
public final class AsyncPlayerCommandSendListener implements Listener {

    private final BlackWidow plugin;

    public AsyncPlayerCommandSendListener(
        final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handle(
        final AsyncPlayerSendCommandsEvent<? extends CommandSourceStack> event
    ) {
        // Event Javadocs:
        // https://jd.papermc.io/paper/1.21/com/destroystokyo/paper/event/brigadier/AsyncPlayerSendCommandsEvent.html

        if(!event.isAsynchronous() && !event.hasFiredAsync()) {
            // as per paper API docs, this event can fire twice, once async and once sync.
            // let's make sure we only handle the async scenario.
            // OR, if the event will not be fired async, then handle it anyways.
            return;
        }

        final Player player = event.getPlayer();
        final RootCommandNode<? extends CommandSourceStack> commandNode = event.getCommandNode();

        //TODO Implement for Paper

        plugin().debugLog(
            DebugCategory.ASYNC_PLAYER_COMMAND_SEND_LISTENER,
            () -> "AsyncPlayerCommandSendListener{player=" + player.getName() + ", commandNode.toString=" +
                commandNode + "};"
        );
    }

    private BlackWidow plugin() {
        return plugin;
    }

}

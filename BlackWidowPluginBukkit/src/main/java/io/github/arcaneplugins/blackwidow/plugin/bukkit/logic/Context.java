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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;

//TODO Javadoc
public final class Context {

    private final BlackWidow plugin;

    private Player player;
    private Collection<String> commands;
    private CommandSender sender;

    //TODO Javadoc
    public Context(
        final BlackWidow plugin
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    //TODO javadoc
    public BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

    // TODO Javadoc
    public Context withPlayer(final Player player) {
        this.player = player;
        return this;
    }

    // TODO Javadoc
    public Player player(final boolean require) {
        return require ? ContextException.require(player) : player;
    }

    // TODO Javadoc
    public Context withCommands(final Collection<String> commands) {
        this.commands = commands;
        return this;
    }

    // TODO Javadoc
    @SuppressWarnings("unused")
    public Collection<String> commands(final boolean require) {
        return require ? ContextException.require(commands) : commands;
    }

    // TODO Javadoc
    @SuppressWarnings("unused")
    public Context withCommandSender(final CommandSender sender) {
        this.sender = sender;
        return this;
    }

    // TODO Javadoc
    public CommandSender commandSender(final boolean require) {
        return require ? ContextException.require(sender) : sender;
    }



    // TODO Javadoc
    private static final class ContextException extends RuntimeException {

        private final String message;

        // TODO Javadoc
        public ContextException(
            final String message
        ) {
            this.message = message;
        }

        /**
         * {@inheritDoc}
         *
         * @author lokka30
         * @since 1.0.0
         */
        @Override
        public String getMessage() {
            return message;
        }

        // TODO Javadoc
        public static <T> T require(final T obj) {
            if (obj != null) {
                return obj;
            }

            throw new ContextException("Attempted to access unavailable context");
        }
    }

}

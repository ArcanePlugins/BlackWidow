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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.inbuilt.action;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.translations.Translation;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Action;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Context;
import net.kyori.adventure.audience.Audience;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class SendMessageAction extends Action {

    public static final String ID = "send-message";

    private final Collection<String> message;

    public SendMessageAction(
        final BlackWidow plugin,
        final Collection<String> message
    ) {
        super(plugin, ID);
        this.message = Objects.requireNonNull(message, "message");
    }

    public SendMessageAction(
        final BlackWidow plugin,
        final CommentedConfigurationNode node
    ) {
        super(
            plugin,
            Objects.requireNonNull(node.node("id").getString(), "id")
        );

        try {
            this.message = Objects.requireNonNull(
                node.node("msg").getList(String.class),
                "msg"
            );
        } catch (final SerializationException ex) {
            throw new RuntimeException("Unable to read msg string list", ex);
        }
    }

    public Collection<String> message() {
        return Objects.requireNonNull(message, "message");
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public void run(Context context) {
        final Audience advPlayer = plugin()
            .adventure()
            .player(context.player(true));

        message()
            .stream()
            .map(msg -> Translation.formatify(plugin(), msg, Collections.emptyMap()))
            .forEachOrdered(advPlayer::sendMessage);
    }
}

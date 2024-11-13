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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.inbuilt.requirement;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Context;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Requirement;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Objects;

public final class HasPermissionRequirement extends Requirement {

    public static final String ID = "has-perission";

    private final String permission;

    public HasPermissionRequirement(
            final BlackWidow plugin,
            final String permission,
            final boolean inverted
    ) {
        super(plugin, ID, inverted);
        this.permission = Objects.requireNonNull(permission, "permission");
    }

    public HasPermissionRequirement(
            final BlackWidow plugin,
            final CommentedConfigurationNode node
    ) {
        this(
                plugin,
                Objects.requireNonNull(node.node("permission").getString(), "permission"),
                node.node("inverted").getBoolean(false)
        );
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public boolean validateImpl(final Context context) {
        return Objects.requireNonNullElse(
                context.player(false),
                context.commandSender(false)
        ).hasPermission(permission());
    }

    public String permission() {
        return Objects.requireNonNull(permission, "permission");
    }
}

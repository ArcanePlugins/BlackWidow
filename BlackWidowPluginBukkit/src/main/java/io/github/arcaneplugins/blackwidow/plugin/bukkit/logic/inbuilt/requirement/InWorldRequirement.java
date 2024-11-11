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

public final class InWorldRequirement extends Requirement {

    public static final String ID = "in-world";

    private final String worldName;

    public InWorldRequirement(
        final BlackWidow plugin,
        final String worldName,
        final boolean inverted
    ) {
        super(plugin, ID, inverted);
        this.worldName = Objects.requireNonNull(worldName, "worldName");
    }

    public InWorldRequirement(
        final BlackWidow plugin,
        final CommentedConfigurationNode node
    ) {
        this(
            plugin,
            Objects.requireNonNull(node.node("world-name").getString(), "worldName"),
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
    public boolean validateImpl(Context context) {
        return context.player(true).getWorld().getName().equalsIgnoreCase(worldName());
    }

    public String worldName() {
        return Objects.requireNonNull(worldName, "worldName");
    }
}

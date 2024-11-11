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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.event;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Requirement;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class LoadRequirementsReadyEvent extends Event {

    private final BlackWidow plugin;
    private final Map<String, Function<CommentedConfigurationNode, Requirement>> reqParsers;
    private static final HandlerList HANDLERS = new HandlerList();

    public LoadRequirementsReadyEvent(
        final BlackWidow plugin,
        final Map<String, Function<CommentedConfigurationNode, Requirement>> reqParsers
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.reqParsers = Objects.requireNonNull(reqParsers, "reqParsers");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * See {@link #getHandlers()}.
     *
     * @return Handler list.
     */
    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

    public Map<String, Function<CommentedConfigurationNode, Requirement>> requirementParsers() {
        return Objects.requireNonNull(reqParsers, "reqParsers");
    }

}

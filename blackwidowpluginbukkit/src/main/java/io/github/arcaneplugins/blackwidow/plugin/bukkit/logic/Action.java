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

import java.util.Objects;

public abstract class Action implements LogicUnit {

    private final BlackWidow plugin;
    private final String id;

    public Action(
        final BlackWidow plugin,
        final String id
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.id = Objects.requireNonNull(id, "id");
    }

    public abstract void run(final Context context);

    public final String id() {
        return Objects.requireNonNull(id, "id");
    }

    protected final BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }


}

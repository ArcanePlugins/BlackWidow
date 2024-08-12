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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.command.blackwidow;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.command.blackwidow.subcommand.ReloadSubcommand;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.command.blackwidow.subcommand.VersionSubcommand;

import java.util.Objects;

//TODO Javadocs
public final class BlackWidowCommand extends CommandAPICommand {

    private final BlackWidow plugin;

    //TODO Javadocs
    public BlackWidowCommand(
        final BlackWidow plugin
    ) {
        super("blackwidow");
        this.plugin = plugin;
        withAliases("bw");
        withPermission("blackwidow.command.bw");
        withShortDescription("Base command to view info of and manage the BlackWidow plugin.");
        withFullDescription("Base command to view and manage the BlackWidow plugin.");
        withSubcommands(
            new ReloadSubcommand(plugin()),
            new VersionSubcommand(plugin())
        );
    }

    //TODO Javadocs
    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

}

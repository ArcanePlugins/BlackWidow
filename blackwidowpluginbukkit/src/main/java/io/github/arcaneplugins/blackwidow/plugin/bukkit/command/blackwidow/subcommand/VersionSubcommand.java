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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.command.blackwidow.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.translations.Translation;

import java.util.Map;
import java.util.Objects;

public final class VersionSubcommand extends CommandAPICommand {

    private final BlackWidow plugin;

    public VersionSubcommand(
            final BlackWidow plugin
    ) {
        super("version");
        this.plugin = plugin;
        withAliases("ver", "about", "info");
        withPermission("blackwidow.command.bw.version");
        withShortDescription("View the version of BlackWidow currently installed.");
        withFullDescription("Views the version of BlackWidow currently installed.");
        executes((sender, args) -> {
            //noinspection deprecation
            Translation.COMMAND_BLACKWIDOW_VERSION.sendTo(
                    plugin(),
                    sender,
                    Map.of(
                            "version", () -> plugin().getDescription().getVersion(),
                            "authors", () -> Translation
                                    .joinSeparatedStrings(plugin(), plugin().getDescription().getAuthors())
                    )
            );
        });
    }

    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

}

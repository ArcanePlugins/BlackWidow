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

import java.util.Collections;
import java.util.Objects;

public final class ReloadSubcommand extends CommandAPICommand {

    private final BlackWidow plugin;

    public ReloadSubcommand(
        final BlackWidow plugin
    ) {
        super("reload");
        this.plugin = plugin;
        withAliases("rl");
        withPermission("blackwidow.command.bw.reload");
        withShortDescription("Soft-reload BlackWidow's configurable features.");
        withFullDescription("Soft-reloads BlackWidow to allow for changes to the configuration to take effect.");
        executes((sender, args) -> {
            Translation.COMMAND_BLACKWIDOW_RELOAD_STARTED.sendTo(plugin(), sender, Collections.emptyMap());
            try {
                plugin().softReload();
            } catch (final Exception ex) {
                Translation.COMMAND_BLACKWIDOW_RELOAD_FAILED.sendTo(plugin(), sender, Collections.emptyMap());
                return;
            }
            Translation.COMMAND_BLACKWIDOW_RELOAD_COMPLETE.sendTo(plugin(), sender, Collections.emptyMap());
        });
    }

    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

}

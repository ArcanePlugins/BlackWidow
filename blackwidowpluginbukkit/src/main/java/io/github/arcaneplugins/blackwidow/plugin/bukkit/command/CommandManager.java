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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.RegisteredCommand;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.command.blackwidow.BlackWidowCommand;

public final class CommandManager {

    private final BlackWidow plugin;

    public CommandManager(
        final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    public void init() {
        plugin().getLogger().info("Initialising commands.");
        CommandAPI.onLoad(
            new CommandAPIBukkitConfig(plugin())
                .verboseOutput(false)
                .silentLogs(true)
                .usePluginNamespace()
        );
        registerCommands();
    }

    private void registerCommands() {
        new BlackWidowCommand(plugin()).register();
    }

    public void load() {
        plugin().getLogger().info("Loading commands.");
        CommandAPI.onEnable();
    }

    public void disable() {
        plugin().getLogger().info("Disabling commands.");
        CommandAPI.getRegisteredCommands()
            .stream()
            .map(RegisteredCommand::commandName)
            .iterator()
            .forEachRemaining(CommandAPI::unregister);
        CommandAPI.onDisable();
    }

    private BlackWidow plugin() {
        return plugin;
    }

}

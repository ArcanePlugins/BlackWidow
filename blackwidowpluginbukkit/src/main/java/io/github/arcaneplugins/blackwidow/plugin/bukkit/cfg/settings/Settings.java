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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.settings;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.YamlCfg;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.DebugCategory;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collections;
import java.util.Objects;

public final class Settings extends YamlCfg {

    // <<< !!! WARNING !!! NOTICE THIS MESSAGE !!! >>>
    // REMEMBER TO UPDATE THE METHOD BELOW 'upgradeFile' IF THIS IS INCREMENTED.
    // ALSO REMEMBER TO UPDATE THE FILE ITSELF - BOTH THE 'original' AND 'installed' VALUES SHOULD MATCH THIS.
    // <<< !!! WARNING !!! NOTICE THIS MESSAGE !!! >>>
    private static final int LATEST_FILE_VERSION = 2;

    public Settings(
        final BlackWidow plugin
    ) {
        super(
            plugin,
            "settings.yml",
            "settings.yml",
            "Settings",
            LATEST_FILE_VERSION
        );
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    protected void loadMore() {
        try {
            plugin().enabledDebugCategories().clear();
            plugin().enabledDebugCategories().addAll(
                Objects.requireNonNullElse(
                    root()
                        .node("debug-categories")
                        .getList(DebugCategory.class),
                    Collections.emptySet()
                )
            );
        } catch (final ConfigurateException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public void upgradeFile() {
        final int installedVer = installedFileVersion();
        //noinspection SwitchStatementWithTooFewBranches
        switch (installedVer) {
            case 1 -> {
                final CommentedConfigurationNode updChkNode = root().node("update-checker");
                try {
                    updChkNode.node("enabled").set(true);
                    updChkNode.node("run-on-startup").set(true);
                    updChkNode.node("repeat-timer-duration-mins").set(60);
                    updChkNode.node("log-updates").set(true);
                    updChkNode.node("notify-players-with-permission").set(true);

                    root().node("do-not-touch", "version", "installed").set(2);
                } catch (SerializationException e) {
                    plugin().getLogger().warning("");
                }
            }
            default -> throw new IllegalArgumentException("No upgrade logic defined for file version v" + installedVer);
        }
    }
}

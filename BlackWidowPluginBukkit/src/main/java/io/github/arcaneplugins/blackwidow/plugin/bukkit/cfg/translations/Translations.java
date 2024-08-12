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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.translations;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.YamlCfg;

//TODO JAVADOCS
public final class Translations extends YamlCfg {

    // <<< !!! WARNING !!! NOTICE THIS MESSAGE !!! >>>
    // REMEMBER TO UPDATE THE METHOD BELOW 'upgradeFile' IF THIS IS INCREMENTED.
    // ALSO REMEMBER TO UPDATE THE FILE ITSELF - BOTH THE 'original' AND 'installed' VALUES SHOULD MATCH THIS.
    // <<< !!! WARNING !!! NOTICE THIS MESSAGE !!! >>>
    private static final int LATEST_FILE_VERSION = 1;

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    public Translations(
        final BlackWidow plugin
    ) {
        super(
            plugin,
            "translations.yml",
            "translations.yml",
            "Translations",
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
                // Do nothing.

                //noinspection UnnecessaryBreak
                break;
            }
            default -> throw new IllegalArgumentException("No upgrade logic defined for file version v" + installedVer);
        }
    }
}

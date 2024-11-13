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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.util;

import org.jetbrains.annotations.Nullable;

/**
 * {@link StringUtil} contains a variety of utilities related to {@link String Java String objects}.
 *
 * @author lokka30
 * @since 1.1.0
 */
public final class StringUtil {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDouble(
            @Nullable final String str
    ) {
        if (str == null || str.isBlank()) {
            return false;
        }

        try {
            Double.parseDouble(str);
            return true;
        } catch (final NumberFormatException ex) {
            return false;
        }
    }
}

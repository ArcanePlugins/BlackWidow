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

/**
 * {@link ClassUtil} contains a variety of utilities related to {@link Class Java classes}.
 *
 * @author lokka30
 * @since 1.0.0
 */
public final class ClassUtil {

    /**
     * Returns whether a class at the specified classpath exists at runtime. Useful for confirming certain features
     * are available before attempting to use them.
     *
     * @param classpath Classpath where the desired class is located.
     * @return Whether the class was found.
     * @author lokka30
     * @since 1.0.0
     */
    public static boolean classExists(final String classpath) {
        try {
            Class.forName(classpath);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDouble(final String str) {
        if (str == null || str.isEmpty()) {
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

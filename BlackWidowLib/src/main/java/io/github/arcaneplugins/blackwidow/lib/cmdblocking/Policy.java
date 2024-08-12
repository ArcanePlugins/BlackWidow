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

package io.github.arcaneplugins.blackwidow.lib.cmdblocking;

/**
 * Values that determine what decision is made on how a player's command should be actioned, i.e.,
 * allowed or denied.
 *
 * @author lokka30
 * @since 1.0.0
 */
public enum Policy {

    /**
     * Represents a decision to allow the command to run.
     *
     * @since 1.0.0
     */
    ALLOW,

    /**
     * Represents a decision to deny the command from being ran.
     *
     * @since 1.0.0
     */
    DENY,

}

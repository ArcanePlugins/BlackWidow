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

import java.util.Objects;

/**
 * Class which acts as a container for a result whether a command matched a rule.
 * <p>
 * This class also contains other information which is useful for debugging, such as what rule was matched, and a
 * description of what circumstance caused this rule to be triggered.
 * </p>
 *
 * @author lokka30
 * @since 1.0.0
 */
public final class MatchResult {

    private final boolean matched;
    private final String rule;
    private final String description;

    /**
     * Construct a {@code MatchResult}.
     *
     * @param matched     Whether the command matched the rule.
     * @param rule        Raw rule string containing the rule that the cmd matched. Null indicates no rule directly involved.
     * @param description A description indicating how the command matched the rule.
     * @author lokka30
     * @since 1.0.0
     */
    public MatchResult(
            final boolean matched,
            final String rule,
            final String description
    ) {
        this.matched = matched;
        this.rule = rule;
        this.description = Objects.requireNonNull(description, "description");
    }

    /**
     * Returns whether the command matched the rule.
     *
     * @return Whether the command matched the rule.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean matched() {
        return matched;
    }

    /**
     * Returns the raw rule string which the command matched, or an empty string if there was no match.
     * <b>Returns nullable value</b>.
     *
     * @return The raw rule string if matched, otherwise an empty string.
     * @author lokka30
     * @since 1.0.0
     */
    public String rule() {
        return rule;
    }

    /**
     * Returns a description useful for debugging.
     *
     * @return A description useful for debugging.
     * @author lokka30
     * @since 1.0.0
     */
    public String description() {
        return description;
    }

}

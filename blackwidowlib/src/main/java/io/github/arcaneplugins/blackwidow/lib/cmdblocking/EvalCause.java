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

import java.util.Collection;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Represents various causes (reasons) of a {@link Chain} being evaluated. Usually supplied via
 * {@link Evaluator#evaluate(String, Collection, Policy, boolean, EvalCause, Consumer, Consumer)}.
 *
 * @author lokka30
 * @since 1.0.0
 */
public enum EvalCause {

    /**
     * Cause of evaluation is due to command execution, e.g., user runs {@code /help}.
     *
     * @since 1.0.0
     */
    CMD_EXECUTION,

    /**
     * Cause of evaluation is due to command suggestion, e.g., user types {@code /hel} and is suggested {@code /help};
     * in this case, {@code /help} was the suggested command related to this {@link EvalCause}.
     *
     * @since 1.0.0
     */
    CMD_SUGGESTION,

    /**
     * Cause of this evaluation is unknown / a miscellaneous reason not listed here.
     *
     * @since 1.0.0
     */
    OTHER;

    private static final EnumSet<EvalCause> SET_CAUSES = EnumSet.allOf(EvalCause.class);

    /**
     * @return All values of this Enum as a {@link EnumSet}.
     * @author lokka30
     * @since 1.0.0
     */
    public static EnumSet<EvalCause> setValues() {
        return SET_CAUSES;
    }

}

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
 * Represents a total evaluation made on whether a player can access a command. Contains additional information such
 * as a reference to the particular rule matched, which {@link Chain chain} it belonged to, and a description of how
 * this evaluation was made.
 *
 * @author lokka30
 * @since 1.0.0
 */
public final class Evaluation {

    private final String command;
    private final Policy policy;
    private final Chain chain;
    private final String rule;
    private final String description;

    private boolean dueToColonInFirstArg = false;
    private boolean dueToException = false;
    private boolean dueToOperatorsBypassCmdBlocking = false;
    private boolean dueToDefaultPolicy = false;

    /**
     * Construct a {@code Evaluation}.
     *
     * @param command     The command evaluated against.
     * @param policy      The policy evaluated.
     * @param chain       The chain containing the {@code rule} that was matched. <b>Nullable</b>.
     * @param rule        The rule that was matched. <b>Nullable</b>.
     * @param description An explanation of how the evaluation was made.
     * @author lokka30
     * @since 1.0.0
     */
    public Evaluation(
        final String command,
        final Policy policy,
        final Chain chain,
        final String rule,
        final String description
    ) {
        this.command = Objects.requireNonNull(command, "command");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.chain = chain;
        this.rule = rule;
        this.description = Objects.requireNonNull(description, "description");
    }

    /**
     * Returns the command evaluated against.
     *
     * @return Command evaluated against.
     * @author lokka30
     * @since 1.0.0
     */
    public String command() {
        return command;
    }

    /**
     * Returns the policy that was evaluated.
     *
     * @return The policy that was evaluated.
     * @author lokka30
     * @since 1.0.0
     */
    public Policy policy() {
        return policy;
    }

    /**
     * Returns the chain containing the rule that was matched.
     * <b>Return value is nullable</b>.
     *
     * @return The chain containing the rule that was matched.
     * @author lokka30
     * @since 1.0.0
     */
    public Chain chain() {
        return chain;
    }

    /**
     * Returns the rule that was matched.
     * <b>Return value is nullable</b>.
     *
     * @return The rule that was matched.
     * @author lokka30
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public String rule() {
        return rule;
    }

    /**
     * Returns a description useful for debugging how the evaluation was made.
     *
     * @return Description of this evaluation.
     * @author lokka30
     * @since 1.0.0
     */
    public String description() {
        return description;
    }

    /**
     * Returns whether the evaluation was made due to a colon being found in the command's first argument when it was
     * not allowed to have one.
     *
     * @return Whether evaluation was due to colon in first argument when disallowed.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean dueToColonInFirstArg() {
        return dueToColonInFirstArg;
    }

    /**
     * Adjust whether the evaluation was made due to the colon being found in the command's first arg when disallowed.
     *
     * @param val New value.
     * @return Instance of self.
     * @author lokka30
     * @since 1.0.0
     */
    public Evaluation withDueToColonInFirstArg(final boolean val) {
        this.dueToColonInFirstArg = val;
        return this;
    }

    /**
     * Returns whether the evaluation was made due to an exception being thrown whilst attempting to evaluate the cmd.
     *
     * @return Whether evaluation was due to colon in first argument when disallowed.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean dueToException() {
        return dueToException;
    }

    /**
     * Adjust whether the evaluation was made due to an exception being thrown whilst attempting to evaluate the cmd.
     *
     * @param val New value.
     * @return Instance of self.
     * @author lokka30
     * @since 1.0.0
     */
    public Evaluation withDueToException(final boolean val) {
        this.dueToException = val;
        return this;
    }

    /**
     * @return Whether the evaluation was caused due to operators bypassing command blocking.
     * @author lokka30
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public boolean dueToOperatorsBypassCmdBlocking() {
        return dueToOperatorsBypassCmdBlocking;
    }

    /**
     * Adjust whether this evaluation was caused directly by operators bypassing command blocking.
     *
     * @param val New value.
     * @return Instance of self.
     * @author lokka30
     * @since 1.0.0
     */
    public Evaluation withDueToOperatorsBypassCmdBlocking(final boolean val) {
        this.dueToOperatorsBypassCmdBlocking = val;
        return this;
    }

    /**
     * @return Whether the evaluation was caused due a default policy being evaluated.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean dueToDefaultPolicy() {
        return dueToDefaultPolicy;
    }

    /**
     * Adjust whether this evaluation was caused directly by a default policy being used.
     *
     * @param val New value.
     * @return Instance of self.
     * @author lokka30
     * @since 1.0.0
     */
    public Evaluation withDueToDefaultPolicy(final boolean val) {
        this.dueToDefaultPolicy = val;
        return this;
    }

}

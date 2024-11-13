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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Evaluator {

    public static final AtomicBoolean SUPPRESS_EXCEPTION_MESSAGES = new AtomicBoolean(false);

    public static Evaluation evaluate(
            final String cmd,
            final Collection<? extends Chain> chains,
            final Policy defaultPolicy,
            final boolean denyColonInFirstArg,
            final EvalCause cause,
            final Consumer<Supplier<String>> debugLogger,
            final Consumer<Supplier<String>> warningLogger
    ) {
        try {
            Objects.requireNonNull(cmd, "cmd");
            Objects.requireNonNull(chains, "chains");
            Objects.requireNonNull(defaultPolicy, "defaultPolicy");
            Objects.requireNonNull(debugLogger, "debugLogger");
            Objects.requireNonNull(warningLogger, "warningLogger");

            if (cmd.isEmpty()) {
                throw new IllegalArgumentException("cmd can't be empty");
            }

            if (cmd.charAt(0) != '/') {
                throw new IllegalArgumentException("cmd must start with a slash (/)");
            }

            int i = 0;
            for (Chain chain : chains) {
                final MatchResult res = chain.matches(cmd, cause, debugLogger);
                if (res.matched()) {
                    return new Evaluation(
                            cmd,
                            chain.policy(),
                            chain,
                            res.rule(),
                            "in chain #" + i + " (id='" + chain.id() + "'; " + res.description()
                    );
                }
                i++;
            }

            if (denyColonInFirstArg && Chain.transformToArgs(cmd)[0].contains(":")) {
                return new Evaluation(cmd, Policy.DENY, null, null, "colon found in first arg")
                        .withDueToColonInFirstArg(true);
            }
        } catch (Exception ex) {
            if (!SUPPRESS_EXCEPTION_MESSAGES.get()) {
                warningLogger.accept(() ->
                        "An exception was caught whilst evaluating cmd '" + cmd + "': '" + ex.getClass().getSimpleName() +
                                "'. For best security practice, this cmd will be forcefully denied due to the error. " +
                                "Message: '" + ex.getMessage() + "'; Stack trace:\n"
                );
                //noinspection CallToPrintStackTrace
                ex.printStackTrace();
            }
            return new Evaluation(
                    cmd == null ? "/??? NULL COMMAND ???" : cmd,
                    Policy.DENY,
                    null,
                    null,
                    "exception caught, denying for security"
            ).withDueToException(true);
        }

        return new Evaluation(
                cmd,
                defaultPolicy,
                null,
                null,
                "default policy, no chains matched"
        ).withDueToDefaultPolicy(true);
    }
}

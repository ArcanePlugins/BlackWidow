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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Represents a {@code rules} sequence, and logic for if any of its rules are matched by a command, what policy to deal
 * with the command that was matched.
 * <p>
 * A {@code rule} is a string used to match commands against. For example, {@code /cd *} will match {@code /cd hello},
 * {@code /cd hey how are you}, but not {@code /cd} (since the wildcard on the argument at index {@code 1} requires
 * an argument there and matches against anything present.
 * </p>
 *
 * @author lokka30
 * @since 1.0.0
 */
public class Chain {

    private final String id;
    private final boolean enabled;
    private final Policy policy;
    private final Collection<String> rules;
    private final boolean isRegex;
    private final Map<String, Pattern> rulesRegexPatterns;
    private final Map<String, String[]> rulesAsArgs;
    private final Collection<EvalCause> causeFilters;

    /**
     * Constructs a {@code Chain}.
     *
     * @param id           Unique identifier for this chain.
     * @param enabled      Whether this chain is enabled.
     * @param policy       How a command should be dealt with (e.g., {@code DENY}) if any of the {@code rules} are matched.
     * @param rules        A collection of strings used to match commands against, an example being {@code /hello * are you}.
     * @param isRegex      Whether this chain's rules are regular expressions (regex).
     * @param causeFilters A collection of causes which this chain will only evaluate against.
     * @author lokka30
     * @since 1.0.0
     */
    public Chain(
        final String id,
        final boolean enabled,
        final Policy policy,
        final Collection<String> rules,
        final boolean isRegex,
        final Collection<EvalCause> causeFilters
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.enabled = enabled;
        this.policy = Objects.requireNonNull(policy, "policy");
        this.rules = Objects.requireNonNull(rules, "rules");
        this.isRegex = isRegex;
        this.rulesRegexPatterns = new HashMap<>(rules.size());
        this.rulesAsArgs = new HashMap<>(rules.size());
        this.causeFilters = causeFilters;

        if (id().isEmpty()) {
            throw new IllegalArgumentException("id can't be empty");
        }

        if (!isRegex()) {
            for (final String rule : rules()) {
                if(rule.startsWith("/")) {
                    continue;
                }

                throw new IllegalArgumentException("Rule string='" + rule + "' in chain id='" + id() + "' does" +
                    "not start with a slash (/); for a non-regex chain, all rules must start with a slash");
            }
        }

        updatePrecompiledMaps();
    }

    /**
     * Update the precompiled maps, being {@link Chain#rulesRegexPatterns()} and {@link Chain#rulesAsArgs()}.
     *
     * @author lokka30
     * @since 1.0.0
     */
    public void updatePrecompiledMaps() {
        rulesAsArgs.clear();
        rulesRegexPatterns.clear();

        for (final String rule : rules) {
            if (rule.isEmpty()) {
                throw new IllegalArgumentException("rule can't be empty");
            }

            if (isRegex()) {
                rulesRegexPatterns.put(rule, Pattern.compile(rule, Pattern.CASE_INSENSITIVE));
            } else {
                if (rule.charAt(0) != '/') {
                    throw new IllegalArgumentException("rule must begin with a slash (/)");
                }

                rulesAsArgs.put(rule, transformToArgs(rule));
            }
        }
    }

    /**
     * Returns this {@code Chain}'s policy.
     *
     * @return Policy of this {@code Chain}.
     * @author lokka30
     * @since 1.0.0
     */
    public final Policy policy() {
        return Objects.requireNonNull(policy, "policy");
    }

    /**
     * Returns this {@code Chain}'s rules.
     *
     * @return Rules of this {@code Chain}.
     * @author lokka30
     * @since 1.0.0
     */
    public final Collection<String> rules() {
        return Objects.requireNonNull(rules, "rules");
    }

    /**
     * Returns if this {@code Chain} uses Regex for matching rules.
     *
     * @return If this {@code} Chain uses Regex for matching rules.
     * @author lokka30
     * @since 1.0.0
     */
    public final boolean isRegex() {
        return isRegex;
    }

    /**
     * Returns the precompiled regex patterns for this chain.
     * <p>
     * <b>
     * This chain MUST use regex (see {@link Chain#isRegex()}) or this will fire a {@link IllegalStateException}!
     * </b>
     * </p>
     *
     * @return precompiled regex patterns for the chain's rules (non-null)
     * @author lokka30
     * @since 1.0.0
     */
    public final Map<String, Pattern> rulesRegexPatterns() {
        if (!isRegex()) {
            throw new IllegalStateException("Chain does not use regex rules and thus can't have regex patterns");
        }
        return Objects.requireNonNull(rulesRegexPatterns, "rulesRegexPatterns");
    }

    /**
     * Returns the precompiled rules as sanitized args.
     * <p>
     * <b>
     * This chain MUST NOT use regex (see {@link Chain#isRegex()}) or this will fire a {@link IllegalStateException}!
     * </b>
     * </p>
     *
     * @return precompiled rules as args for the chain's rules (non-null)
     * @author lokka30
     * @since 1.0.0
     */
    public final Map<String, String[]> rulesAsArgs() {
        if (isRegex()) {
            throw new IllegalStateException("Chain uses regex rules and thus can't have rules as args");
        }

        return Objects.requireNonNull(rulesAsArgs, "rulesAsArgs");
    }

    /**
     * Returns if this chain is enabled.
     *
     * @return Whether this chain is enabled.
     * @author lokka30
     * @since 1.0.0
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean enabled() {
        return enabled;
    }

    /**
     * Returns this chain's ID.
     *
     * @return This chain's ID.
     * @author lokka30
     * @since 1.0.0
     */
    public final String id() {
        return id;
    }

    /**
     * Transforms a command or rule string into an array of arguments, sanitized to make comparing various args simple.
     * <p>
     * {@code str}'s starting slash is removed, then converted to lowercase, then trimmed of whitespace, and finally,
     * split by any whitespace into an array of arguments.
     * </p>
     *
     * @param str A command or rule string.
     * @return {@code str} converted into a lowercase sanitized array of whitespace-separated arguments.
     * @author lokka30
     * @since 1.0.0
     */
    protected static String[] transformToArgs(
        final String str
    ) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("str parameter must not be empty");
        }

        if (str.charAt(0) != '/') {
            throw new IllegalArgumentException("str parameter must start with a slash (/)");
        }

        return str
            .substring(1) // we don't want the starting slash.
            .toLowerCase(Locale.ROOT) // let's use lowercase for case insensitivity.
            .trim() // trim leading and trialing whitespace otherwise it can break String#split below.
            .split("\\s+"); // finally, split by whitespace via regex.
    }

    /**
     * Match a particular {@code command} against a particular {@code rule}. USES REGEXP.
     *
     * @param command  Command to match with.
     * @param rule     Rule to match against.
     * @param pattern  Pattern to match against.
     * @param debugger Consumer accepting debug logs.
     * @return A {@link MatchResult match result} explaining whether it matched and how it determined the outcome.
     * @author lokka30
     * @since 1.0.0
     */
    private MatchResult matchRulePattern(
        final String command,
        final String rule,
        final Pattern pattern,
        final EvalCause cause,
        final Consumer<Supplier<String>> debugger
    ) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(rule, "rule");
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(debugger, "debugger");

        if (!enabled()) {
            return new MatchResult(false, rule, "chain is disabled");
        }

        if (!causeFilters().contains(cause)) {
            return new MatchResult(false, rule, "cause not in causeFilters");
        }

        if (command.isEmpty()) {
            throw new IllegalArgumentException("cmd can't be empty");
        }

        if (command.charAt(0) != '/') {
            throw new IllegalArgumentException("cmd must start with a slash (/)");
        }

        if (rule.isEmpty()) {
            throw new IllegalArgumentException("rule can't be empty");
        }

        if (!isRegex()) {
            throw new IllegalStateException("Chain must have regexp rules");
        }

        return new MatchResult(
            pattern.matcher(command).find(),
            rule,
            "regex pattern match find result"
        );
    }

    /**
     * Match a particular {@code command} against a particular {@code rule}. NOT regexp.
     *
     * @param cmdArgs  Command to match with.
     * @param ruleArgs Rule to match against.
     * @param debugger Consumer accepting debug logs.
     * @return A {@link MatchResult match result} explaining whether it matched and how it determined the outcome.
     * @author lokka30
     * @since 1.0.0
     */
    private MatchResult matchRuleArgs(
        final String cmd,
        final String[] cmdArgs,
        final String rule,
        final String[] ruleArgs,
        final EvalCause cause,
        final Consumer<Supplier<String>> debugger
    ) {
        Objects.requireNonNull(cmd, "cmd");
        Objects.requireNonNull(cmdArgs, "cmdArgs");
        Objects.requireNonNull(rule, "rule");
        Objects.requireNonNull(ruleArgs, "ruleArgs");
        Objects.requireNonNull(debugger, "debugger");

        if (!enabled()) {
            return new MatchResult(false, rule, "chain is disabled");
        }

        if (!causeFilters().contains(cause)) {
            return new MatchResult(false, rule, "cause not in causeFilters");
        }

        final int minLen = Math.min(cmdArgs.length, ruleArgs.length);

        debugger.accept(() -> "matchRule: Checking cmd='" + cmd + "', " +
            "rule='" + rule + "', " +
            "minLen='" + minLen + "', " +
            "cmdArgs='" + Arrays.toString(cmdArgs) + "' (len='" + cmdArgs.length + "'), " +
            "ruleArgs='" + Arrays.toString(ruleArgs) + "' (len='" + ruleArgs.length + "').");

        if (ruleArgs.length == 0 && cmdArgs.length == 0) {
            debugger.accept(() -> "matchRule: Yes, because rule args and cmd args are both empty arrays.");
            return new MatchResult(true, rule, "rule and cmd are both empty");
        }

        if (ruleArgs.length > cmdArgs.length) {
            debugger.accept(() -> "matchRule: No, because it's impossible for this command to match this rule, " +
                "because it doesn't have an equal or greater number args.");
            return new MatchResult(false, rule, "rule has more args than cmd, thus " +
                "impossible to match");
        }

        for (int i = 0; i < minLen; i++) {
            final String cmdArg = cmdArgs[i];
            final String ruleArg = ruleArgs[i];

            final int finalI = i;
            debugger.accept(() -> "matchRule [i='" + finalI + "']: cmdArg='" + cmdArg + "', ruleArg='" + ruleArg + "'.");

            // if the args don't match and the rule arg isn't a wildcard, nope
            if (!cmdArg.equals(ruleArg) && !ruleArg.equals("*")) {
                debugger.accept(() -> "matchRule: No, since args don't match and ruleArg is not a wildcard");
                return new MatchResult(false, rule, "arg at index '" + i + "' didn't match, and " +
                    "ruleArg is not a wildcard");
            }

            // if we're looking at the last arg of the rule, we've found a match.
            if (i + 1 == ruleArgs.length) {
                debugger.accept(() -> "matchRule: Yes, matched all args of rule.");
                return new MatchResult(true, rule, "cmd matches rule");
            }

            // otherwise, check that all next args (if present) match
        }

        // fallback to nope
        debugger.accept(() -> "matchRule: No, there weren't any arg matches");
        return new MatchResult(false, rule, "cmd doesn't match rule");
    }

    /**
     * Match a particular {@code command} against this chain's {@link Chain#rules() rules}.
     *
     * @param command  Command to match with.
     * @param debugger Consumer accepting debug logs.
     * @return A {@link MatchResult match result} explaining whether it matched and how it determined the outcome.
     * @author lokka30
     * @since 1.0.0
     */
    public final MatchResult matches(
        final String command,
        final EvalCause cause,
        final Consumer<Supplier<String>> debugger
    ) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(debugger, "debugger");

        if (command.isEmpty()) {
            throw new IllegalArgumentException("command must not be empty");
        }
        if (command.charAt(0) != '/') {
            throw new IllegalArgumentException("command must start with a slash (/)");
        }

        if (!enabled()) {
            return new MatchResult(false, null, "chain is disabled");
        }

        if (!causeFilters().contains(cause)) {
            return new MatchResult(false, null, "cause not in causeFilters");
        }

        debugger.accept(() -> "matches: Checking cmd='" + command + "' on chain, len='" + rules().size() + "'.");

        for (final String rule : rules) {
            debugger.accept(() -> "matches: Checking rule '" + rule + "'.");

            final MatchResult res = isRegex() ?
                matchRulePattern(command, rule, rulesRegexPatterns().get(rule), cause, debugger) :
                matchRuleArgs(command, transformToArgs(command), rule, rulesAsArgs().get(rule), cause, debugger);

            if (res.matched()) {
                debugger.accept(() -> "matches: Matched!");
                return new MatchResult(
                    true,
                    res.rule(),
                    "in rule '" + rule + "'; " + res.description()
                );
            }

            debugger.accept(() -> "matches: Didn't match, continuing.");
        }

        debugger.accept(() -> "matches: No rules matched in this chain.");
        return new MatchResult(false, null, "no rule matched");
    }

    /**
     * @return A collection which specifies what {@link EvalCause}(s) are able to evaluate against the chain.
     */
    public final Collection<EvalCause> causeFilters() {
        return this.causeFilters;
    }

}

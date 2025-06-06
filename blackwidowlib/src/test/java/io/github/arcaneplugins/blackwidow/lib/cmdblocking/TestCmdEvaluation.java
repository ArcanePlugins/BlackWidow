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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public final class TestCmdEvaluation {

    public TestCmdEvaluation() {
        TEST_CHAINS.addAll(
                asList(
                        new Chain("0", true, Policy.DENY, asList("/cd reload *", "/"), false, EvalCause.setValues()),
                        new Chain("1", true, Policy.ALLOW, singletonList("/cd reload"), false, EvalCause.setValues()),
                        new Chain("2", true, Policy.DENY, singletonList("/cd"), false, EvalCause.setValues()),
                        new Chain("3", true, Policy.DENY, asList(TEST_PLUGIN_CMDS), false, EvalCause.setValues()),
                        new Chain("4", true, Policy.ALLOW, singletonList("/es version"), false, EvalCause.setValues()),
                        new Chain("5", true, Policy.DENY, asList("/es give", "/es enchant"), false, EvalCause.setValues()),
                        new Chain("6", true, Policy.ALLOW, singletonList("^(/heywhats(up)?(?:$|\\W)cool(beans)?(?:$|\\W).*)"), true, EvalCause.setValues()),
                        new Chain("7", true, Policy.DENY, singletonList("^(/heywhats(up)?(?:$|\\W).*)"), true, EvalCause.setValues()),
                        new Chain("8", false, Policy.DENY, singletonList("/thisshouldnotbedenied"), false, EvalCause.setValues()),
                        new Chain("9", true, Policy.DENY, singletonList("/blocksuggestionsonly"), false, EnumSet.of(EvalCause.CMD_SUGGESTION)),
                        new Chain("10", true, Policy.DENY, singletonList("/UPPERCASE"), false, EvalCause.setValues()),
                        new Chain("11", true, Policy.DENY, singletonList("/suggestion argument"), false, Collections.singleton(EvalCause.CMD_SUGGESTION))
                )
        );
    }

    // Various 'plugin checking' commands
    private static final String[] TEST_PLUGIN_CMDS = {
        "/plugins", "/pl", "/version", "/ver", "/icanhasbukkit",
        "/about", "/?", "/help", "/ehelp", "/paper", "/spigot"
    };

    // Various programatically defined chains of rules, some regex ones provided at the bottom.
    // Chains should NOT contain 'donotuseinchains', doing so will make some tests fail (by design).
    private static final Collection<Chain> TEST_CHAINS = new LinkedList<>();

    // Commands to be tested which are expected to be evaluated with a DENY policy.
    public static final String[] TEST_CMDS_EXPECTING_DENY = {
        "/cd abc", "/cd reloada", "/cd reloa", "/cd reload abc",
        "/plugins", "/pl", "/version", "/ver", "/icanhasbukkit",
        "/about", "/?", "/help", "/ehelp", "/paper", "/spigot",
        "/es give", "/es enchant", "/cd", "/", "/:", "/hello:how",
        "/the:quick brown fox", "/bukkit:help", "/uppercase",
        "/UPPERCASE",
        // regex:
        "/heywhats",
        "/heywhats coolio",
        "/heywhats verycool",
        "/heywhatsup",
        "/heywhatsup coolio",
        "/heywhatsup verycool",
    };

    // Commands to be tested which are expected to be evaluated with an ALLOW policy.
    public static final String[] TEST_CMDS_EXPECTING_ALLOW = {
        "/cd reload", "/CD RELOAD", "//", "/es", "/es version", "/abcdefg", "/thisshouldnotbedenied",
        // regex:
        "/heywhats cool",
        "/heywhats coolbeans",
        "/heywhatsup cool",
        "/heywhatsup coolbeans",
        "/heywhatsup cool beans",
    };

    // Commands to be tested which contains colons.
    // 'donotuseinchains' term is used to make it clear PLEASE DO NOT USE THIS TERM IN ANY CHAINS or it can mess
    // up the tests.
    public static final String[] TEST_CMDS_WITH_COLONS = {
        "/donotuseinchains:donotuseinchains",
        "/donotuseinchains:donotuseinchains donotuseinchains",
    };

    // Commands to be tested which do NOT contain colons.
    // 'donotuseinchains' term is used to make it clear PLEASE DO NOT USE THIS TERM IN ANY CHAINS or it can mess
    // up the tests.
    public static final String[] TEST_CMDS_WITHOUT_COLONS = {
        "/donotuseinchains",
        "/donotuseinchains donotuseinchains",
    };

    // Default policy to be used during these tests. ALLOW here represents a whitelist which is the usual setup.
    public static final Policy TEST_DEFAULT_POLICY = Policy.ALLOW;

    // Handle debug logs here
    private static final Consumer<Supplier<String>> debugLogger = (msg) -> {
    }; // Do nothing

    // Handle warning logs here
    private static final Consumer<Supplier<String>> warningLogger = (msg) -> System.out.println("[WARN] " + msg.get());

    /**
     * Short hand version of running the test evaluations.
     *
     * @param cmd                 Command to evaluate.
     * @param denyColonInFirstArg Whether colons in the first arg should cause a denied evaluation.
     * @return {@link Evaluation Evaluation} made with the provided parameters.
     */
    private static Evaluation testEvaluation(
        final String cmd,
        final boolean denyColonInFirstArg
    ) {
        return Evaluator.evaluate(
            cmd,
            TEST_CHAINS,
            TEST_DEFAULT_POLICY,
            denyColonInFirstArg,
            EvalCause.CMD_EXECUTION,
            debugLogger,
            warningLogger
        );
    }

    /**
     * Ensures all chains rules start with a slash if it is not a regex chain.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testChainsMustStartWithSlash() {
        for (final Chain chain : TEST_CHAINS) {
            if(chain.isRegex()) {
                continue;
            }

            for(final String rule : chain.rules()) {
                Assertions.assertTrue(
                    rule.startsWith("/"),
                    "Non-regex chain rules must start with a slash, failed on rule='" + rule + "'"
                );
            }
        }
    }

    /**
     * Ensures wildcard base command rules detect all base commands.
     *
     * @author lokka30
     * @since 1.1.5
     */
    @Test
    public void testWildcardBaseCmd() {
        final BiFunction<String, Policy, Boolean> test = (cmd, policy) -> {
            final Evaluation eval = Evaluator.evaluate(
                    "/hello",
                    singletonList(new Chain(
                            "wildcard-base-cmd",
                            true,
                            Policy.DENY,
                            singletonList("/*"),
                            false,
                            EvalCause.setValues()
                    )),
                    Policy.ALLOW,
                    false,
                    EvalCause.CMD_EXECUTION,
                    debugLogger,
                    warningLogger
            );

            return eval.policy() == policy && !eval.dueToException();
        };

        Assertions.assertTrue(test.apply("/hello", Policy.DENY), "failed wildcard base cmd match");
        Assertions.assertTrue(test.apply("/something abc", Policy.DENY), "failed wildcard base cmd match with an argument");
    }

    /**
     * Ensures wildcard argument command rules detect all command arguments.
     *
     * @author lokka30
     * @since 1.1.5
     */
    @Test
    public void testWildcardArgs() {
        final BiFunction<String, Policy, Boolean> test = (cmd, policy) -> {
            final Evaluation eval = Evaluator.evaluate(
                    cmd,
                    singletonList(new Chain(
                            "wildcard-arg-cmd",
                            true,
                            policy,
                            singletonList("/hello * world *"),
                            false,
                            EvalCause.setValues()
                    )),
                    Policy.ALLOW,
                    false,
                    EvalCause.CMD_EXECUTION,
                    debugLogger,
                    warningLogger
            );

            return eval.policy() == policy && !eval.dueToException();
        };

        Assertions.assertTrue(test.apply("/hello something world anything123", Policy.DENY));
        Assertions.assertTrue(test.apply("/hello another world thing withmorestuff at the end", Policy.DENY));
        Assertions.assertTrue(test.apply("/hello * world *", Policy.DENY));
        Assertions.assertTrue(test.apply("/hello * world * hasmorestuff", Policy.DENY));

        Assertions.assertTrue(test.apply("/nothello a world a", Policy.ALLOW));
        Assertions.assertTrue(test.apply("/hello something notworld anotherthinghere", Policy.ALLOW));
    }

    /**
     * Example to ensure all commands expected to be denied are.. denied.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testRulesExpectingDeny() {
        for (final String cmd : TEST_CMDS_EXPECTING_DENY) {
            final Evaluation eval = testEvaluation(cmd, true);
            Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
            Assertions.assertEquals(Policy.DENY, eval.policy(), "Wrong policy evaluated for cmd='" +
                cmd + "'; description='" + eval.description() + "'");
        }
    }

    /**
     * Example to ensure all commands expected to be allowed are.. allowed.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testRulesExpectingAllow() {
        for (final String cmd : TEST_CMDS_EXPECTING_ALLOW) {
            final Evaluation eval = testEvaluation(cmd, true);
            Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
            Assertions.assertEquals(Policy.ALLOW, eval.policy(), "Wrong policy evaluated for cmd='" +
                cmd + "'; description='" + eval.description() + "'");
        }
    }

    /**
     * Example to ensure 'colon in first arg' commands are being denied when present AND when denyColonInFirstArg
     * is enabled on the evaluate method.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testColonBlocking() {
        for (final String cmd : TEST_CMDS_WITH_COLONS) {
            {
                final Evaluation eval = testEvaluation(cmd, true);

                Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
                Assertions.assertTrue(eval.dueToColonInFirstArg(), "Colon in first arg expected to be cause");
                Assertions.assertEquals(Policy.DENY, eval.policy(),
                    "Wrong policy evaluated for cmd='" + cmd + "'; description='" +
                        eval.description() + "'");
            }

            {
                final Evaluation eval = testEvaluation(cmd, false);

                Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
                Assertions.assertFalse(eval.dueToColonInFirstArg(), "Colon in first arg unexpected to be cause");
                Assertions.assertEquals(Policy.ALLOW, eval.policy(),
                    "Wrong policy evaluated for cmd='" + cmd + "'; description='" +
                        eval.description() + "'");
            }
        }

        for (final String cmd : TEST_CMDS_WITHOUT_COLONS) {
            {
                final Evaluation eval = testEvaluation(cmd, true);

                Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
                Assertions.assertFalse(eval.dueToColonInFirstArg(), "Colon in first arg unexpected to be cause");
                Assertions.assertEquals(Policy.ALLOW, eval.policy(),
                    "Wrong policy evaluated for cmd='" + cmd + "'; description='" +
                        eval.description() + "'");
            }

            {
                final Evaluation eval = testEvaluation(cmd, false);
                Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
                Assertions.assertFalse(eval.dueToColonInFirstArg(), "Colon in first arg unexpected to be cause");
                Assertions.assertEquals(Policy.ALLOW, eval.policy(),
                    "Wrong policy evaluated for cmd='" + cmd + "'; description='" +
                        eval.description() + "'");
            }
        }
    }

    /**
     * Example regex to block /pl and /plugins.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testPluginCheckingRegex() {
        final String regex = "^(/pl(ugins)?(?:$|\\W).*)";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Assertions.assertTrue(pattern.matcher("/pl").find(), "Invalid regex find result");
        Assertions.assertTrue(pattern.matcher("/pl help").find(), "Invalid regex find result");
        Assertions.assertTrue(pattern.matcher("/plugins").find(), "Invalid regex find result");
        Assertions.assertTrue(pattern.matcher("/plugins help").find(), "Invalid regex find result");
        Assertions.assertTrue(pattern.matcher("/plugins help /pl").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/plot").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/player").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/tpa").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/help").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/help /pl").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/help /plugins").find(), "Invalid regex find result");
        Assertions.assertFalse(pattern.matcher("/help /plugins help /pl").find(), "Invalid regex find result");
    }

    /**
     * Example to ensure default policy of 'DENY' works ('ALLOW' is already tested thoroughly in other tests).
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testDenyDefaultPolicy() {
        final Evaluation eval = Evaluator.evaluate(
            "/donotuseinchains",
            TEST_CHAINS,
            Policy.DENY,
            true,
            EvalCause.CMD_EXECUTION,
            debugLogger,
            warningLogger
        );

        Assertions.assertFalse(eval.dueToException(), "Exception not expected here");
        Assertions.assertEquals(Policy.DENY, eval.policy(), "Wrong policy evaluated");
    }

    /**
     * Example to ensure an exception being fired in the evaluation process causes a DENY policy to be evaluated
     * for best-practice security.
     * <p>
     * To stop the exception messages from being printed whilst testing this particular case, the
     * {@link Evaluator#SUPPRESS_EXCEPTION_MESSAGES SUPPRESS_EXCEPTION_MESSAGES} flag is enabled during the test.
     * </p>
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testExceptionDeniesCmd() {
        Evaluator.SUPPRESS_EXCEPTION_MESSAGES.set(true);

        final Evaluation eval = testEvaluation("DoesNotHaveStartingSlash", true);

        Assertions.assertTrue(eval.dueToException(), "Intentional exception was expected here");
        Assertions.assertEquals(Policy.DENY, eval.policy(), "Wrong security policy evaluated");

        Evaluator.SUPPRESS_EXCEPTION_MESSAGES.set(false);
    }

    /**
     * Tests that the 'EvalCause' feature is working correctly - i.e., chains having a criteria of a particular
     * eval cause being used should be respected.
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Test
    public void testEvalCauseFiltering() {
        final String cmd = "/blocksuggestionsonly";

        final Function<EvalCause, Evaluation> eval = (cause) -> Evaluator.evaluate(
            cmd,
            TEST_CHAINS,
            TEST_DEFAULT_POLICY,
            true,
            cause,
            debugLogger,
            warningLogger
        );

        final Evaluation evalExecution = eval.apply(EvalCause.CMD_EXECUTION);
        final Evaluation evalSuggestion = eval.apply(EvalCause.CMD_SUGGESTION);
        final Evaluation evalOther = eval.apply(EvalCause.OTHER);

        Assertions.assertTrue(evalExecution.dueToDefaultPolicy());
        Assertions.assertSame(Policy.ALLOW, evalExecution.policy());

        Assertions.assertFalse(evalSuggestion.dueToDefaultPolicy()); // << notice
        Assertions.assertSame(Policy.DENY, evalSuggestion.policy()); // << notice

        Assertions.assertTrue(evalOther.dueToDefaultPolicy());
        Assertions.assertSame(Policy.ALLOW, evalOther.policy());
    }

    /**
     * Tests that command suggestion filtering is working with arguments too.
     *
     * @author lokka30
     * @since 1.1.6
     */
    @Test
    public void testSuggestionFilteringArgumentsWorks() {
        final Collection<String> cmdsShouldAllow = asList(
                "/suggestion argumento",
                "/suggestion argumen argument",
                "/suggestion"
        );
        final Collection<String> cmdsShouldDeny = asList(
                "/suggestion argument",
                "/suggestion argument test"
        );

        cmdsShouldAllow.forEach(cmd -> Assertions.assertSame(Policy.ALLOW, Evaluator.evaluate(
                cmd,
                TEST_CHAINS,
                TEST_DEFAULT_POLICY,
                true,
                EvalCause.CMD_SUGGESTION,
                debugLogger,
                warningLogger
        ).policy()));

        cmdsShouldDeny.forEach(cmd -> Assertions.assertSame(Policy.DENY, Evaluator.evaluate(
                cmd,
                TEST_CHAINS,
                TEST_DEFAULT_POLICY,
                true,
                EvalCause.CMD_SUGGESTION,
                debugLogger,
                warningLogger
        ).policy()));
    }
}

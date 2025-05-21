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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.component.cmdblocking;

import io.github.arcaneplugins.blackwidow.lib.cmdblocking.EvalCause;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Evaluation;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Evaluator;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Policy;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Action;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Context;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.DebugCategory;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;

public final class CmdBlocker {

    private final BlackWidow plugin;

    public CmdBlocker(final BlackWidow plugin) {
        this.plugin = plugin;
    }

    private Policy defaultBehaviourPolicy;
    private final Collection<Action> defaultBehaviourActions = new LinkedHashSet<>();
    private boolean filterCmdExecution;
    private boolean filterCmdSuggestion;
    private boolean operatorsBypassCompletely;
    private boolean denyColonInFirstArgEnabled;
    private final Collection<Action> denyColonInFirstArgActions = new LinkedHashSet<>();
    private final Collection<BukkitChain> chains = new LinkedHashSet<>();

    public void load() {
        final CommentedConfigurationNode settingsRoot = plugin().settings().root();

        try {
            // defaultBehaviourPolicy
            setDefaultBehaviourPolicy(settingsRoot.node("cmd-blocking", "default-behaviour", "policy").get(Policy.class));

            // defaultBehaviourActions
            defaultBehaviourActions().clear();
            defaultBehaviourActions().addAll(plugin().logicManager().parseActionsInChildrenOfNode(settingsRoot.node("cmd-blocking", "default-behaviour", "actions")));

            // filterCmdExecution
            setFilterCmdExecution(settingsRoot.node("cmd-blocking", "filtering", "execution").getBoolean(true));

            // filterCmdSuggestion
            setFilterCmdSuggestion(settingsRoot.node("cmd-blocking", "filtering", "suggestion").getBoolean(true));

            // operatorsBypassCompletely
            setOperatorsBypassCompletely(settingsRoot.node("cmd-blocking", "operators-bypass-completely").getBoolean(true));

            // denyColonInFirstArgEnabled
            setDenyColonInFirstArgEnabled(settingsRoot.node("cmd-blocking", "deny-colon-in-first-arg", "enabled").getBoolean(true));

            // denyColonInFirstArgActions
            denyColonInFirstArgActions().clear();
            denyColonInFirstArgActions().addAll(plugin().logicManager().parseActionsInChildrenOfNode(settingsRoot.node("cmd-blocking", "deny-colon-in-first-arg", "actions")));

            // chains
            parseChains();
        } catch (final Exception ex) {
            throw new RuntimeException("Unable to load CmdBlocker", ex);
        }
    }

    private void parseChains() {
        final CommentedConfigurationNode chainsRoot = plugin().settings().root().node("cmd-blocking", "chains");

        chains().clear();

        for (final CommentedConfigurationNode chainNode : chainsRoot.childrenList()) {
            chains.add(new BukkitChain(plugin(), chainNode));
        }
    }

    public Evaluation evalAndProcess(final Context context, final String cmd, final boolean runActions, final EvalCause cause) {
        final Player player = context.player(false);
        if (player != null && player.isOp() && operatorsBypassCompletely()) {
            return new Evaluation(cmd, Policy.ALLOW, null, null, "Operators configured to bypass command blocking")
                    .withDueToOperatorsBypassCmdBlocking(true);
        }

        // copy chains into a new LinkedHashSet, remove chains not applicable to context.
        final Collection<BukkitChain> applicableChains = new LinkedHashSet<>(chains());
        applicableChains.removeIf(chain -> chain.requirements().stream().anyMatch(req -> !req.validate(context)));

        final Evaluation eval = Evaluator.evaluate(
                cmd,
                applicableChains,
                defaultBehaviourPolicy(),
                denyColonInFirstArgEnabled(),
                cause,
                debugMsg -> plugin().debugLog(DebugCategory.CMD_DETECTION_ENGINE, debugMsg),
                warnMsg -> plugin().getLogger().warning(warnMsg.get())
        );

        if (runActions && eval.dueToColonInFirstArg()) {
            denyColonInFirstArgActions().forEach(act -> act.run(context));
        }

        if (runActions && eval.chain() != null && eval.chain() instanceof BukkitChain chain) {
            chain.actions().forEach(act -> act.run(context));
        }

        if (runActions && eval.dueToDefaultPolicy()) {
            defaultBehaviourActions().forEach(act -> act.run(context));
        }

        return eval;
    }

    private BlackWidow plugin() {
        return plugin;
    }

    public void setDefaultBehaviourPolicy(Policy defaultBehaviourPolicy) {
        this.defaultBehaviourPolicy = defaultBehaviourPolicy;
    }

    public Policy defaultBehaviourPolicy() {
        return defaultBehaviourPolicy;
    }

    public Collection<Action> defaultBehaviourActions() {
        return Objects.requireNonNull(defaultBehaviourActions, "defaultBehaviourActions");
    }

    public boolean filterCmdExecution() {
        return filterCmdExecution;
    }

    public void setFilterCmdExecution(boolean filterCmdExecution) {
        this.filterCmdExecution = filterCmdExecution;
    }

    public boolean filterCmdSuggestion() {
        return filterCmdSuggestion;
    }

    public void setFilterCmdSuggestion(boolean filterCmdSuggestion) {
        this.filterCmdSuggestion = filterCmdSuggestion;
    }

    public boolean operatorsBypassCompletely() {
        return operatorsBypassCompletely;
    }

    public void setOperatorsBypassCompletely(boolean operatorsBypassCompletely) {
        this.operatorsBypassCompletely = operatorsBypassCompletely;
    }

    public boolean denyColonInFirstArgEnabled() {
        return denyColonInFirstArgEnabled;
    }

    public void setDenyColonInFirstArgEnabled(boolean denyColonInFirstArgEnabled) {
        this.denyColonInFirstArgEnabled = denyColonInFirstArgEnabled;
    }

    public Collection<Action> denyColonInFirstArgActions() {
        return Objects.requireNonNull(denyColonInFirstArgActions, "denyColonInFirstArgActions");
    }

    public Collection<BukkitChain> chains() {
        return Objects.requireNonNull(chains, "chains");
    }

}

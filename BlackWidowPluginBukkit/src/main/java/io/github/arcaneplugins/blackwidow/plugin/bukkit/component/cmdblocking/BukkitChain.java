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

import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Chain;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.EvalCause;
import io.github.arcaneplugins.blackwidow.lib.cmdblocking.Policy;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Action;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.Requirement;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public final class BukkitChain extends Chain {

    private final BlackWidow plugin;
    private final Collection<Requirement> requirements;
    private final Collection<Action> actions;

    /**
     * Constructs a {@code BukkitChain}.
     *
     * @param id           {@inheritDoc}
     * @param enabled      {@inheritDoc}
     * @param policy       {@inheritDoc}
     * @param rules        {@inheritDoc}
     * @param isRegex      {@inheritDoc}
     * @param requirements Requirements (on top of a matching rule) for this chain to be evaluated.
     * @param actions      What should be done if the chain is matched against.
     * @author lokka30
     * @since 1.0.0
     */
    public BukkitChain(
        final BlackWidow plugin,
        final String id,
        final boolean enabled,
        final Policy policy,
        final Collection<String> rules,
        final boolean isRegex,
        final Collection<EvalCause> causeFilters,
        final Collection<Requirement> requirements,
        final Collection<Action> actions
    ) {
        super(id, enabled, policy, rules, isRegex, causeFilters);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.requirements = Objects.requireNonNull(requirements, "requirements");
        this.actions = Objects.requireNonNull(actions, "actions");
    }

    public BukkitChain(
        final BlackWidow plugin,
        final CommentedConfigurationNode node
    ) {
        this(
            Objects.requireNonNull(plugin, "plugin"),
            node.node("id").getString(),
            node.node("enabled").getBoolean(true),
            parsePolicyAtNode(node.node("policy")),
            parseStringListAtNode(node.node("rules")),
            node.node("regex").getBoolean(false),
            parseEvalCauseListAtNode(node.node("cause-filters")),
            plugin.logicManager().parseRequirementsInChildrenOfNode(node.node("requirements")),
            plugin.logicManager().parseActionsInChildrenOfNode(node.node("actions"))
        );
    }

    @SuppressWarnings("unused")
    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

    private static Policy parsePolicyAtNode(
        final CommentedConfigurationNode node
    ) {
        try {
            return node.get(Policy.class, Policy.DENY);
        } catch (final SerializationException ex) {
            throw new RuntimeException("Unable to parse policy at node '" + node.path() + "'", ex);
        }
    }

    private static List<String> parseStringListAtNode(
        final CommentedConfigurationNode node
    ) {
        try {
            return node.getList(String.class);
        } catch (final SerializationException ex) {
            throw new RuntimeException("Unable to parse string list at node '" + node.path() + "'", ex);
        }
    }

    public Collection<Requirement> requirements() {
        return requirements;
    }

    public Collection<Action> actions() {
        return actions;
    }

    public static Collection<EvalCause> parseEvalCauseListAtNode(
        final CommentedConfigurationNode node
    ) {

        Objects.requireNonNull(node, "node");

        try {
            if (node.virtual()) {
                return EnumSet.allOf(EvalCause.class);
            }

            return node.getList(EvalCause.class);
        } catch (final SerializationException ex) {
            throw new RuntimeException("Unable to parse eval cause list at node '" + node.path() + "'", ex);
        }
    }
}

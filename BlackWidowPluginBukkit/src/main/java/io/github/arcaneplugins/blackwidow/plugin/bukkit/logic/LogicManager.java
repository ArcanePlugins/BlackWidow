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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.event.LoadActionsReadyEvent;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.event.LoadRequirementsReadyEvent;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.inbuilt.action.SendMessageAction;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.inbuilt.requirement.HasPermissionRequirement;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.inbuilt.requirement.InWorldRequirement;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

//TODO Javadoc
public final class LogicManager {

    private final Map<String, Function<CommentedConfigurationNode, Action>> actParsers = new HashMap<>();
    private final Map<String, Function<CommentedConfigurationNode, Requirement>> reqParsers = new HashMap<>();

    private final BlackWidow plugin;

    //TODO Javadoc
    public LogicManager(
        final BlackWidow plugin
    ) {
        this.plugin = plugin;
    }

    //TODO Javadoc
    public void load() {
        plugin().getLogger().info("Loading logic.");
        loadStdRequirements();
        loadThirdPartyRequirements();
        loadStdActions();
        loadThirdPartyActions();
    }

    //TODO Javadoc
    private void loadStdRequirements() {
        requirementParsers().put(HasPermissionRequirement.ID, (n) -> new HasPermissionRequirement(plugin(), n));
        requirementParsers().put(InWorldRequirement.ID, (n) -> new InWorldRequirement(plugin(), n));
    }

    //TODO Javadoc
    private void loadThirdPartyRequirements() {
        final LoadRequirementsReadyEvent event = new LoadRequirementsReadyEvent(
            plugin(),
            requirementParsers()
        );
        plugin().getServer().getPluginManager().callEvent(event);
    }

    //TODO Javadoc
    private void loadStdActions() {
        actionParsers().put(SendMessageAction.ID, (n) -> new SendMessageAction(plugin(), n));
    }

    //TODO Javadoc
    private void loadThirdPartyActions() {
        final LoadActionsReadyEvent event = new LoadActionsReadyEvent(
            plugin(),
            actionParsers()
        );
        plugin().getServer().getPluginManager().callEvent(event);
    }

    //TODO Javadoc
    public Action parseActionAtNode(
        final CommentedConfigurationNode node
    ) {
        final String id = Objects.requireNonNull(
            node.node("id").getString(),
            "Action node at path '" + node.path() + "' must define an ID with 'id'"
        );

        try {
            return Objects.requireNonNull(
                actionParsers().get(id),
                "Action node at path '" + node.path() + "' must define the ID ('id') of an Action that" +
                    "exists (did you make a typo?), but got '" + id + "'"
            ).apply(node);
        } catch (final Exception ex) {
            throw new RuntimeException(
                "Unable to parse Action with ID '" + id + "' at node path '" + node.path() + "': " +
                    ex.getLocalizedMessage(),
                ex
            );
        }
    }

    //TODO Javadoc
    public Collection<Action> parseActionsInChildrenOfNode(
        final CommentedConfigurationNode nodes
    ) {
        final Collection<Action> actions = new LinkedHashSet<>();
        for (final CommentedConfigurationNode node : nodes.childrenList()) {
            actions.add(parseActionAtNode(node));
        }
        return actions;
    }

    //TODO Javadoc
    public Requirement parseRequirementAtNode(
        final CommentedConfigurationNode node
    ) {
        final String id = Objects.requireNonNull(
            node.node("id").getString(),
            "Requirement node at path '" + node.path() + "' must define an ID with 'id'"
        );

        try {
            return Objects.requireNonNull(
                requirementParsers().get(id),
                "Requirement node at path '" + node.path() + "' must define the ID ('id') of a Requirement that" +
                    "exists (did you make a typo?), but got '" + id + "'"
            ).apply(node);
        } catch (final Exception ex) {
            throw new RuntimeException(
                "Unable to parse Requirement with ID '" + id + "' at node path '" + node.path() + "': " +
                    ex.getLocalizedMessage(),
                ex
            );
        }
    }

    //TODO Javadoc
    public Collection<Requirement> parseRequirementsInChildrenOfNode(
        final CommentedConfigurationNode nodes
    ) {
        final Collection<Requirement> requirements = new LinkedHashSet<>();
        for (final CommentedConfigurationNode node : nodes.childrenList()) {
            requirements.add(parseRequirementAtNode(node));
        }
        return requirements;
    }

    //TODO Javadoc
    private BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

    //TODO Javadoc
    public Map<String, Function<CommentedConfigurationNode, Action>> actionParsers() {
        return actParsers;
    }

    //TODO Javadoc
    public Map<String, Function<CommentedConfigurationNode, Requirement>> requirementParsers() {
        return reqParsers;
    }
}

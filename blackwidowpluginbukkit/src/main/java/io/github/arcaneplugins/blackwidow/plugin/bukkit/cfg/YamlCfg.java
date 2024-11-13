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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public abstract class YamlCfg {

    private final BlackWidow plugin;
    private final Path filePath;
    private final String filePathStr;
    private final String fileName;
    private final String description;
    private final YamlConfigurationLoader loader;
    private final int latestFileVersion;
    private CommentedConfigurationNode root;

    public YamlCfg(
        final BlackWidow plugin,
        final String filePathStr,
        final String fileName,
        final String description,
        final int latestFileVersion
    ) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.filePathStr = Objects.requireNonNull(filePathStr, "filePathStr");
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        this.description = Objects.requireNonNull(description, "description");
        this.latestFileVersion = latestFileVersion;

        if (filePathStr().isBlank()) {
            throw new IllegalArgumentException("filePath can't be blank");
        }

        this.filePath = Path.of(plugin.getDataFolder().toString(), filePathStr());

        if (fileName().isBlank()) {
            throw new IllegalArgumentException("fileName can't be blank");
        }

        if (description().isBlank()) {
            throw new IllegalArgumentException("description can't be blank");
        }

        this.loader = YamlConfigurationLoader.builder()
            .path(filePath())
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .build();
    }

    public final void load() {
        try {
            copyFromResourcesIfDoesntExist();
            setRoot(yamlLoader().load());

            // upgrading
            int installedVer = installedFileVersion();
            final int latestVer = latestFileVersion();
            if (installedVer <= 0) {
                throw new IllegalArgumentException("Config '" + fileName() + "' has an installed file version below " +
                    "or equal to 0, indicating it has almost certainly been manually tampered with. Please address " +
                    "this issue ASAP, as a manually-adjusted file version can cause instability.");
            }
            if (installedVer < latestVer) {
                int lastVersion = installedFileVersion();
                plugin().getLogger().info("Config '" + fileName() + "' is outdated, automatically upgrading.");
                while (installedVer < latestFileVersion()) {
                    plugin().getLogger().info("Upgrading '" + fileName() + "' from v" +
                        installedVer + " to v" + (installedVer + 1) + ".");
                    upgradeFile();
                    write();
                    installedVer = installedFileVersion();
                    if (installedVer == lastVersion){
                        // prevent potential endless loop
                        plugin().getLogger().warning("There was an error upgrading the file version, " +
                                "the version number was not incremented");
                        break;
                    }
                    lastVersion = installedVer;
                }
            } else if (installedVer > latestVer) {
                plugin().getLogger().warning("Config '" + fileName() + "' apparently has version '" + installedVer +
                    "' but the latest is '" + latestVer + "'. Was the file version manually tampered with, or used " +
                    "on a newer version of the plugin? Please address ASAP as this may cause instability.");
            }

            // lastly, set 'context' metadata if not already present
            root().node("do-not-touch", "version", "context").act(node -> {
                if (!node.getString("").isBlank()) {
                    return;
                }

                //noinspection deprecation,UnstableApiUsage
                final String version = plugin().usePaperFeatures() ?
                    plugin().getPluginMeta().getVersion() :
                    plugin().getDescription().getVersion();

                node.set(plugin().getName() + " " + version);

                write();
            });

            loadMore();
        } catch (final Exception ex) {
            throw new RuntimeException("Unable to load '" + fileName() + "': " + ex.getLocalizedMessage(), ex);
        }
    }

    protected abstract void loadMore();

    public final void write() {
        try {
            yamlLoader().save(root());
        } catch (final ConfigurateException ex) {
            throw new RuntimeException("Unable to save '" + fileName() + "'", ex);
        }
    }

    public final void copyFromResourcesIfDoesntExist() {
        if (Files.exists(filePath())) {
            return;
        }

        plugin().saveResource(filePathStr(), false);
    }

    protected final BlackWidow plugin() {
        return Objects.requireNonNull(plugin, "plugin");
    }

    public final String fileName() {
        return Objects.requireNonNull(fileName, "fileName");
    }

    public final String description() {
        return Objects.requireNonNull(description, "description");
    }

    public final Path filePath() {
        return Objects.requireNonNull(filePath, "filePath");
    }

    public final YamlConfigurationLoader yamlLoader() {
        return Objects.requireNonNull(loader, "loader");
    }

    public final CommentedConfigurationNode root() {
        return root;
    }

    protected final void setRoot(CommentedConfigurationNode root) {
        this.root = root;
    }

    public final String filePathStr() {
        return filePathStr;
    }

    public final int latestFileVersion() {
        return this.latestFileVersion;
    }

    public final int installedFileVersion() {
        final int installedVer = root().node("do-not-touch", "version", "installed").getInt(Integer.MIN_VALUE);
        if (installedVer == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Installed file version must be specified in file '" + fileName() + "'");
        }
        return installedVer;
    }

    //Note that no need to call 'write()' as this is done automatically after each upgrade by the loader
    public abstract void upgradeFile();

}

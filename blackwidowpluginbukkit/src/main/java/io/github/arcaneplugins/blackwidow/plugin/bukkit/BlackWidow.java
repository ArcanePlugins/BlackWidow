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

package io.github.arcaneplugins.blackwidow.plugin.bukkit;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.YamlCfg;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.settings.Settings;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.translations.Translations;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.command.CommandManager;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.component.cmdblocking.CmdBlocker;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.listener.ListenerManager;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.LogicManager;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.logic.BukkitVersionChecker;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.ClassUtil;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.DebugCategory;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.ExceptionUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * BlackWidow Bukkit Plugin main class.
 * {@link BlackWidow#onEnable()} is ran on startup by the {@link org.bukkit.plugin.PluginManager Bukkit PluginManager}.
 *
 * @author lokka30
 * @since 1.0.0
 */
public final class BlackWidow extends JavaPlugin {

    private final ListenerManager listenerManager = new ListenerManager(this);
    private final CommandManager commandManager = new CommandManager(this);
    private final CmdBlocker cmdBlocker = new CmdBlocker(this);
    private final Settings settings = new Settings(this);
    private final Translations translations = new Translations(this);
    private final LogicManager logicManager = new LogicManager(this);
    private final EnumSet<DebugCategory> debugCategories = EnumSet.noneOf(DebugCategory.class);
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    public final BukkitVersionChecker bukkitVersionChecker = new BukkitVersionChecker(this);

    private boolean usingPaper = false;
    private BukkitAudiences adventure = null;

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public void onLoad() {
        final long startTime = System.currentTimeMillis();

        try {
            commandManager().init();
        } catch (Exception ex) {
            ExceptionUtil.logException(this, ex, "An error occurred whilst initializing BlackWidow (Bukkit-onLoad).");
            return;
        }

        final long duration = System.currentTimeMillis() - startTime;
        getLogger().info("Plugin initialised (took %.3fs).".formatted(duration / 1_000f));
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();

        try {
            loadCompat();
            this.adventure = BukkitAudiences.create(this);
            loadConfigs();
            logicManager().load();
            loadComponents();
            listenerManager().load();
            commandManager().load();
            bukkitVersionChecker.load(true);
        } catch (Exception ex) {
            ExceptionUtil.logException(this, ex, "An error occurred whilst enabling BlackWidow.");
            return;
        }

        final long duration = System.currentTimeMillis() - startTime;
        getLogger().info("Plugin enabled (took %.3fs).".formatted(duration / 1_000f));
    }

    /**
     * {@inheritDoc}
     *
     * @author lokka30
     * @since 1.0.0
     */
    @Override
    public void onDisable() {
        final long startTime = System.currentTimeMillis();

        try {
            commandManager().disable();

            if (adventure != null) {
                adventure.close();
                adventure = null;
            }
        } catch (Exception ex) {
            ExceptionUtil.logException(this, ex, "An error occurred whilst disabling BlackWidow.");
            return;
        }

        final long duration = System.currentTimeMillis() - startTime;
        getLogger().info("Plugin disabled (took %.3fs).".formatted(duration / 1_000f));
        getLogger().info("Thank you for using BlackWidow");
    }

    /**
     * Used to 'soft'-reload the plugin, i.e., reload configs and related configurable logic, without having to
     * restart the plugin or the server.
     *
     * @author lokka30
     * @since 1.0.0
     */
    public void softReload() {
        getLogger().info("Performing soft-reload.");
        final long startTime = System.currentTimeMillis();

        try {
            loadCompat();
            loadConfigs();
            logicManager().load();
            loadComponents();
            bukkitVersionChecker.load(false);
        } catch (Exception ex) {
            ExceptionUtil.logException(this, ex, "An error occurred whilst performing a soft-reload.");
            return;
        }

        final long duration = System.currentTimeMillis() - startTime;
        getLogger().info("Soft-reloaded (took %.3fs).".formatted(duration / 1_000f));
    }

    /**
     * Load compatibility checking tools.
     *
     * @author lokka30
     * @since 1.0.0
     */
    private void loadCompat() {
        getLogger().info("Loading compatibility.");
        setUsingPaper(ClassUtil.classExists("io.papermc.paper.ServerBuildInfo"));

        if (!usingPaper()) {
            getLogger().info("Server is not using PaperMC (or fork); Paper features are disabled.");
        }
    }

    /**
     * Load the plugin's configuration.
     *
     * @author lokka30
     * @since 1.0.0
     */
    private void loadConfigs() {
        getLogger().info("Loading configs.");
        List.of(settings(), translations()).forEach(YamlCfg::load);
    }

    /**
     * Load the major components of the plugin.
     *
     * @author lokka30
     * @since 1.0.0
     */
    private void loadComponents() {
        getLogger().info("Loading components.");
        cmdBlocker().load();
    }

    /**
     * Send a debug log if the debug category {@code cat} is enabled.
     * <p>
     * The supplier is used to avoid needlessly operating on the debug log message itself (e.g., fetching required data,
     * concatenating strings, etc.) unless it's actually needed.
     * </p>
     *
     * @param cat     Debug category associated with the message.
     * @param strSupp Supplier of the message that can be sent.
     * @author lokka30
     * @since 1.0.0
     */
    public void debugLog(
        final DebugCategory cat,
        final Supplier<String> strSupp
    ) {
        if (!enabledDebugCategories().contains(cat)) {
            return;
        }

        getLogger().info("[DEBUG - " + cat.name() + "]: " + strSupp.get());
    }

    /**
     * @return Whether the server is believed to be using PaperMC or any derivative of.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean usingPaper() {
        return usingPaper;
    }

    /**
     * Set whether the server is believed to be using PaperMC or any derivative of.
     *
     * @param val New value.
     * @author lokka30
     * @since 1.0.0
     */
    private void setUsingPaper(final boolean val) {
        this.usingPaper = val;
    }

    /**
     * @return An instance of the {@link ListenerManager}.
     * @author lokka30
     * @since 1.0.0
     */
    public ListenerManager listenerManager() {
        return Objects.requireNonNull(listenerManager, "listenerManager");
    }

    /**
     * @return An instance of the {@link CommandManager}.
     * @author lokka30
     * @since 1.0.0
     */
    public CommandManager commandManager() {
        return Objects.requireNonNull(commandManager, "commandManager");
    }

    /**
     * @return An instance of the {@link CmdBlocker}.
     * @author lokka30
     * @since 1.0.0
     */
    public CmdBlocker cmdBlocker() {
        return Objects.requireNonNull(cmdBlocker, "cmdBlocker");
    }

    /**
     * @return Whether BlackWidow should enable using features only available for PaperMC or derivatives.
     * @author lokka30
     * @since 1.0.0
     */
    public boolean usePaperFeatures() {
        //TODO temporarily disable ALL paper-specific features. get it working on bukkit first :)
        return false;
    }

    /**
     * @return An instance of the {@link Settings} configuration.
     * @author lokka30
     * @since 1.0.0
     */
    public Settings settings() {
        return Objects.requireNonNull(settings, "settings");
    }

    /**
     * @return An instance of the {@link LogicManager}.
     * @author lokka30
     * @since 1.0.0
     */
    public LogicManager logicManager() {
        return Objects.requireNonNull(logicManager, "logicManager");
    }

    /**
     * @return An {@link EnumSet enum set} of {@link DebugCategory debug categories} which were enabled by the user.
     * @author lokka30
     * @since 1.0.0
     */
    public EnumSet<DebugCategory> enabledDebugCategories() {
        return Objects.requireNonNull(debugCategories, "debugCategories");
    }

    /**
     * @return An instance of the {@link Translations} configuration.
     * @author lokka30
     * @since 1.0.0
     */
    public Translations translations() {
        return Objects.requireNonNull(translations, "translations");
    }

    public BukkitAudiences adventure() {
        return Objects.requireNonNull(adventure, "adventure");
    }

    public MiniMessage miniMessage() {
        return Objects.requireNonNull(miniMessage, "miniMessage");
    }
}

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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.cfg.translations;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.ConfigurateException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

//TODO Javadocs
public enum Translation {

    COMMAND_BLACKWIDOW_RELOAD_STARTED(
        new String[]{"command", "blackwidow", "reload", "started"},
        true,
        List.of("%prefix%Reloading BlackWidow.")
    ),

    COMMAND_BLACKWIDOW_RELOAD_FAILED(
        new String[]{"command", "blackwidow", "reload", "failed"},
        true,
        List.of("%prefix%Reload failed! Please see your server console for more details.")
    ),

    COMMAND_BLACKWIDOW_RELOAD_COMPLETE(
        new String[]{"command", "blackwidow", "reload", "complete"},
        true,
        List.of("%prefix%Reload complete.")
    ),

    COMMAND_BLACKWIDOW_VERSION(
        new String[]{"command", "blackwidow", "version"},
        true,
        List.of("<color:dark_gray><st>*---*</st></color> <color:red><insert:/bw><hover:show_text:'<color:red><bold>BlackWidow</bold></color><newline><color:gray>A security solution for Minecraft.</color>'><bold>BlackWidow</bold> v%version%</hover></insert></color> <color:dark_gray><st>*---*</st></color><newline><color:gray>A security solution for Minecraft.</color><newline><color:gray>From <i><click:open_url:https://github.com/arcaneplugins>ArcanePlugins</click></i> by <color:red>%authors%</color>.</color>")
    ),

    PREFIX(
        new String[]{"prefix"},
        false,
        "<color:red><bold><insert:/bw><hover:show_text:'<color:red><bold>BlackWidow</bold></color>" +
            "<newline><color:gray>A security solution for Minecraft.</color>'>BW: </hover></insert></bold></color> " +
            "<color:gray>"
    ),

    LIST_DELIMITER(
        new String[]{"list-delimeter"},
        false,
        "<color:gray>, </color:gray>"
    );

    private final String[] nodePath;
    private final boolean isList;
    private final Object defValue;

    //TODO Javadocs
    Translation(
        final String[] nodePath,
        final boolean isList,
        final Object defValue
    ) {
        this.nodePath = nodePath;
        this.isList = isList;
        this.defValue = defValue;
    }

    //TODO Javadocs
    public final String[] nodePath() {
        return nodePath;
    }

    //TODO Javadocs
    public final boolean isList() {
        return isList;
    }

    //TODO Javadocs
    public final Object defValue() {
        return defValue;
    }

    //TODO Javadocs
    public String strSingle(
        final BlackWidow plugin
    ) {
        if (isList()) {
            throw new IllegalStateException("Translation is list type, but called `str` instead of `strList`");
        }

        return plugin.translations().root().node((Object[]) nodePath()).getString((String) defValue());
    }

    //TODO Javadocs
    public static String placeholerify(
        final BlackWidow plugin,
        final String msg,
        final Map<String, Supplier<String>> placeholders
    ) {
        // temp var to hold latest placeholderified version of `msg`
        String formattedMsg = msg;

        // do placeholder replacement
        for (final Map.Entry<String, Supplier<String>> placeholder : placeholders.entrySet()) {
            final String id = "%" + placeholder.getKey() + "%";
            final Supplier<String> val = placeholder.getValue();

            if (!formattedMsg.contains(id)) {
                continue;
            }

            formattedMsg = formattedMsg.replace(id, val.get());
        }

        // other standard placeholders
        formattedMsg = formattedMsg.replace("%prefix%", Translation.PREFIX.strSingle(plugin));

        return formattedMsg;
    }

    //TODO Javadocs
    public static String joinSeparatedStrings(
        final BlackWidow plugin,
        final Collection<String> strings
    ) {
        return String.join(
            LIST_DELIMITER.strSingle(plugin),
            strings
        );
    }

    //TODO Javadocs
    public static Component formatify(
        final BlackWidow plugin,
        final String msg,
        final Map<String, Supplier<String>> placeholders
    ) {
        return plugin.miniMessage().deserialize(
            placeholerify(
                plugin,
                msg,
                placeholders
            )
        );
    }

    //TODO Javadocs
    public final List<String> strList(
        final BlackWidow plugin
    ) {
        if (!isList()) {
            throw new IllegalStateException("Translation is not list type, but called `strList` instead of `str`");
        }

        try {
            //noinspection unchecked
            return Objects.requireNonNullElse(
                plugin.translations().root().node((Object[]) nodePath()).getList(String.class),
                (List<String>) defValue()
            );
        } catch (final ConfigurateException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    //TODO Javadocs
    public final void sendTo(
        final BlackWidow plugin,
        final Audience audience,
        final Map<String, Supplier<String>> placeholders
    ) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(audience, "audience");

        if (isList()) {
            for (final String msg : strList(plugin)) {
                audience.sendMessage(formatify(plugin, msg, placeholders));
            }
        } else {
            audience.sendMessage(formatify(plugin, strSingle(plugin), placeholders));
        }
    }

    //TODO Javadocs
    public final void sendTo(
        final BlackWidow plugin,
        final CommandSender sender,
        final Map<String, Supplier<String>> placeholders
    ) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(sender, "sender");

        //noinspection resource
        sendTo(plugin, plugin.adventure().sender(sender), placeholders);
    }

    //TODO Javadocs
    @SuppressWarnings("unused")
    public final void sendTo(
        final BlackWidow plugin,
        final Player player,
        final Map<String, Supplier<String>> placeholders
    ) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(placeholders, "placeholders");

        //noinspection resource
        sendTo(plugin, plugin.adventure().player(player), placeholders);
    }

}

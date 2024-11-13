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

package io.github.arcaneplugins.blackwidow.plugin.bukkit.util;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.BlackWidow;

import java.util.logging.Logger;

public class ExceptionUtil {

    private ExceptionUtil() {
        throw new IllegalStateException("Can't instantiate utility class");
    }

    public static void logException(
            final BlackWidow plugin,
            final Exception ex,
            final String msg
    ) {
        final Logger logger = plugin.getLogger();

        final String bigdiv = '+' + "-".repeat(43) + '+';
        final String minidiv = "=----->";

        logger.severe(bigdiv);
        logger.severe("");
        logger.severe(minidiv + "Warning: Please Read Below Carefully");
        logger.severe("\tBlackWidow has detected an issue, please carefully read the below and attempt to " +
                "resolve the issue (or otherwise, report it to the maintainers via the details also provided below).");
        logger.severe("");
        logger.severe("\tSometimes, these are simple errors caused when users misconfigure the plugin, other " +
                "times, it can be a bug (issue) with the plugin's code which the maintainers might not know about.");
        logger.severe("");
        logger.severe(bigdiv);
        logger.severe("");
        logger.severe(minidiv + "Details about this Issue");
        logger.severe("Context (if known):");
        logger.severe('\t' + msg);
        logger.severe("");
        logger.severe("Localized Message:");
        logger.severe('\t' + ex.getLocalizedMessage());
        logger.severe("");
        logger.severe(bigdiv);
        logger.severe("");
        logger.severe(minidiv + "Contacting Maintainers for Assistance");
        logger.severe("If this is an issue you can't fix, or if it is a bug report, please contact the " +
                "maintainers by checking for recommended links in the GitHub repository description.");
        logger.severe("Please visit: < https://github.com/ArcanePlugins/BlackWidow/ >");
        logger.severe("");
        logger.severe("Our Discord Server (should be linked within the page above) is usually the best avenue " +
                "for users to report issues like these.");
        logger.severe("");
        logger.severe("It's usually worth checking the Frequently Asked Questions page (if applicable).");
        logger.severe("");
        logger.severe(bigdiv);
        logger.severe("");
        logger.severe(minidiv + "Java Exception Details (for Developers)");
        logger.severe("These details may be difficult to understand for those without Java experience.");
        logger.severe("Regardless, it may contain useful details or expressions hinting to the root problem.");
        logger.severe("");
        logger.severe("Exception Class:");
        logger.severe('\t' + ex.getClass().getSimpleName());
        logger.severe("");
        logger.severe("Exception Stack Trace:");
        //noinspection CallToPrintStackTrace
        ex.printStackTrace();
        logger.severe("");
        logger.severe(bigdiv);
        logger.severe("");
        logger.severe(minidiv + "ATTENTION Server Administrators:");
        logger.severe("Just saw noticed the bottom of this log in your console? Don't panic - please scroll " +
                "up to the top of this big log message and read carefully.");
        logger.severe("");
        logger.severe(bigdiv);
    }
}

package io.github.arcaneplugins.blackwidow.plugin.bukkit.logic;

import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.ClassUtil;
import io.github.arcaneplugins.blackwidow.plugin.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.InvalidObjectException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A custom implementation for comparing program versions
 *
 * @author stumper66
 * @since 1.1.0
 */
public final class VersionInfo implements Comparable<VersionInfo> {

    public VersionInfo(
            @NotNull final String verStr
    ) throws InvalidObjectException {
        Objects.requireNonNull(verStr, "verStr");

        this.verStr = verStr;

        for (final String numTemp : verStr.split("\\.")) {
            if (!StringUtil.isDouble(numTemp)) {
                throw new InvalidObjectException("Version can only contain numbers and periods");
            }
            final int intD = Integer.parseInt(numTemp);
            verSplit().add(intD);
        }

        for (int i = 4; i < verSplit().size(); i++) {
            verSplit().add(0);
        }
    }

    private final String verStr;
    private final List<Integer> verSplit = new LinkedList<>();

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof VersionInfo)) {
            return false;
        }

        return this.verStr.equals(((VersionInfo) o).verStr());
    }

    @Override
    @NotNull
    public String toString() {
        return verStr();
    }

    @NotNull
    private String verStr() {
        return Objects.requireNonNull(verStr, "verStr");
    }

    @Override
    public int compareTo(
            @NotNull final VersionInfo other
    ) {
        for (int i = 0; i < 4; i++) {

            if (other.verSplit().size() <= i && this.verSplit().size() - 1 <= i) {
                break;
            }

            // if one has extra digits we'll assume that one is newer
            else if (other.verSplit().size() <= i) {
                return 1;
            } else if (verSplit().size() <= i) {
                return -1;
            }

            final int compareInt = other.verSplit().get(i);
            final int thisInt = this.verSplit().get(i);

            if (thisInt > compareInt) {
                return 1;
            } else if (thisInt < compareInt) {
                return -1;
            }
        }

        return 0;
    }

    private List<Integer> verSplit() {
        return Objects.requireNonNull(verSplit, "verSplit");
    }
}
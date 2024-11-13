package io.github.arcaneplugins.blackwidow.lib.cmdblocking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * @author stumper66
 * @since 1.1.0
 */
public class UpdateChecker {

    public UpdateChecker(
            final String resourceName
    ) {
        this.resourceName = resourceName;
    }

    private final String resourceName;
    private String errorMessage = null;

    public boolean success() {
        return errorMessage != null;
    }

    @Nullable
    public String errorMessage() {
        return errorMessage;
    }

    // returns if successful or not
    public boolean checkLatestVersion(
            final @NotNull Consumer<String> consumer
    ) {
        try (InputStream stream = new URI(
                "https://hangar.papermc.io/api/v1/projects/" +
                        resourceName + "/latest?channel=Release")
                .toURL().openStream()
        ) {
            final Scanner scanner = new Scanner(stream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
            }
            return true;
        } catch (FileNotFoundException e) {
            errorMessage = "Error checking for latest version, file not found: " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "Error checking for latest version. " + e.getMessage();
        }

        return false;
    }
}

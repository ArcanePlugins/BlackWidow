package io.github.arcaneplugins.blackwidow.lib.cmdblocking;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    public UpdateChecker(String resourceName) {
        this.resourceName = resourceName;
    }

    private final String resourceName;
    private String errorMessage;

    public boolean hadError() {
        return errorMessage != null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean getLatestVersion(Consumer<String> consumer) {

        try (InputStream inputStream = new URI(
                "https://hangar.papermc.io/api/v1/projects/" +
                        resourceName + "/latest?channel=Release")
                .toURL().openStream()) {

            final Scanner scanner = new Scanner(inputStream);
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

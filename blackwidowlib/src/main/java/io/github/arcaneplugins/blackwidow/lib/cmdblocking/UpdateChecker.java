package io.github.arcaneplugins.blackwidow.lib.cmdblocking;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
    public static void getLatestVersion(final String resourceName, final Consumer<String> consumer)
            throws IOException, URISyntaxException {
        try (InputStream inputStream = new URI(
                "https://hangar.papermc.io/api/v1/projects/" +
                        resourceName + "/latest?channel=Release")
                    .toURL().openStream()){

            final Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()){
                consumer.accept(scanner.next());
            }
        }
    }
}

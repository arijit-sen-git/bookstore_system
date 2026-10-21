package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class DatabaseConfig {
    public static final String URL;
    public static final String USER;
    public static final String PASSWORD;

    static {
        Map<String, String> values = new HashMap<>();
        try {
            for (String line : Files.readAllLines(Path.of(".env"))) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) continue;
                int separator = line.indexOf('=');
                values.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Unable to read .env: " + e.getMessage());
        }
        URL = required(values, "DB_URL");
        USER = required(values, "DB_USER");
        PASSWORD = required(values, "DB_PASSWORD");
    }

    private static String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.isEmpty()) throw new ExceptionInInitializerError("Missing " + key + " in .env");
        return value;
    }

    private DatabaseConfig() {}
}

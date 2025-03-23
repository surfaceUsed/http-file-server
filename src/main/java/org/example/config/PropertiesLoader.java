package org.example.config;

import org.example.error.ServerConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A utility class responsible for loading and accessing properties from the application.properties file. The properties
 * are loaded once when the class is initialized and can be retrieved using a key-based search.
 * This class follows a static initialization approach, ensuring that the properties are loaded only once during the
 * application's lifetime.
 */
final class PropertiesLoader {

    private static final Properties PROPERTIES = new Properties();

    private PropertiesLoader() {}

    // Static initialization block to load properties when the class is first accessed.
    static {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new ServerConfigurationException("Failed to load server properties: " + e.getMessage());
        }
    }

    /**
     * Loads the application.properties file from the classpath and stores its key-value pairs in a Properties object.
     *
     * @throws IOException if the properties file cannot be read.
     */
    private static void loadProperties() throws IOException {

        try (InputStream input = PropertiesLoader.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            PROPERTIES.load(input);
        }
    }

    /**
     * Retrieves the value of a property by its key.
     *
     * @param key the name of the property to retrieve.
     * @return the value associated with the specified key, or null if the key is not found.
     */
    static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}

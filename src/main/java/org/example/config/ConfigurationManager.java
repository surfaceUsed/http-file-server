package org.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.error.ServerConfigurationException;
import org.example.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * The ConfigurationManager class is responsible for managing the configuration settings and system resources related
 * to the server. This includes loading URL structures, managing file system configurations, and ensuring the server
 * properties are accessed globally.
 *
 * This class follows the Singleton pattern and ensures that there is only one instance of the ConfigurationManager '
 * throughout the lifecycle of the application.
 */
public class ConfigurationManager {

    private static final ConfigurationManager MANAGER = new ConfigurationManager(); // Eager init.
    private static final ServerProperties SERVER_PROPERTIES;
    private Map<String, Map<String, List<String>>> urlStructures;
    private FileSystem fileSystem;

    private ConfigurationManager() {}

    // Static block to initialize server properties
    static {
        SERVER_PROPERTIES = ServerProperties.getInstance();
    }

    /**
     * Returns the singleton instance of the ConfigurationManager.
     */
    public static ConfigurationManager getInstance() {
        return MANAGER;
    }

    /**
     * Returns the server properties loaded from the configuration.
     */
    public ServerProperties getServerProperties() {
        return SERVER_PROPERTIES;
    }

    /**
     * Lazy initialization of the FileSystem.
     * If the file system is not yet initialized, it loads the necessary metadata
     * and file system from the given configuration paths.
     *
     *    1. FileMetadataTracker - tracks metadata related to files in the system.
     *    2. FileSystem - responsible for managing files on the server.
     *
     * @return FileSystem instance.
     */
    public FileSystem getFileSystem() {
        if (fileSystem == null) {
            FileMetadataTracker dataTracker = FileMetadataTracker.loadDataTracker(
                    SERVER_PROPERTIES.getFileMetadataPath(),
                    SERVER_PROPERTIES.getMetadataCurrentIdFieldKey(),
                    SERVER_PROPERTIES.getMetadataDataFieldKey());
            fileSystem = LocalFileSystem.loadFileSystem(
                    SERVER_PROPERTIES.getFileSystemPath(),
                    dataTracker);
        }
        return fileSystem;
    }

    /**
     * Returns the URL structure patterns, loading them if they are not already loaded.
     */
    public Map<String, Map<String, List<String>>> getUrlStructurePatterns() {
        if (urlStructures == null) {
            urlStructures = loadUrlStructures();
        }
        return urlStructures;
    }

    /**
     * Loads the URL structures from a JSON configuration file ('resources/url_structures.json').
     *
     * The nested Map<String, Map<String, List<String>>> stores generic URL patterns for various HTTP methods (GET,
     * POST, PUT, DELETE) associated with a specific endpoint.
     *
     * @return Map of generic URL structure patterns.
     * @throws ServerConfigurationException If loading the URL structures fails.
     */
    private Map<String, Map<String, List<String>>> loadUrlStructures() {
        try {
            JsonDataLoader jsonLoader = new JsonDataLoader(SERVER_PROPERTIES.getUrlStructuresPath());
            return JsonUtil.parseJsonToMap(jsonLoader.getNode(),
                    new TypeReference<Map<String, Map<String, List<String>>>>() {});
        } catch (JsonProcessingException e) {
            throw new ServerConfigurationException("Failed to load server URL paths from file " +
                    "'url_structures.json': " + e.getMessage());
        }
    }
}

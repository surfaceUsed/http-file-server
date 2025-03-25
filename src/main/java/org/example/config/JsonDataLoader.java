package org.example.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.error.ServerConfigurationException;
import org.example.util.FileUtil;
import org.example.util.JsonUtil;
import java.io.IOException;

/**
 * A utility class responsible for loading and parsing JSON data from a file. This class reads a JSON file from the
 * specified path and provides methods to access its contents in the form of a JsonNode.
 */
class JsonDataLoader {

    private final JsonNode node;

    /**
     * Constructs a JsonDataLoader instance by reading and parsing the JSON content from the specified file path.
     *
     * @param path the file path of the JSON file to load.
     * @throws ServerConfigurationException if the file cannot be read or parsed.
     */
    JsonDataLoader(String path) {
        try {
            this.node = JsonUtil.getAsJsonNode(FileUtil.getFileAsString(path));
        } catch (IOException e) {
            throw new ServerConfigurationException("Failed to load JSON file '" + path + "': " + e.getMessage());
        }
    }

    /**
     * Retrieves a specific field from the loaded JSON data.
     *
     * @param fieldName the name of the JSON field to retrieve.
     * @return the JsonNode representing the specified field.
     * @throws ServerConfigurationException if the field is not found in the JSON data.
     */
    JsonNode getFieldNode(String fieldName) {
        JsonNode field = this.node.get(fieldName);
        if (field == null) {
            throw new ServerConfigurationException("Field '" + fieldName + "' is not found in JSON.");
        }
        return field;
    }

    /**
     * Retrieves the root of the loaded JSON data.
     *
     * @return the root JsonNode.
     */
    JsonNode getNode() {
        return node;
    }
}

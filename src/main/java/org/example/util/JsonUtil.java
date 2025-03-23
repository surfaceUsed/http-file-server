package org.example.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.enums.LoggerType;
import org.example.logs.LogHandler;
import org.example.model.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The JsonUtil class provides utility methods for working with JSON data using the Jackson library. It includes methods
 * for converting objects to JSON, parsing JSON strings into Java objects, and handling serialization and
 * deserialization with custom configurations.
 */
public final class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = getCustomMapper();

    private JsonUtil() {}

    /**
     * Creates and configures an instance of ObjectMapper with custom settings.
     *
     * @return A configured ObjectMapper instance.
     */
    private static ObjectMapper getCustomMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * Retrieves the pre-configured ObjectMapper instance.
     *
     * @return The shared ObjectMapper instance.
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Converts a JSON string into a JsonNode object.
     *
     * @param text The JSON string to parse.
     * @return A JsonNode representing the parsed JSON structure.
     * @throws JsonProcessingException If the input string is not valid JSON.
     */
    public static JsonNode getAsJsonNode(String text) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(text);
    }

    /**
     * Converts a Java object into a JsonNode.
     *
     * @param object The object to convert.
     * @return A JsonNode representation of the object.
     */
    public static JsonNode getAsJsonNode(Object object) {
        return OBJECT_MAPPER.valueToTree(object);
    }

    /**
     * Parses a JsonNode into a Map with the specified key-value type.
     *
     * @param node The JSON node to parse.
     * @param typeRef The type reference specifying the key-value types for the map.
     * @return A Map representation of the JSON data.
     * @throws JsonProcessingException If parsing the node fails.
     */
    public static <K, V> Map<K, V> parseJsonToMap(JsonNode node, TypeReference<Map<K, V>> typeRef)
            throws JsonProcessingException {
        return OBJECT_MAPPER.treeToValue(node, typeRef);
    }

    /**
     * Serializes a Java object into a JSON string.
     *
     * @param object The object to serialize.
     * @return A JSON string representation of the object.
     * @throws JsonProcessingException If serialization fails.
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     *
     * If a client requests metadata for a file or a list of files, the data will be sent as a single json-string
     * without the filed name of the variable from the message-object.
     */

    /**
     * Creates a JSON message body from an HttpMessage object. If the message contains a list, only the list is
     * serialized; otherwise, the full message is serialized.
     *
     * @param message The HTTP message object to convert to JSON.
     * @return A byte array representing the JSON-formatted message.
     */
    public static byte[] createJsonMessageBody(HttpMessage message) {

        try {
            // removes the field name from printed list object.
            return (message.getList() != null) ?
                    JsonUtil.toJsonString(message.getList()).getBytes(StandardCharsets.UTF_8) :
                    JsonUtil.toJsonString(message).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            return handleFallbackResponse(message).getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * Generates a fallback JSON response in case of serialization failure.
     *
     * @param message The original HTTP message that failed to serialize.
     * @return A JSON string containing fallback response data.
     */
    private static String handleFallbackResponse(HttpMessage message) {
        try {
            Map<String, Object> fallbackResponse = new LinkedHashMap<>();
            fallbackResponse.put("warning", "Response formatting failed; fallback content was provided");

            if (message.getStatus() != null) {
                fallbackResponse.put("status", message.getStatus());
            }
            if (message.getMessage() != null) {
                fallbackResponse.put("message", message.getMessage());
            }
            if (message.isErrorMessage()) {
                fallbackResponse.put("error", message.getError());
            }
            if (message.getReason() != null) {
                fallbackResponse.put("reason", message.getReason());
            }
            if (message.getInfo() != null) {
                fallbackResponse.put("info", message.getInfo());
            }
            if (message.getList() != null) {
                fallbackResponse.put("dataList", message.getList());
            }
            return JsonUtil.toJsonString(fallbackResponse);
        } catch (JsonProcessingException e) {
            LogHandler.handleLog(LOGGER, LoggerType.ERROR, "Formatting proper json string failed: " + e.getMessage());
            return "{\n\t\"warning\": \"Critical error: Unable to process response\"\n}";
        }
    }
}
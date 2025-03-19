package org.example.model;

/**
 * The UrlStructureMatcher class provides utility methods for comparing a generic server URL with a specific client
 * request URL. It is designed to validate whether the structure of the client's request adheres to the expected format
 * defined by the server, and to dynamically match server-specific URL patterns with actual client requests.
 *
 * The generic URL may contain placeholders (e.g., "{name}" or "{action}") that are intended to be matched with actual
 * values in the client request URL.
 *
 * The class performs the following validations:
 * 1. Compares the path of the generic URL with the specific client URL path.
 * 2. Compares the query parameters in the generic URL with the corresponding query parameters in the client URL.
 *
 * It provides methods to:
 * - Split the URL into path and query components.
 * - Validate the URL path by matching individual segments.
 * - Validate the query parameters by matching key-value pairs.
 */
final class UrlStructureMatcher {

    private UrlStructureMatcher() {}

    /**
     * Compares a generic server URL with a specific client request URL to determine if they match. The comparison
     * includes both the path and query parts of the URL, handling dynamic parameters.
     *
     * @param serverGenericUrl the generic URL pattern defined by the server
     * @param clientRequestUrl the specific URL of the client's request
     * @return true if the generic and specific URLs match; false otherwise
     */
    static boolean genericAndSpecificPathMatch(String serverGenericUrl, String clientRequestUrl) {
        String[] genericUrlParts = serverGenericUrl.split("\\?", 2); // Splits path and query;
        String[] specificUrlParts = clientRequestUrl.split("\\?", 2);

        if (genericUrlParts.length != specificUrlParts.length) {
            return false;
        }

        return (genericUrlParts.length < 2) ? validatePath(genericUrlParts[0], specificUrlParts[0]) :
                validatePath(genericUrlParts[0], specificUrlParts[0]) && validateQuery(genericUrlParts[1], specificUrlParts[1]);
    }

    /**
     * Validates that the path of the client request matches the expected structure of the generic URL pattern.
     * This method compares the segments of the URL path, accounting for dynamic parameters.
     *
     * @param genericPath the path from the generic server URL pattern
     * @param specificPath the path from the client request URL
     * @return true if the paths match; false otherwise
     */
    private static boolean validatePath(String genericPath, String specificPath) {
        String[] generic = genericPath.split("/");
        String[] specific = specificPath.split("/");

        if (generic.length != specific.length) {
            return false;
        }

        for (int i = 0; i < generic.length; i++) {
            String parameter = generic[i];
            String value = specific[i];
            if (!isUrlMatch(parameter, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates that the query part of the client request URL matches the expected query structure in the generic URL.
     * This method compares each key-value pair in the query string.
     *
     * @param genericQuery the query part from the generic server URL pattern
     * @param specificQuery the query part from the client request URL
     * @return true if the queries match; false otherwise
     */
    private static boolean validateQuery(String genericQuery, String specificQuery) {
        String[] generic = genericQuery.split("&");
        String[] specific = specificQuery.split("&");

        if (generic.length != specific.length) {
            return false;
        }

        for (int i = 0; i < generic.length; i++) {
            String[] splitGenericQuery = generic[i].split("=");
            String[] splitSpecificQuery = specific[i].split("=");
            for (int j = 0; j < splitGenericQuery.length; j++) {
                String parameter = splitGenericQuery[j];
                String value = splitSpecificQuery[j];
                if (!isUrlMatch(parameter, value)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a parameter from the generic URL matches a value from the client request URL.
     * This method handles both dynamic parameters (e.g., {parameter}) and static values.
     *
     * @param parameter the parameter from the generic URL
     * @param value the value from the client request URL
     * @return true if the parameter and value match, considering dynamic parameters; false otherwise
     */
    public static boolean isUrlMatch(String parameter, String value) {
        return parameter.startsWith("{") && parameter.endsWith("}") || parameter.equals(value);
    }
}

package org.example.enums;

/**
 * Enum representing HTTP response status codes and their corresponding messages. This enum provides predefined status
 * codes, messages, and descriptions that the server can use to communicate response statuses to clients.
 */
public enum HttpResponseStatus {

    // Server success.
    SERVER_RESPONSE_SUCCESS(200, "OK", "Request successful."),
    SERVER_RESPONSE_CREATED(201, "Created", "A new resource was created."),

    // Client error.
    CLIENT_ERROR_BAD_REQUEST(400, "Bad Request", "Request failed."),
    CLIENT_ERROR_NOT_FOUND(404, "Not Found", "Requested resource not found."),
    CLIENT_ERROR_METHOD_NOT_ALLOWED(405, "Method Not Allowed", "Request method error."),
    CLIENT_ERROR_NOT_ACCEPTABLE(406, "Not Acceptable", "Requested media type not supported."),
    CLIENT_ERROR_LENGTH_REQUIRED(411, "Length Required", "File size not established."),
    CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type", "Media type not supported."),

    // Server error.
    SERVER_ERROR_INTERNAL_SERVER_ERROR(500, "Internal Server Error", "Server failed to handle request."),
    SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported", "Server and client HTTP version mismatch.");

    private final int statusCode;
    private final String message;
    private final String description;

    /**
     * Initializes the HTTP response status with a status code, a message, and a description.
     *
     * @param statusCode   the numerical HTTP status code (e.g., 200, 404, 500).
     * @param message      the standard HTTP status message (e.g., "OK", "Not Found", "Internal Server Error").
     * @param description  a brief explanation of the status.
     */
    HttpResponseStatus(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return this.description;
    }
}
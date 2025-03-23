package org.example.enums;

/**
 * Enum representing the HTTP headers used and read by the server. This enum contains the standard headers that are used
 * for content type, content length, connection status, etc. It simplifies handling and accessing HTTP headers within
 * the server.
 */
public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    SERVER("Server"),
    HOST("Host"),
    CONTENT_DISPOSITION("Content-Disposition");

    private final String name;

    Header(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

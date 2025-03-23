package org.example.enums;

/**
 * Enum representing the connection statuses for HTTP requests.
 *
 * This enum contains two possible connection statuses: "keep-alive" and "close".
 *
 * It is used to manage the connection state between the server and client, controlling whether the connection is
 * persistent or should be closed after the request.
 */
public enum ConnectionStatus {

    OPEN("keep-alive"),
    CLOSE("close");

    private final String status;

    ConnectionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    /**
     * Parses the input string and returns the corresponding connection status. If the input does not match a valid
     * status, it defaults to the "CLOSE" status.
     *
     * @param status the string input representing the connection status to parse.
     * @return the corresponding ConnectionStatus enum, or CLOSE if the status is invalid or null.
     */
    public static ConnectionStatus getStatus(String status) {
        if (status != null) {
            for (ConnectionStatus connectionStatus : ConnectionStatus.values()) {
                if (status.equals(connectionStatus.status)) {
                    return connectionStatus;
                }
            }
        }
        return ConnectionStatus.CLOSE; // If "status" is not valid, then set status to "close".
    }
}

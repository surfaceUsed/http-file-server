package org.example.error;

/**
 * Exception class for handling errors that occur during the configuration process of the server.
 */
public class ServerConfigurationException extends RuntimeException {

    public ServerConfigurationException(String message) {
        super(message);
    }
}

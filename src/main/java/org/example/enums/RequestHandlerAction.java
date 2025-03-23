package org.example.enums;

import org.example.error.HttpRequestURLException;

/**
 * The RequestHandlerAction enum defines a set of possible actions that a request handler can take in response to an
 * HTTP request. It helps categorize the different operations (e.g., download, add, delete) that can be performed on
 * resources in the application.
 *
 * - DOWNLOAD: Action to download a resource.
 * - VIEW: Action to view a resource.
 * - POST: Action to add a new resource.
 * - DELETE: Action to delete a resource.
 * - OVERRIDE: Action to override an existing resource.
 * - UPDATE: Action to update an existing resource.
 * - INVALID: Used for invalid or unrecognized actions.
 */
public enum RequestHandlerAction {

    DOWNLOAD("download"),
    VIEW("view"),
    UPLOAD("upload"),
    DELETE("delete"),
    OVERRIDE("override"),
    UPDATE("update"),
    INVALID("invalid");

    private final String type;

    RequestHandlerAction(String type) {
        this.type = type;
    }

    public String getHandlerType() {
        return type;
    }

    /**
     * Takes a string input and returns the corresponding RequestHandlerAction enum value. If the input string does not
     * match any known action, it returns INVALID. If the input is malformed or missing essential parts, throws an
     * HttpRequestURLException.
     *
     * @param input The input string representing the action type.
     * @return The corresponding RequestHandlerAction enum.
     * @throws HttpRequestURLException If the input string is null, empty, or malformed.
     */
    public static RequestHandlerAction getHandlerType(String input) throws HttpRequestURLException {
        if (input != null && !input.isEmpty()) {
            for (RequestHandlerAction type : RequestHandlerAction.values()) {
                if (type.getHandlerType().equals(input)) {
                    return type;
                }
            }
            return INVALID;
        }
        throw new HttpRequestURLException("URL is malformed. Essential parts are missing",
                HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
    }
}

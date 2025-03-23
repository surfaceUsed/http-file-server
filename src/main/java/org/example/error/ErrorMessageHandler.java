package org.example.error;

import org.example.model.HttpMessage;

/**
 * Interface that defines a contract for classes that handle error messages.
 * Any class implementing this interface should provide a way to retrieve an error message (HttpMessage).
 */
public interface ErrorMessageHandler {

    /**
     * Retrieves the error message associated with an error condition.
     *
     * @return The HttpMessage containing the error details such as status code, description, and reason.
     */
    HttpMessage getErrorMessage();
}

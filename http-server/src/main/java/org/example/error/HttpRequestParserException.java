package org.example.error;

import org.example.enums.HttpResponseStatus;
import org.example.model.HttpMessage;

/**
 * Exception class for handling errors related to parsing HTTP requests.
 * When this exception is thrown, an error message is created and will be processed by an HttpResponse object.
 */
public class HttpRequestParserException extends Exception implements ErrorMessageHandler {

    private final HttpMessage errorMessage;

    /**
     * Constructs a new HttpRequestParserException with the specified error message and HTTP response status.
     *
     * @param message The error message explaining the reason for the exception.
     * @param httpResponseStatus The HTTP response status (e.g., 404, 500) associated with the error.
     */
    public HttpRequestParserException(String message, HttpResponseStatus httpResponseStatus) {
        super(message);
        this.errorMessage = createErrorMessage(message, httpResponseStatus);
    }

    /**
     * Creates an error message consisting of the HTTP response status, status code, error description, and reason for
     * the error.
     *
     * @param message The error message explaining the reason for the exception.
     * @param httpResponseStatus The HTTP response status (e.g., 404, 500) associated with the error.
     * @return A populated HttpMessage object containing the error information.
     */
    private HttpMessage createErrorMessage(String message, HttpResponseStatus httpResponseStatus) {
        return HttpMessage.newBuilder().createErrorMessage()
                .serverResponse(httpResponseStatus)
                .status(httpResponseStatus.getStatusCode())
                .error(httpResponseStatus.getDescription())
                .reason(message).build();
    }

    /**
     * Returns the error message associated with this exception.
     *
     * @return The HttpMessage object containing the error details.
     */
    @Override
    public HttpMessage getErrorMessage() {
        return this.errorMessage;
    }
}

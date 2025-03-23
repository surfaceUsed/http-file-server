package org.example.command;

import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;

/**
 * The Command interface represents a generic command pattern for handling HTTP requests.
 * Implementing classes define specific behavior for different HTTP methods (GET, POST, PUT, DELETE).
 *
 * This interface ensures that all command implementations provide an `execute` method, which processes the client
 * request and generates an appropriate response.
 */
public interface Command {

    /**
     * Executes the command based on the given HTTP request and prepares a response.
     *
     * @param request  The HttpRequest object containing client request details.
     * @param response The HttpResponse object to store the server's response.
     */
    void execute(HttpRequest request, HttpResponse response);
}

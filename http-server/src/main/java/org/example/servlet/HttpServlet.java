package org.example.servlet;

import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;

/**
 * Defines the contract for handling HTTP requests and responses. This interface represents a simplified servlet model,
 * providing lifecycle methods for initialization, request handling, and cleanup.
 */
public interface HttpServlet {

    /**
     * Initializes the servlet. This method is called once when the servlet is instantiated and can be used to perform
     * any necessary setup or resource allocation.
     */
    void init();

    /**
     * Handles an incoming HTTP request and generates an appropriate response.
     *
     * @param request  the incoming HttpRequest object containing request details.
     * @param response the HttpResponse object used to send a response back to the client.
     */
    void handle(HttpRequest request, HttpResponse response);

    /**
     * Cleans up resources before the servlet is destroyed. This method is called before the servlet is removed from
     * service, allowing for cleanup operations such as closing connections or releasing memory.
     */
    void destroy();
}

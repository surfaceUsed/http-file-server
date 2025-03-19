package org.example.command;

import org.example.enums.HttpMethod;
import org.example.enums.UrlRootDirectory;

/**
 * The RequestHandlerInitializer class is responsible for initializing the appropriate request handler based on the
 * provided URL root directory and HTTP method. It uses the "getRootRequestCommander()" method from the
 * "UrlRootDirectory" class to fetch the appropriate commander for processing the request.
 */
public class RequestHandlerInitializer {

    private RequestHandlerInitializer() {}

    /**
     * Initializes the appropriate command handler based on the URL root and HTTP method provided. It calls the
     * "getRootRequestCommander()" method from the "UrlRootDirectory" class to retrieve the specific request commander '
     * for the given root directory and HTTP method.
     *
     * @param urlRoot the root directory of the URL
     * @param method the HTTP method (GET, POST, etc.)
     * @return a Command object corresponding to the URL root and HTTP method
     */
    public static Command initHandler(UrlRootDirectory urlRoot, HttpMethod method) {
        return urlRoot.getRootRequestCommander(method);
    }
}

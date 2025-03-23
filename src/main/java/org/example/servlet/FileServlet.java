package org.example.servlet;

import org.example.enums.UrlRootDirectory;
import org.example.error.HttpRequestParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.model.UrlRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A servlet responsible for handling file-related HTTP requests. This servlet routes incoming requests based on the
 * specified UrlRootDirectory and delegates request execution to appropriate handlers. It supports initialization,
 * request processing, and cleanup operations.
 */
public class FileServlet implements HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServlet.class);

    private static UrlRouter urlRouter;

    private final AtomicBoolean servletInitiated;
    private final UrlRootDirectory root;

    /**
     * Constructs a FileServlet instance with the specified root directory.
     *
     * @param root the UrlRootDirectory defining the base URL path for this servlet.
     */
    FileServlet(UrlRootDirectory root) {
        this.root = root;
        this.servletInitiated = new AtomicBoolean(false);
    }

    /**
     * Initializes the URL router for request handling.
     *
     * @param root the root directory defining URL mappings.
     */
    private static void initRouter(UrlRootDirectory root) {
        urlRouter = new UrlRouter(root);
    }

    /**
     * Initializes the servlet.
     * This method ensures that the servlet is only initialized once. If the router has not been set up, it is created
     * and loaded with the necessary handlers.
     */
    @Override
    public void init() {
        if (!this.servletInitiated.get()) {
            if (urlRouter == null) { // Just to make sure.
                initRouter(this.root); // Automatically loads all handlers for all possible URLs.
            }
            this.servletInitiated.compareAndSet(false, true);
            LOGGER.info("[INFO] FileServlet initiated.");
        } else {
            LOGGER.info("[INFO] Client made new '" + this.root.getRootDirectory() + "' endpoint request.");
        }
    }

    /**
     * Handles an incoming HTTP request and generates an appropriate response.
     * Routes the request based on its HTTP method and URL, and executes the corresponding handler. If an error occurs
     * while parsing the request, an error response is generated.
     *
     * @param request  the incoming HttpRequest object containing request details.
     * @param response the HttpResponse object used to send a response back to the client.
     */
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        try {
            urlRouter.getRequestHandler(request.getMethod(), request.getFullUrl())
                    .execute(request, response);
        } catch (HttpRequestParserException e) {
            ResponseHandler.handleResponseFromServer(response, e.getErrorMessage());
        }
    }

    /**
     * Destroys the servlet and performs cleanup operations.
     * Ensures that any modifications to the file system are saved to disk before shutdown. If an error occurs while
     * saving, a warning is logged.
     */
    @Override
    public void destroy() {
        if (this.servletInitiated.get()) {
            try {
                this.root.close(); // Saves the metadata.
                LOGGER.info("[INFO] FileServlet destroyed and modifications saved to file 'files_metadata.json' " +
                        "successfully.");
            } catch (IOException e) {
                LOGGER.error("[ERROR] Critical error; failed to save file system modifications to file " +
                        "'files_metadata.json': " + e.getMessage());
                LOGGER.warn("[WARNING] File metadata was not updated properly and data might be missing/not up to date.");
            }
        }
    }
}

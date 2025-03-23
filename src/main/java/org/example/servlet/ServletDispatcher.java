package org.example.servlet;

import org.example.enums.LoggerType;
import org.example.enums.UrlRootDirectory;
import org.example.logs.LogHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages servlet instances and routes requests to the appropriate servlets.
 */
public class ServletDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletDispatcher.class);
    private final Map<UrlRootDirectory, HttpServlet> servletMapper;

    public ServletDispatcher() {
        this.servletMapper = initializeServlets();
    }

    /**
     * Initializes the servlet mappings for known URL root directories.
     *
     * @return a map containing URL root directories mapped to their corresponding servlets.
     */
    private Map<UrlRootDirectory, HttpServlet> initializeServlets() {
        Map<UrlRootDirectory, HttpServlet> map = new HashMap<>();
        map.put(UrlRootDirectory.FILES, new FileServlet(UrlRootDirectory.FILES));
        return map;
    }

    /**
     * Retrieves the servlet associated with the given root directory.
     *
     * @param rootDirectory the UrlRootDirectory to retrieve the servlet for.
     * @return the corresponding HttpServlet, or null if not found.
     */
    public HttpServlet getServlet(UrlRootDirectory rootDirectory) {
        HttpServlet servlet = this.servletMapper.get(rootDirectory);
        if (servlet == null) {
            LogHandler.handleLog(LOGGER, LoggerType.WARNING,
                    "No servlet found for root directory " + rootDirectory.getRootDirectory());
        }
        return servlet;
    }

    /**
     * Destroys all active servlets and performs necessary cleanup operations.
     */
    public void destroyActiveServlets() {

        LogHandler.handleLog(LOGGER, LoggerType.INFO, "Destroying all active servlets..");
        for (Map.Entry<UrlRootDirectory, HttpServlet> entry : this.servletMapper.entrySet()) {
            LogHandler.handleLog(LOGGER, LoggerType.INFO, "Destroying servlet for " + entry.getKey().getRootDirectory());
            entry.getValue().destroy();
        }
        LogHandler.handleLog(LOGGER, LoggerType.INFO, "All servlets have been destroyed.");
    }
}

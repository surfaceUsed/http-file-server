package org.example.model;

import org.example.command.Command;
import org.example.command.RequestHandlerInitializer;
import org.example.config.ConfigurationManager;
import org.example.enums.HttpMethod;
import org.example.enums.HttpResponseStatus;
import org.example.enums.UrlRootDirectory;
import org.example.error.HttpRequestParserException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Router class used to map the specific server request urls to specific handler objects (Command objects). These
 * objects will in turn be called upon when a client request url matches a predefined server url, and perform the
 * appropriate action.
 */
public final class UrlRouter {

    private final UrlRootDirectory root;
    private final Map<HttpMethod, Map<String, Command>> paths; // root sub-directories

    /**
     * Constructor for UrlRouter. Initializes the router with the specified root directory and sets up the url mapping.
     * Takes a UrlRootDirectory-object as parameter, and uses it to call all the valid server url structures for that
     * specific endpoint.
     */
    public UrlRouter(UrlRootDirectory rootDirectory) {
        this.root = rootDirectory;
        this.paths = new HashMap<>();
        initRouter();
    }

    /**
     *
     * Initializes the URL mapping by retrieving valid URL patterns from the ConfigurationManager for the specified root
     * directory. The Map-object is traversed, and for each valid request method, the corresponding URL patterns are
     * mapped to handler objects. The specific HttpMethod-object is used as a key in the "paths" map, which maps the URL
     * pattern to its handler object.
     */
    private void initRouter() {
        Map<String, List<String>> list = ConfigurationManager.getInstance().getUrlStructurePatterns().get(this.root.getRootDirectory());
        for (Map.Entry<String, List<String>> entry : list.entrySet()) {
            HttpMethod method = HttpMethod.valueOf(entry.getKey());
            for (String url : entry.getValue()) {
                paths.computeIfAbsent(method, value -> new HashMap<>()).put(url, RequestHandlerInitializer.initHandler(this.root, method));
            }
        }
    }

    /**
     * Given an HTTP method and a requested URL, this method returns the corresponding handler object that should be
     * used to process the request. The URL structure is compared to the available URL patterns, and if a match is
     * found, the appropriate handler object is returned.
     * If no matching handler is found, an exception is thrown indicating a malformed URL or unsupported method.
     *
     * @param method The HTTP method (GET, POST, etc.)
     * @param requestUrl The URL of the client request.
     * @return Command The handler object for the matched URL.
     * @throws HttpRequestParserException If no handler matches or if the method is not allowed for the requested
     *                                    resource.
     */
    public Command getRequestHandler(HttpMethod method, String requestUrl) throws HttpRequestParserException {
        Map<String, Command> methodMapper = this.paths.get(method);
        if (methodMapper != null) {
            for (Map.Entry<String, Command> entry : methodMapper.entrySet()) {
                if (UrlStructureMatcher.genericAndSpecificPathMatch(entry.getKey(), requestUrl)) {
                    return entry.getValue();
                }
            }
            throw new HttpRequestParserException("URL malformed; the requested path is not valid",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }
        throw new HttpRequestParserException("'" + method + "' is not a valid method for the requested resource",
                HttpResponseStatus.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }
}
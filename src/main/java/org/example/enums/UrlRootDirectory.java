package org.example.enums;

import org.example.command.Command;
import org.example.command.methods.DELETE;
import org.example.command.methods.GET;
import org.example.command.methods.POST;
import org.example.command.methods.PUT;
import org.example.error.HttpRequestParserException;
import org.example.service.FileService;
import org.example.service.ServerCloudStorage;
import java.io.IOException;

/**
 * Enum representing the root directories (endpoints) of the server. Each root directory is associated with a set of
 * request handlers that process HTTP methods.
 *
 * TODO: Extend this enum to include additional endpoints in the future.
 */
public enum UrlRootDirectory {

    /**
     *
     * Handles all client requests made to this root directory.
     */
    FILES("/files") {

        // Instance of FileService used for managing file operations for the "/files" endpoint.
        private final FileService service = ServerCloudStorage.getInstance();

        /**
         * Returns the appropriate command handler for the requested HTTP method. Depending on the method (GET, PUT,
         * POST, DELETE), a corresponding command object is returned.
         *
         * @param method The HTTP method (GET, PUT, POST, DELETE).
         * @return The command handler for the specified HTTP method.
         */
        @Override
        public Command getRootRequestCommander(HttpMethod method) {

            return switch (method) {

                case GET -> new GET(service);

                case PUT -> new PUT(service);

                case POST -> new POST(service);

                case DELETE -> new DELETE(service);
            };
        }

        /**
         * Saves any modifications made to the file system.
         *
         * @throws IOException If there is an error saving the file system.
         */
        @Override
        public void close()throws IOException {
            service.close();
        }
    };

    private final String rootDirectory;

    UrlRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Returns the `UrlRootDirectory` corresponding to the provided input string.
     *
     * @param input The string representing a root directory.
     * @return The matching `UrlRootDirectory`.
     * @throws HttpRequestParserException If no matching root directory is found.
     */
    public static UrlRootDirectory getRootDirectory(String input) throws HttpRequestParserException {
        for (UrlRootDirectory root : UrlRootDirectory.values()) {
            if (root.getRootDirectory().equals(input)) {
                return root;
            }
        }
        throw new HttpRequestParserException("Root directory is invalid",
                HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
    }

    /**
     * Returns the specific command handler for the requested URL endpoint and HTTP method.
     *
     * @param method The HTTP method (GET, PUT, POST, DELETE).
     * @return The `Command` object that processes the request.
     */
    public abstract Command getRootRequestCommander(HttpMethod method);

    /**
     * Closes the resource at the specific endpoint.
     *
     * @throws IOException If there is an error saving the modifications.
     */
    public abstract void close() throws IOException;
}
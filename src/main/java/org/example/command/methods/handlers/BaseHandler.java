package org.example.command.methods.handlers;

import org.example.enums.Header;
import org.example.enums.HttpResponseStatus;
import org.example.enums.RequestHandlerAction;
import org.example.error.FileSystemException;
import org.example.error.HttpRequestParserException;
import org.example.error.HttpRequestURLException;
import org.example.error.HttpResponseParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.service.FileService;

/**
 * Abstract base class for handling file-related HTTP requests.
 *
 * Subclasses implement specific actions such as downloading, viewing, adding, updating, overriding, and deleting files.
 * Provides utility methods for parsing request details.
 */
public abstract class BaseHandler {

    protected final HttpRequest request;
    protected final HttpResponse response;

    /**
     * Constructs a BaseHandler with the given request and response.
     *
     * @param request  The HTTP request object.
     * @param response The HTTP response object.
     */
    public BaseHandler(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Executes the handler's logic.
     *
     * @throws HttpResponseParserException If there's an error parsing the response.
     * @throws HttpRequestURLException     If the request URL is malformed.
     * @throws HttpRequestParserException  If the request cannot be properly parsed.
     * @throws FileSystemException         If a file system operation fails.
     */
    public abstract void run()
            throws HttpResponseParserException, HttpRequestURLException, HttpRequestParserException, FileSystemException;

    /**
     * Writes the appropriate HTTP response message.
     */
    protected abstract void createHttpMessage();

    /**
     * Returns an appropriate handler based on the request type action.
     *
     * @param type       The request action type.
     * @param request    The HTTP request.
     * @param response   The HTTP response.
     * @param fileService The file service handling file operations.
     * @return A concrete implementation of BaseHandler.
     * @throws HttpRequestURLException If the provided request type is invalid.
     */
    public static BaseHandler getHandler(RequestHandlerAction type, HttpRequest request, HttpResponse response, FileService fileService)
            throws HttpRequestURLException {

        return switch (type) {
            case DOWNLOAD -> new DownloadHandler(request, response, fileService);
            case VIEW -> new ViewHandler(request, response, fileService);
            case UPLOAD -> new UploadHandler(request, response, fileService);
            case UPDATE -> new UpdateHandler(request, response, fileService);
            case OVERRIDE -> new OverrideHandler(request, response, fileService);
            case DELETE -> new DeleteHandler(request, response, fileService);
            default -> throw new HttpRequestURLException("URL is malformed. '" + type.getHandlerType() + "' is not a valid action",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        };
    }

    /**
     * Parses the URL path (excluding the url query) into an array of its components.
     *
     * @return A string array containing parts of the URL.
     * @throws HttpRequestURLException If the URL resource is missing or empty.
     */
    protected String[] parseUrlParts() throws HttpRequestURLException {
        String url = request.getUrlPath();
        if (url == null || url.isEmpty()) {
            throw new HttpRequestURLException("URL resource is empty",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        // Remove leading slash if present and split
        return url.startsWith("/") ? url.substring(1).split("/") : url.split("/");
    }

    /**
     * Parses an ID from a string parameter.
     *
     * @param idParam The string representing an ID.
     * @return The parsed ID as a long value.
     * @throws HttpRequestParserException If the ID is not a valid number.
     */
    protected long parseId(String idParam) throws HttpRequestParserException {
        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            throw new HttpRequestParserException("Malformed URL; '" + idParam + "' is not a valid number",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }
    }

    /**
     * Extracts the request body as a byte array.
     *
     * @param request The HTTP request.
     * @return The request body as a byte array.
     * @throws HttpRequestParserException If the body is missing or empty.
     */
    protected byte[] extractRequestBody(HttpRequest request) throws HttpRequestParserException {
        String size = request.getHeaders().get(Header.CONTENT_LENGTH.getName());
        if (size.isEmpty()) {
            throw new HttpRequestParserException("File size not established",
                    HttpResponseStatus.CLIENT_ERROR_LENGTH_REQUIRED);
        }
        byte[] responseBody = request.getRequestBody();
        if (responseBody == null || responseBody.length == 0) {
            throw new HttpRequestParserException("Missing request body.",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        return responseBody;
    }

    /**
     * Sets the Content-Disposition header to indicate a file attachment.
     *
     * @param fileName The name of the file to be downloaded.
     */
    protected void createContentDispositionHeader(String fileName) {
        response.addNewHeader(Header.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
    }
}

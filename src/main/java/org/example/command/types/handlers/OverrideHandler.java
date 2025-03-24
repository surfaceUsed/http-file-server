package org.example.command.types.handlers;

import org.example.command.ContentTypeValidator;
import org.example.enums.ContentType;
import org.example.enums.HttpResponseStatus;
import org.example.enums.UrlParameters;
import org.example.error.FileSystemException;
import org.example.error.HttpRequestParserException;
import org.example.error.HttpRequestURLException;
import org.example.error.HttpResponseParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.model.FileIdentifier;
import org.example.model.HttpMessage;
import org.example.model.Identifier;
import org.example.service.FileService;

import java.util.List;

/**
 * Handles file override requests.
 *
 * This class processes HTTP PUT requests to override existing files with new content. The file can be identified either
 * by name or ID.
 */
public class OverrideHandler extends BaseHandler {

    // Allowed content types for requests and responses
    private final List<ContentType> validRequestContentType = List.of(ContentType.APPLICATION_OCTET_STREAM, ContentType.VIDEO_MP4,
            ContentType.AUDIO_MPEG, ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF);
    private final List<ContentType> validResponseContentType = List.of(ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN,
            ContentType.NO_CONTENT_TYPE);

    private final FileService service;

    private String overrideMessage;

    /**
     * Constructs an OverrideHandler instance.
     *
     * @param request  The HTTP request containing the file override parameters.
     * @param response The HTTP response object.
     * @param service  The file service responsible for handling file operations.
     */
    OverrideHandler(HttpRequest request, HttpResponse response, FileService service) {
        super(request, response);
        this.service = service;
    }

    /**
     * Executes the override operation by first validating the content-type of the request and the ACCEPT header. It
     * then parses the URL to identify the file to be overridden, overrides it with new data, and finally
     * creates an HTTP success message with the result of the operation.
     *
     * @throws HttpResponseParserException if an error occurs while parsing the response.
     * @throws HttpRequestURLException     if the request URL is malformed.
     * @throws HttpRequestParserException  if the request format is invalid.
     * @throws FileSystemException         if an error occurs while accessing the file system.
     */
    @Override
    public void run()
            throws HttpResponseParserException, HttpRequestURLException, HttpRequestParserException, FileSystemException {

        ContentTypeValidator.validateContentTypes(this.request, this.response, this.validRequestContentType,
                this.validResponseContentType);
        parseUrlRequest();
        createHttpMessage();
    }

    /**
     * Parses the request URL to extract file identification parameters.
     *
     * @throws HttpRequestURLException    if the URL format is incorrect or missing required parameters.
     * @throws HttpRequestParserException if there is an issue parsing the request.
     * @throws FileSystemException        if an error occurs while accessing the file system.
     */
    private void parseUrlRequest() throws HttpRequestURLException, HttpRequestParserException, FileSystemException {
        String[] parts = parseUrlParts();
        if (parts.length != 2) {
            throw new HttpRequestURLException("URL is malformed",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        UrlParameters parameter = UrlParameters.getParameter(parts[0]);
        String query = parts[1];
        handleOverrideRequest(parameter, query);
    }

    /**
     * Processes the file override request based on the provided URL parameter:
     *
     * - NAME: Search for a file by name.
     * - ID: Search for a file by ID.
     *
     * @param parameter The URL parameter indicating whether the request is by file name or ID.
     * @param query     The file identifier (either name or ID).
     * @throws HttpRequestParserException if the provided file ID is not a Long.
     * @throws FileSystemException        if an error occurs while retrieving or overriding the file.
     * @throws HttpRequestURLException    if the URL contains an unsupported parameter.
     */
    private void handleOverrideRequest(UrlParameters parameter, String query)
            throws HttpRequestParserException, FileSystemException, HttpRequestURLException {
        Identifier identifier;
        switch (parameter) {
            case NAME -> {
                identifier = FileIdentifier.newBuilder().setFileName(query).build();
                this.overrideMessage = "The file '" + identifier.getStringIdentifier() + "' was overridden";
            }
            case ID -> {
                long fileId = parseId(query);
                identifier = FileIdentifier.newBuilder().setFileId(fileId).build();
                String fileName = this.service.search(identifier).getFileName();
                this.overrideMessage = "File #" + fileId + " ('" + fileName + "') was overridden";
            }
            default -> throw new HttpRequestURLException("Unsupported URL parameter",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        byte[] overrideFile = extractRequestBody(request);
        this.service.override(identifier, overrideFile);
    }

    /**
     * Creates an HTTP message indicating that the override was successful.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_SUCCESS)
                .status(HttpResponseStatus.SERVER_RESPONSE_SUCCESS.getStatusCode())
                .message("Override successful")
                .info(this.overrideMessage).build();
        ResponseHandler.handleResponseFromServer(this.response, message);
    }
}

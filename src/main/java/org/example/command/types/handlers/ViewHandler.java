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
import org.example.model.FileDetails;
import org.example.model.FileIdentifier;
import org.example.model.HttpMessage;
import org.example.model.Identifier;
import org.example.service.FileService;

import java.util.List;

/**
 * Handles HTTP requests for viewing file data.
 *
 * The class validates the content types of incoming requests and outgoing responses and interacts with the FileService
 * to fetch the file information based on the request URL parameters.
 */
public class ViewHandler extends BaseHandler {

    // Allowed content types for requests and responses
    private final List<ContentType> validRequestContentType = List.of(ContentType.ACCEPT_ANY_TYPE);
    private final List<ContentType> validResponseContentType = List.of(ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN);

    private final FileService service;

    private List<FileDetails> fileDetails;

    /**
     * Constructs a `ViewHandler` instance.
     *
     * @param request The HTTP request to process.
     * @param response The HTTP response to send.
     * @param service The `FileService` instance for handling file-related operations.
     */
    ViewHandler(HttpRequest request, HttpResponse response, FileService service) {
        super(request, response);
        this.service = service;
    }

    /**
     * Runs the main logic for processing the view request, including content type validation,
     * URL parsing, and message creation.
     *
     * @throws HttpResponseParserException If there is an error while parsing the HTTP response.
     * @throws HttpRequestURLException If the request URL is malformed.
     * @throws HttpRequestParserException If there is an error while parsing the HTTP request.
     * @throws FileSystemException If there is an issue with accessing the file system.
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
     * @throws HttpRequestURLException If the URL is malformed or does not contain the required parameters.
     * @throws HttpRequestParserException If the URL parameters cannot be parsed.
     * @throws FileSystemException If there is an issue with file system access.
     */
    private void parseUrlRequest() throws HttpRequestURLException, HttpRequestParserException, FileSystemException {
        String[] parts = parseUrlParts();
        if (parts.length < 2) {
            throw new HttpRequestParserException("Missing URL parameters", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        UrlParameters parameter = UrlParameters.getParameter(parts[0]);
        String query = parts[1];
        handleViewRequest(parameter, query);
    }

    /**
     * Handles the view request based on the URL parameter type:
     * - NAME: Search for a file by name.
     * - ID: Search for a file by ID.
     * - KEYWORD: List files that match a keyword.
     *
     * @param parameter The URL parameter identifier
     * @param query The query string to search for.
     * @throws FileSystemException If there is an issue with the file system during the search.
     * @throws HttpRequestParserException If the request cannot be properly parsed.
     * @throws HttpRequestURLException If the URL is malformed.
     */
    private void handleViewRequest(UrlParameters parameter, String query)
            throws FileSystemException, HttpRequestParserException, HttpRequestURLException {

        Identifier identifier;
        switch (parameter) {
            case NAME -> {
                identifier = FileIdentifier.newBuilder().setFileName(query).build();
                this.fileDetails = List.of(this.service.search(identifier));
            }
            case ID -> {
                long fileId = parseId(query);
                identifier = FileIdentifier.newBuilder().setFileId(fileId).build();
                this.fileDetails = List.of(this.service.search(identifier));
            }
            case QUERY -> this.fileDetails = this.service.list(query);

            default -> throw new HttpRequestURLException("Unsupported URL parameter",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    /**
     * Creates the HTTP response message containing the file details based on the request.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_SUCCESS)
                .data(this.fileDetails)
                .build();
        ResponseHandler.handleResponseFromServer(response, message);
    }
}

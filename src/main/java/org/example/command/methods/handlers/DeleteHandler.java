package org.example.command.methods.handlers;

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
 * Handles DELETE requests to remove a file from the server.
 *
 * It handles different types of URL parameters (file name and file id) to fetch the relevant file.
 *
 * The class validates the content types of incoming requests and outgoing responses, and interacts with FileService to
 * delete files based on file name or ID.
 */
public class DeleteHandler extends BaseHandler {

    // Allowed request and response content types
    private final List<ContentType> validRequestContentType = List.of(ContentType.ACCEPT_ANY_TYPE);
    private final List<ContentType> validResponseContentType = List.of(ContentType.APPLICATION_JSON,
            ContentType.TEXT_PLAIN, ContentType.NO_CONTENT_TYPE);

    private final FileService service;

    /**
     * Constructs a DeleteHandler instance.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response object.
     * @param service  The file service used to delete files.
     */
    DeleteHandler(HttpRequest request, HttpResponse response, FileService service) {
        super(request, response);
        this.service = service;
    }

    /**
     * Executes the delete operation by first validating the content-type of the request and the ACCEPT header. It then
     * parses the URL to identify the file to be deleted, performs the deletion using the appropriate file service, and
     * finally creates an HTTP success message with the result of the operation.
     *
     * @throws HttpResponseParserException if there is an error parsing the response.
     * @throws HttpRequestURLException     if the request URL is malformed.
     * @throws HttpRequestParserException  if there is an error parsing the request.
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
     * Parses the URL request to extract file identification parameters.
     *
     * @throws HttpRequestURLException    if the URL is malformed or contains invalid parameters.
     * @throws HttpRequestParserException if there is an issue parsing the request.
     * @throws FileSystemException        if an error occurs while processing the file system.
     */
    private void parseUrlRequest() throws HttpRequestURLException, HttpRequestParserException, FileSystemException {
        String[] urlParts = parseUrlParts();
        if (urlParts.length != 2) {
            throw new HttpRequestURLException("URL is malformed",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        UrlParameters parameter = UrlParameters.getParameter(urlParts[0]);
        String query = urlParts[1];
        handleDeleteRequest(parameter, query);
    }

    /**
     * Processes the deletion request based on the URL parameter type:
     *
     * - NAME: Search for a file by name.
     * - ID: Search for a file by ID.
     *
     * @param parameter The URL parameter specifying whether to delete by file name or ID.
     * @param query     The file identifier value (name or ID).
     * @throws FileSystemException        if an error occurs while accessing the file system.
     * @throws HttpRequestParserException if the file ID is invalid.
     * @throws HttpRequestURLException    if the URL contains an invalid parameter.
     */
    private void handleDeleteRequest(UrlParameters parameter, String query)
            throws FileSystemException, HttpRequestParserException, HttpRequestURLException {

        Identifier identifier;
        switch (parameter) {
            case NAME -> {
                identifier = FileIdentifier.newBuilder().setFileName(query).build();
                this.service.delete(identifier);
            }
            case ID -> {
                long fileId = parseId(query);
                identifier = FileIdentifier.newBuilder().setFileId(fileId).build();
                this.service.delete(identifier);
            }
            default -> throw new HttpRequestURLException("URL malformed; missing valid parameters",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    /**
     * Writes the HTTP response message indicating the success of the delete operation.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_SUCCESS)
                .status(HttpResponseStatus.SERVER_RESPONSE_SUCCESS.getStatusCode())
                .message("The file was deleted successfully from the server.").build();
        ResponseHandler.handleResponseFromServer(response, message);
    }
}

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
import org.example.model.FileDetails;
import org.example.model.FileIdentifier;
import org.example.model.HttpMessage;
import org.example.model.Identifier;
import org.example.service.FileService;

import java.util.List;

/**
 * Handles HTTP requests for updating file names on the server. This class processes the request, extracts parameters,
 * and updates the file name accordingly.
 */
public class UpdateHandler extends BaseHandler {

    // Allowed request and response content types
    private final List<ContentType> validRequestContentType2 = List.of(ContentType.ACCEPT_ANY_TYPE);
    private final List<ContentType> validResponseContentType = List.of(ContentType.APPLICATION_JSON,
            ContentType.TEXT_PLAIN, ContentType.NO_CONTENT_TYPE);

    private final FileService service;

    private String updateMessage;

    /**
     * Constructs an UpdateHandler to process an HTTP request for updating a file name.
     *
     * @param request  The HTTP request containing update information.
     * @param response The HTTP response to be sent back to the client.
     * @param service  The file service used for handling file updates.
     */
    UpdateHandler(HttpRequest request, HttpResponse response, FileService service) {
        super(request, response);
        this.service = service;
    }

    /**
     * Executes the file update process. Validates content type and ACCEPT header, parses the request URL, processes
     * the update operation, and creates an HTTP success message.
     *
     * @throws HttpResponseParserException If there is an error parsing the response.
     * @throws HttpRequestURLException     If the request URL is malformed or invalid.
     * @throws HttpRequestParserException  If the request format is incorrect.
     * @throws FileSystemException         If an error occurs while updating the file.
     */
    @Override
    public void run()
            throws HttpResponseParserException, HttpRequestURLException, HttpRequestParserException, FileSystemException {

        ContentTypeValidator.validateContentTypes(this.request, this.response, this.validRequestContentType2,
                this.validResponseContentType);
        parseUrlRequest();
        createHttpMessage();
    }

    /**
     * Parses the request URL to extract parameters and determine the update operation. It retrieves the path parameter
     * from the URL ("id" or "name"), extracts the query value, and fetches the "VALUE" parameter from the request
     * query string, which represents the new file name.
     *
     * @throws HttpRequestURLException     If the URL is malformed.
     * @throws HttpRequestParserException  If there is an error parsing the request.
     * @throws FileSystemException         If an error occurs while processing the update.
     */
    private void parseUrlRequest() throws HttpRequestURLException, HttpRequestParserException, FileSystemException {
        String[] urlParts = parseUrlParts();
        if (urlParts.length != 2) {
            throw new HttpRequestURLException("URL is malformed",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        UrlParameters pathParameter = UrlParameters.getParameter(urlParts[0]);
        String query = urlParts[1];
        String updateName = UrlParameters.mapQueryValues(request.getUrlQuery()).get(UrlParameters.VALUE);
        handleUpdateRequest(pathParameter, query, updateName);
    }

    /**
     * Handles the update request based on the given parameter type. Supports updating a file by name or ID.
     *
     * @param parameter   The URL parameter type (NAME or ID).
     * @param query       The query value (existing file name or ID).
     * @param updateName  The new file name to update.
     * @throws HttpRequestParserException  If the file type of the new name doesn't match the original.
     * @throws FileSystemException         If an error occurs while updating the file.
     * @throws HttpRequestURLException     If the URL lacks valid parameters.
     */
    private void handleUpdateRequest(UrlParameters parameter, String query, String updateName)
            throws HttpRequestParserException, FileSystemException, HttpRequestURLException {
        Identifier identifier;
        switch (parameter) {
            case NAME -> {
                if (!FileDetails.isEqualFileType(query, updateName)) {
                    throw new HttpRequestParserException("The file type of the updated file name does not match the original file type",
                            HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
                }
                identifier = FileIdentifier.newBuilder().setFileName(query).build();
                this.updateMessage = "New file name: " + updateName;
            }
            case ID -> {
                Long fileId = parseId(query);
                identifier = FileIdentifier.newBuilder().setFileId(fileId).build();
                String originalFileName = this.service.search(identifier).getFileName();
                if (!FileDetails.isEqualFileType(originalFileName, updateName)) {
                    throw new HttpRequestParserException("The file type of the updated file name does not match the original file type",
                            HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
                }
                this.updateMessage = "File #" + fileId + " has a new name: " + updateName;
            }
            default -> throw new HttpRequestURLException("URL malformed; missing valid parameters",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }

        this.service.update(identifier, updateName);
    }

    /**
     * Creates an HTTP response message indicating that the file was successfully updated.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_SUCCESS)
                .status(HttpResponseStatus.SERVER_RESPONSE_SUCCESS.getStatusCode())
                .message("File updated successfully")
                .info(this.updateMessage).build();
        ResponseHandler.handleResponseFromServer(response, message);
    }
}

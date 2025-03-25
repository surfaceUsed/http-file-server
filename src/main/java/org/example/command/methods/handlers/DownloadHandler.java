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
 * Handles file download requests.
 *
 * It handles different types of URL parameters (file name and file id) to fetch the relevant file from the server based
 * on the parameter value. The file's content is then sent back in the HTTP response.
 */
public class DownloadHandler extends BaseHandler {

    // Allowed content types for requests and responses
    private final List<ContentType> validRequestContentType = List.of(ContentType.ACCEPT_ANY_TYPE);
    private final List<ContentType> validResponseContentType = List.of(ContentType.APPLICATION_OCTET_STREAM, ContentType.VIDEO_MP4,
            ContentType.AUDIO_MPEG, ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF);

    private final FileService fileService;
    private byte[] fileBytes;

    /**
     * Constructs a DownloadHandler instance.
     *
     * @param request     The HTTP request containing the download parameters.
     * @param response    The HTTP response object.
     * @param fileService The file service used to retrieve files from the system.
     */
    DownloadHandler(HttpRequest request, HttpResponse response, FileService fileService) {
        super(request, response);
        this.fileService = fileService;
    }

    /**
     * Executes the download operation by first validating the content-type of the request and the ACCEPT header. It
     * then parses the URL to identify the file to be downloaded, retrieves the file from the FileService, and finally
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
            throw new HttpRequestParserException("Missing URL parameters",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        UrlParameters parameter = UrlParameters.getParameter(parts[0]);
        String query = parts[1];
        handleDownloadRequest(parameter, query);
    }

    /**
     * Processes the file retrieval request based on the provided URL parameter:
     *
     * - NAME: Search for a file by name.
     * - ID: Search for a file by ID.
     *
     * @param parameter                   The URL parameter indicating whether the request is by file name or ID.
     * @param query                       The file identifier (either name or ID).
     * @throws HttpRequestParserException if the provided file ID is invalid.
     * @throws FileSystemException        if an error occurs while retrieving the file.
     * @throws HttpRequestURLException    if the URL contains an unsupported parameter.
     */
    private void handleDownloadRequest(UrlParameters parameter, String query)
            throws HttpRequestParserException, FileSystemException, HttpRequestURLException {
        Identifier identifier;
        switch (parameter) {
            case NAME -> {
                identifier = FileIdentifier.newBuilder().setFileName(query).build();
                createContentDispositionHeader(identifier.getStringIdentifier());
                this.fileBytes = this.fileService.retrieve(identifier);
            }
            case ID -> {
                long fileId = parseId(query);
                identifier = FileIdentifier.newBuilder().setFileId(fileId).build();
                String fileName = this.fileService.search(identifier).getFileName();
                createContentDispositionHeader(fileName);
                this.fileBytes = this.fileService.retrieve(identifier);
            }
            default -> throw new HttpRequestURLException("Unsupported URL parameter",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    /**
     * Creates an HTTP message containing the requested file data and sends it in the response.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_SUCCESS)
                .responseBody(this.fileBytes)
                .build();
        ResponseHandler.handleResponseFromServer(response, message);
    }
}
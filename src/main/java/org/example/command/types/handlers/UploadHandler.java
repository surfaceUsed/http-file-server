package org.example.command.types.handlers;

import org.example.command.ContentTypeValidator;
import org.example.enums.ContentType;
import org.example.enums.Header;
import org.example.enums.HttpResponseStatus;
import org.example.error.FileSystemException;
import org.example.error.HttpRequestParserException;
import org.example.error.HttpRequestURLException;
import org.example.error.HttpResponseParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import org.example.http.response.ResponseHandler;
import org.example.model.HttpMessage;
import org.example.service.FileService;

import java.util.List;

/**
 * Handles HTTP POST requests for uploading files to the server.
 * This class processes the request, extracts the file, and saves it in the file system.
 */
public class UploadHandler extends BaseHandler {

    // Allowed request and response content types
    private final List<ContentType> validRequestContentType = List.of(ContentType.APPLICATION_OCTET_STREAM, ContentType.VIDEO_MP4,
            ContentType.AUDIO_MPEG, ContentType.IMAGE_JPEG, ContentType.IMAGE_PNG, ContentType.IMAGE_GIF);
    private final List<ContentType> validResponseContentType = List.of(
            ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN, ContentType.NO_CONTENT_TYPE);

    private final FileService service;

    private String fileName;
    private long fileId;

    /**
     * Constructs a PostHandler to process an HTTP POST request.
     *
     * @param request  The HTTP request containing the file to be uploaded.
     * @param response The HTTP response that will be sent back to the client.
     * @param service  The file service used to store the uploaded file.
     */
    UploadHandler(HttpRequest request, HttpResponse response, FileService service) {
        super(request, response);
        this.service = service;
    }

    /**
     * Executes the file upload process. Validates the content type and ACCEPT header, parses the request URL, extracts
     * the file, stores it, and creates an HTTP success message.
     *
     * @throws HttpResponseParserException If there is an error parsing the response.
     * @throws HttpRequestURLException     If the request URL is malformed or invalid.
     * @throws HttpRequestParserException  If the request format is incorrect.
     * @throws FileSystemException         If an error occurs while storing the file.
     */
    @Override
    public void run() throws HttpResponseParserException, HttpRequestURLException, HttpRequestParserException, FileSystemException {
        ContentTypeValidator.validateContentTypes(this.request, this.response,
                this.validRequestContentType, this.validResponseContentType);
        parseUrlRequest();
        createHttpMessage();
    }

    /**
     *
     * NEED TO UPDATE!!!!!!
     *
     * Parses the request URL to extract the file name and reads the request body. The extracted file is then saved
     * using the file service.
     *
     * @throws HttpRequestParserException  If there is an error parsing the request.
     * @throws FileSystemException         If an error occurs while saving the file.
     */
    private void parseUrlRequest() throws HttpRequestParserException, FileSystemException {
        /*String[] parts = parseUrlParts();
        if (parts.length != 2) {
            throw new HttpRequestURLException("URL is malformed",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        this.fileName = parts[1];


        Burde heller lage url: /files/upload, og hente filnavnet fra conteten disposition header.

        byte[] fileToPost = extractRequestBody(request);
        this.fileId = this.service.add(this.fileName, fileToPost);

         */

        this.fileName = parseContentDisposition(request);
        byte[] fileToPost = extractRequestBody(request);
        this.fileId = this.service.add(this.fileName, fileToPost);
    }


    /**
     *
     * Parses the Content-Disposition header to get the name of the downloaded file.
     *
     * Header as String: Content-Disposition: attachment; filename="example.txt"
     *
     * Splits the header value at '=', and then removes the remaining '"' from start
     * and end of the header String. Returns the name of the file formatted correctly.
     */
    private static String parseContentDisposition(HttpRequest response) throws HttpRequestParserException {
        String disposition = response.getHeaders().get(Header.CONTENT_DISPOSITION.getName());
        if (disposition == null) {
            throw new HttpRequestParserException("Failed to retrieve file name because Content-Disposition header " +
                    "is missing", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
        String[] splitDisposition = disposition.split("=");
        System.out.println("what is filename: " + splitDisposition[1].substring(1, splitDisposition[1].length() - 1));
        return splitDisposition[1].substring(1, splitDisposition[1].length() - 1);
    }



    /**
     * Creates an HTTP response message indicating that the file was successfully uploaded.
     */
    @Override
    protected void createHttpMessage() {
        HttpMessage message = HttpMessage.newBuilder()
                .serverResponse(HttpResponseStatus.SERVER_RESPONSE_CREATED)
                .status(HttpResponseStatus.SERVER_RESPONSE_CREATED.getStatusCode())
                .message("File saved on the server")
                .info("'" + this.fileName + "' was given a unique identifier #" + this.fileId).build();
        ResponseHandler.handleResponseFromServer(response, message);
    }
}

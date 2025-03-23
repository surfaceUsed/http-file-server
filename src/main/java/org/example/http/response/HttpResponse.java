package org.example.http.response;

import org.example.config.ConfigurationManager;
import org.example.enums.ConnectionStatus;
import org.example.enums.ContentType;
import org.example.enums.Header;
import org.example.model.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an HTTP response, responsible for constructing, initializing, and sending a response from the server to
 * the client.
 *
 * The response includes a status line, headers, and an optional body. Headers such as `Content-Type`, `Content-Length`,
 * and `Connection` are set accordingly.
 *
 * The class provides methods for initializing a response based on an HttpMessage, adding headers, setting the
 * connection status, and writing the full response to an output stream.
 */
public class HttpResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private static final String HTTP_VERSION = ConfigurationManager.getInstance().getServerProperties().getHttpVersion();
    private static final String SERVER_NAME = ConfigurationManager.getInstance().getServerProperties().getServerName();

    private String statusLine;
    private final Map<Header, Object> headers;
    private byte[] responseBody;
    private ConnectionStatus connectionStatus;
    private ContentType serverResponseContentType;

    /**
     * Constructs an HttpResponse object with default headers.
     *
     * The `Connection` header is set to `CLOSE` by default, and the `Server` header is initialized with the server name
     * from the configuration.
     */
    public HttpResponse() {
        this.headers = new LinkedHashMap<>();
        this.connectionStatus = ConnectionStatus.CLOSE; // Set close as default.
        initHeader();
    }

    /**
     * Initializes the default headers for the HTTP response.
     *
     * The `Server` header is added using the configured server name.
     */
    private void initHeader() {
        this.headers.put(Header.SERVER, SERVER_NAME);
    }

    /**
     * Sets the status line of the response.
     *
     * @param statusLine The HTTP status line (e.g., "HTTP/1.1 200 OK").
     */
    private void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    /**
     * Adds a new header to the response.
     *
     * @param header The header field to add.
     * @param value  The value associated with the header.
     */
    public void addNewHeader(Header header, Object value) {
        this.headers.put(header, value);
    }

    /**
     * Sets the content type of the response.
     *
     * @param contentType The ContentType of the response.
     */
    public void setServerResponseContentType(ContentType contentType) {
        this.serverResponseContentType = contentType;
    }

    /**
     * Retrieves the content type of the response.
     *
     * @return The ContentType of the response.
     */
    public ContentType getServerResponseContentType() {
        return this.serverResponseContentType;
    }

    /**
     * Sets the connection status of the response.
     *
     * @param connectionStatus The ConnectionStatus to be used.
     */
    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * Sets the response body.
     *
     * @param responseBody The body content as a byte array.
     */
    private void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * Initializes the HTTP response based on the provided HttpMessage.
     *
     * This method constructs the response status line, sets initial headers (including `Content-Type`,
     * `Content-Length`, and `Connection`), and assigns the response body if it's not empty.
     *
     * @param message      The HttpMessage containing the response status.
     * @param responseBody The response body as a byte array.
     */
    void initResponse(HttpMessage message, byte[] responseBody) {
        int responseLength = (responseBody == null) ? 0 : responseBody.length;
        setStatusLine(HttpResponseInitializer.createStatusLine(HTTP_VERSION, message.getServerResponse()));
        HttpResponseInitializer.initHeaders(this.headers, this.connectionStatus, this.serverResponseContentType, responseLength);
        if (responseLength > 0) {
            setResponseBody(responseBody);
        }
    }

    /**
     * Sends the fully constructed HTTP response to the provided output stream.
     *
     * This method writes the status line, headers, and body (if present) to the output stream.
     *
     * @param output The OutputStream to which the response will be written.
     * @throws IOException If an error occurs while writing to the stream.
     */
    public void sendResponse(OutputStream output) throws IOException {
        output.write(this.statusLine.getBytes(StandardCharsets.UTF_8));
        output.write(HttpResponseInitializer.getHeadersAsString(this.headers).getBytes(StandardCharsets.UTF_8));
        if (this.responseBody != null && this.responseBody.length > 0) {
            output.write(this.responseBody);
        }
        output.flush();
    }
}

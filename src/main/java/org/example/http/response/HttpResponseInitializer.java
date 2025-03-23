package org.example.http.response;

import org.example.enums.ConnectionStatus;
import org.example.enums.ContentType;
import org.example.enums.Header;
import org.example.enums.HttpResponseStatus;

import java.util.Map;

/**
 * Utility class for initializing HTTP response components, including the response line and headers.
 *
 * This class provides helper methods to construct an HTTP response, ensuring proper formatting and inclusion of
 * essential headers. It is designed to support the HTTP response structure, handling connection status, content type,
 * and content length.
 */
public class HttpResponseInitializer {

    private static final String NEW_LINE_SEPARATOR = "\r\n";

    private HttpResponseInitializer() {}

    /**
     * Constructs the response line of an HTTP response.
     *
     * The status line follows the format:
     *
     *     <HTTP Version> <Status Code> <Reason Phrase><CRLF>
     *
     * Example:
     *
     *     HTTP/1.1 200 OK\r\n
     *
     * @param httpVersion     The HTTP version.
     * @param serverResponse  The HttpResponseStatus containing the status code and message.
     * @return A formatted HTTP status line.
     */
    static String createStatusLine(String httpVersion, HttpResponseStatus serverResponse) {
        return httpVersion + " " + serverResponse.getStatusCode() + " " + serverResponse.getMessage() + NEW_LINE_SEPARATOR;
    }

    /**
     * Initializes and populates the response headers with essential information.
     *
     * This method ensures that the response includes headers for:
     * - Connection status
     * - Content type (if applicable)
     * - Content length (if greater than 0)
     *
     * @param headers          The map where headers are stored.
     * @param connectionStatus The connection status (e.g., keep-alive or close).
     * @param contentType      The content type of the response.
     * @param size             The content length in bytes.
     */
    static void initHeaders(Map<Header, Object> headers, ConnectionStatus connectionStatus, ContentType contentType, int size) {
        addConnectionStatusHeader(headers, connectionStatus);
        addContentTypeHeader(headers, contentType);
        addContentLengthHeader(headers, size);
    }

    /**
     * Adds the Connection header to the response.
     *
     * @param headers          The headers map.
     * @param connectionStatus The connection status to be set.
     */
    private static void addConnectionStatusHeader(Map<Header, Object> headers, ConnectionStatus connectionStatus) {
        headers.put(Header.CONNECTION, connectionStatus.getStatus());
    }

    /**
     * Adds the Content-Type header to the response if applicable.
     *
     * @param headers     The headers map.
     * @param contentType The content type to be set.
     */
    private static void addContentTypeHeader(Map<Header, Object> headers, ContentType contentType) {
        if (contentType != ContentType.NO_CONTENT_TYPE) {
            headers.put(Header.CONTENT_TYPE, contentType.getType());
        }
    }

    /**
     * Adds the Content-Length header to the response if the content size is greater than zero.
     *
     * @param headers The headers map.
     * @param size    The content length in bytes.
     */
    private static void addContentLengthHeader(Map<Header, Object> headers, int size) {
        if (size > 0) {
            headers.put(Header.CONTENT_LENGTH, size);
        }
    }

    /**
     * Converts the response headers map into a properly formatted HTTP header string.
     *
     * Each header is formatted as:
     *
     *     Header-Name: Value\r\n
     *
     * The method ensures that an extra CRLF is added at the end to separate headers from the body.
     *
     * @param headers The headers map.
     * @return A formatted string containing all headers.
     */
    static String getHeadersAsString(Map<Header, Object> headers) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Header, Object> entry : headers.entrySet()) {
            sb.append(entry.getKey().getName())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NEW_LINE_SEPARATOR);
        }
        return sb.append(NEW_LINE_SEPARATOR).toString();
    }
}

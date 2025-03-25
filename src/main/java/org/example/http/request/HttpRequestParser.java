package org.example.http.request;

import org.example.enums.Header;
import org.example.enums.HttpResponseStatus;
import org.example.error.HttpRequestParserException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The HttpRequestParser class is responsible for parsing raw HTTP requests received from a client. It reads the request
 * from an input stream and extracts the request line, headers, and body, storing them in an HttpRequest object.
 *
 * The parsing follows the standard HTTP request format:
 *
 * 1. The request line (method, URL, and HTTP version).
 * 2. The headers (key-value pairs).
 * 3. The optional request body (based on the "Content-Length" header).
 *
 * If the request is malformed or does not follow the expected structure, an HttpRequestParserException is thrown.
 */
public class HttpRequestParser {

    private static final int SPACE = 32;
    private static final int CARRIAGE_RETURN = 13;
    private static final int LINE_FEED = 10;

    /**
     * Parses an HTTP request from the given InputStream and returns an HttpRequest object. The request is broken down
     * into components: request line, headers and request body.
     *
     * @param input The input stream containing the raw HTTP request data.
     * @return An HttpRequest object containing the parsed request details.
     * @throws HttpRequestParserException If the request is malformed or does not follow HTTP specifications.
     * @throws IOException If an I/O error occurs while reading the request data.
     */
    public static HttpRequest parseRequest(InputStream input) throws HttpRequestParserException, IOException {
        HttpRequest request = new HttpRequest();
        DataInputStream fromStream = new DataInputStream(input);
        parseRequestLine(fromStream, request);
        parseRequestHeaders(fromStream, request);
        parseRequestBody(fromStream, request);
        return request;
    }

    /**
     * Parses the request line of the HTTP request, extracting the HTTP method, request URL, and HTTP version.
     *
     * The request line follows the format:
     *
     *      <HTTP Method> <Request URL> <HTTP Version><CRLF>
     *
     * Each character from the input stream is read individually. The components are separated by spaces and stored in
     * the HttpRequest object. The method terminates when a carriage return ('\r') followed by a line feed ('\n') is
     * encountered.
     *
     * If the request line is malformed or does not contain the expected components, an exception is thrown.
     *
     * @param input   The input stream containing the request data.
     * @param request The HttpRequest object to populate.
     * @throws IOException If an I/O error occurs while reading from the stream.
     * @throws HttpRequestParserException If the request line is malformed.
     */
    private static void parseRequestLine(DataInputStream input, HttpRequest request)
            throws IOException, HttpRequestParserException {

        StringBuilder sb = new StringBuilder();
        List<String> requestLine = new ArrayList<>();

        int byteRead;

        while ((byteRead = input.read())!= -1) {
            if (byteRead == CARRIAGE_RETURN) {
                byteRead = input.read();
                if (byteRead == LINE_FEED) {
                    requestLine.add(sb.toString().trim());
                    break;
                } else {
                    throw new HttpRequestParserException("Malformed header structure: expected CRLF but found " +
                            "incomplete sequence.", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
                }
            }
            if (byteRead == SPACE) {
                requestLine.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append((char) byteRead);
            }
        }
        if (requestLine.size() == 3) {
            request.setMethod(requestLine.get(0));
            request.setRequestUrl(requestLine.get(1));
            request.setHttpVersion(requestLine.get(2));
        }
    }

    /**
     * Parses the headers of the HTTP request and stores them as key-value pairs in a map.
     *
     * Each header follows the format:
     *
     *      <Key: Value><CRLF>
     *
     * The method reads headers until it encounters an empty line (\r\n\r\n), which signals the end of the header
     * section.
     *
     * @param input   The input stream containing the request headers.
     * @param request The HttpRequest object to populate with headers.
     * @throws IOException If an I/O error occurs while reading from the stream.
     * @throws HttpRequestParserException If the headers are malformed or missing.
     */
    private static void parseRequestHeaders(DataInputStream input, HttpRequest request)
            throws IOException, HttpRequestParserException {

        StringBuilder sb = new StringBuilder();
        Map<String, String> headers = new LinkedHashMap<>();

        boolean endOfLine = false;

        int countCRLF = 0;

        int byteRead;

        while ((byteRead = input.read()) != -1) {
            if (byteRead == CARRIAGE_RETURN) {
                byteRead = input.read();
                if (byteRead == LINE_FEED) {
                    endOfLine = true;
                } else {

                    throw new HttpRequestParserException("Malformed header structure: expected CRLF but found " +
                            "incomplete sequence.", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
                }
            }
            if (endOfLine) {
                countCRLF++;
                if (countCRLF == 2) {
                    break;
                }
                String headerLine = sb.toString().trim();
                createHeaderLine(headerLine, headers);
                sb.setLength(0);
                endOfLine = false;
            } else {
                sb.append((char) byteRead);
                countCRLF = 0;
            }
        }

        if (headers.isEmpty()) {

            throw new HttpRequestParserException("No headers found or headers are improperly formatted.",
                    HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);

        }
        request.setHeaders(headers);
    }

    /**
     * Parses an individual HTTP header line and adds it to the provided header map.
     *
     * Each header is expected to follow the format "Key: Value". If a header does not contain a colon separator, an
     * exception is thrown.
     *
     * @param line      The raw header line to parse.
     * @param headerMap The map where parsed headers will be stored.
     * @throws HttpRequestParserException If the header format is invalid.
     */
    private static void createHeaderLine(String line, Map<String, String> headerMap) throws HttpRequestParserException {
        String[] headerEntry = line.split(": ", 2);
        if (headerEntry.length == 2) {
            headerMap.put(headerEntry[0], headerEntry[1]);
        } else {
            throw new HttpRequestParserException("Invalid header format: each header must be in the format " +
                    "'Key: Value'.", HttpResponseStatus.CLIENT_ERROR_BAD_REQUEST);
        }
    }

    /**
     * Parses the request body, if present, and stores it in the HttpRequest object.
     *
     * The body is read only if the "Content-Length" header is present, indicating its size. If there is no body, this
     * method does nothing.
     *
     * @param input   The input stream containing the request body.
     * @param request The HttpRequest object to populate.
     * @throws IOException If an I/O error occurs while reading the body.
     */
    private static void parseRequestBody(DataInputStream input, HttpRequest request) throws IOException {

        String bodyLength = request.getHeaders().get(Header.CONTENT_LENGTH.getName());
        if (bodyLength == null) {
            return;
        }
        request.setRequestBody(getRequestBody(input, Long.parseLong(bodyLength)));
    }

    /**
     * Reads the request body from the input stream based on the specified length.
     *
     * This method ensures that the exact number of bytes specified by the "Content-Length" header is read from the
     * stream.
     *
     * @param input    The input stream containing the request body.
     * @param bodySize The expected size of the body in bytes.
     * @return A byte array containing the request body.
     * @throws IOException If an I/O error occurs while reading.
     */
    private static byte[] getRequestBody(DataInputStream input, long bodySize) throws IOException {
        byte[] bytes = new byte[(int) bodySize];
        input.readFully(bytes); // Reads from input stream, and puts content in byte array.
        return bytes;
    }
}

package org.example.http.request;

import org.example.config.ConfigurationManager;
import org.example.enums.HttpMethod;
import org.example.enums.HttpResponseStatus;
import org.example.enums.UrlRootDirectory;
import org.example.error.HttpRequestParserException;

import java.util.Map;

/**
 * This class represents an HTTP request and provides methods to read, save, and access various HTTP request variables
 * such as the HTTP method, headers, body, URL, and version. It also handles the parsing of URLs, headers, and validates
 * request versions.
 */
public final class HttpRequest {

    private String httpVersion;
    private Map<String, String> headers;
    private byte[] requestBody;
    private HttpMethod method;
    private String fullUrl;
    private UrlRootDirectory urlRoot;
    private String urlQuery;
    private String urlPath; // Does not contain the root.

    public HttpRequest() {}

    /**
     * Gets the HTTP method of the request.
     *
     * @return The HTTP method of the request (e.g., GET, POST, PUT).
     */
    public HttpMethod getMethod() {
        return method;
    }


    /**
     * Sets the HTTP method for the request and validates the method.
     *
     * @param method The HTTP method as a string.
     * @throws HttpRequestParserException If the provided method is not supported.
     */
    void setMethod(String method) throws HttpRequestParserException {
        for (HttpMethod methodType : HttpMethod.values()) {
            if (method.equals(methodType.name())) {
                this.method = methodType;
                return;
            }
        }
        throw new HttpRequestParserException("'" + method + "' is not supported by the server",
                HttpResponseStatus.SERVER_ERROR_INTERNAL_SERVER_ERROR);
    }

    /**
     * Sets the full request URL and parses it into three components: the root, path, and query.
     *
     * The URL is split at the '?' character, if present, to separate the path from the query string.
     * - The path (before the '?') is parsed to extract the root directory and the remaining resource path.
     * - If the URL contains a query string (after the '?'), it is extracted and stored.
     *
     * If the URL is malformed or invalid, an exception will be thrown.
     *
     * @param url The full URL of the HTTP request, including the path and optional query string.
     * @throws HttpRequestParserException If the URL is malformed or invalid (e.g., missing target resource or
     *                                    improperly formatted).
     */
    public void setRequestUrl(String url) throws HttpRequestParserException {
        this.fullUrl = url;
        String[] splitUrl = this.fullUrl.split("\\?", 2);
        if (splitUrl.length == 0) {
            throw new HttpRequestParserException("Malformed URL; no target resource found",
                    HttpResponseStatus.CLIENT_ERROR_NOT_FOUND);
        }
        parseUrlPath(splitUrl[0]);
        if (splitUrl.length == 2) {
            this.urlQuery = splitUrl[1];
        }
    }

    /**
     * This method parses the provided URL to separate the root directory from the URL path.
     * It identifies the root directory by locating the first '/' in the URL and considers everything
     * before that as the root. The remaining part of the URL after the root is considered the path.
     *
     * The root directory is set in the `urlRoot` property, and the path (after the root) is set in the
     * `urlPath` property.
     *
     * @param url The URL to be parsed.
     * @throws HttpRequestParserException If an error occurs while parsing the URL.
     */
    void parseUrlPath(String url) throws HttpRequestParserException {
        int index = 0;
        for (int i = 1; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                index = i;
                break;
            }
        }
        System.out.println(url.substring(0, index));
        this.urlRoot = UrlRootDirectory.getRootDirectory((index > 0) ? url.substring(0, index) : url);
        this.urlPath = url.substring(this.urlRoot.getRootDirectory().length());
    }

    /**
     * Sets the HTTP version for the request and validates the version.
     *
     * @param httpVersion The HTTP version (e.g., "HTTP/1.1").
     * @throws HttpRequestParserException If the version is not supported by the server.
     */
    void setHttpVersion(String httpVersion) throws HttpRequestParserException {
        if (!ConfigurationManager.getInstance().getServerProperties().getHttpVersion().equals(httpVersion)) {
            throw new HttpRequestParserException("HTTP version mismatch",
                    HttpResponseStatus.SERVER_ERROR_HTTP_VERSION_NOT_SUPPORTED);
        }
        this.httpVersion = httpVersion;
    }

    /**
     * Sets the body of the HTTP request.
     *
     * @param requestBody The body of the request as a byte array.
     */
    void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Sets the headers of the HTTP request.
     *
     * @param headers A map of header names and values.
     */
    void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Gets the full URL of the request.
     *
     * @return The full URL of the request, including the query if present.
     */
    public String getFullUrl() {
        return fullUrl;
    }

    /**
     * Gets the root directory of the request URL.
     *
     * @return The root directory of the URL as a UrlRootDirectory enum.
     */
    public UrlRootDirectory getUrlRoot() {
        return urlRoot;
    }

    /**
     * Gets the query part of the request URL.
     *
     * @return The query part of the URL, or null if no query exists.
     */
    public String getUrlQuery() {
        return urlQuery;
    }

    /**
     * Gets the resource part of the request URL, excluding the root and query.
     *
     * @return The resource request portion of the URL.
     */
    public String getUrlPath() {
        return urlPath;
    }

    /**
     * Gets the HTTP version of the request.
     *
     * @return The HTTP version (e.g., "HTTP/1.1").
     */
    public String getHttpVersion() {
        return httpVersion;
    }

    /**
     * Gets the headers of the HTTP request.
     *
     * @return A map of header names and values.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the body of the HTTP request.
     *
     * @return The body of the request as a byte array.
     */
    public byte[] getRequestBody() {
        return requestBody;
    }

}
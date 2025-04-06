package org.example.command;

import org.example.enums.ContentType;
import org.example.enums.Header;
import org.example.enums.HttpResponseStatus;
import org.example.error.HttpResponseParserException;
import org.example.http.request.HttpRequest;
import org.example.http.response.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This utility class is responsible for validating the content types of both client requests and server responses.
 * It ensures that the request and response content types are supported by the server, based on predefined valid content
 * types. It also sets the appropriate content types in the response object based on the client request and server
 * capabilities.
 */
public final class ContentTypeValidator {

    private ContentTypeValidator() {}

    /**
     * Validates the content types of both the client request and the server response.
     * It checks if the content type of the client request is valid and supported, and if the server can respond
     * with a compatible content type.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response object to be populated with valid content type information.
     * @param validRequestTypes A list of content types supported by the server for incoming requests.
     * @param validResponseTypes A list of content types that the server can return in responses.
     * @throws HttpResponseParserException If the content types are invalid or not supported.
     */
    public static void validateContentTypes(HttpRequest request, HttpResponse response, List<ContentType> validRequestTypes,
                                            List<ContentType> validResponseTypes) throws HttpResponseParserException {
        validateClientRequestContentType(request, validRequestTypes);
        validateServerResponseContentType(request, response, validResponseTypes);
    }

    /**
     * Validates the content type of the client request.
     * It checks if the content type of the request is valid for the specified method and URL. If the content type is
     * invalid, an HttpResponseParserException is thrown.
     *
     * @param request The incoming HTTP request.
     * @param validRequestTypes A list of content types the server can handle for incoming requests.
     * @throws HttpResponseParserException If the request content type is unsupported or missing.
     */
    private static void validateClientRequestContentType(HttpRequest request,
                                                         List<ContentType> validRequestTypes) throws HttpResponseParserException {
        if (!validRequestTypes.contains(ContentType.ACCEPT_ANY_TYPE)) {
            ContentType requestType = ContentType.getContentType(request.getHeaders().get(Header.CONTENT_TYPE.getName())); // Returns NO_CONTENT_TYPE if the header is missing.
            if (requestType != ContentType.NO_CONTENT_TYPE && !validRequestTypes.contains(requestType)) {
                throw new HttpResponseParserException("The request content type is missing or cannot be processed by the server",
                        HttpResponseStatus.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
            }
        }
    }

    /**
     * Validates the acceptable content types for the server response, based on the client's ACCEPT header.
     * It checks if the server can provide a response in any of the content types the client has requested.
     * If no valid content type is found, an HttpResponseParserException is thrown.
     *
     * TODO: better parsing of ACCEPT header string. Current parsing only splits at','. Does not take priority into consideration.
     *
     * @param request The incoming HTTP request.
     * @param response The HTTP response object.
     * @param validResponseTypes A list of content types the server can return in responses.
     * @throws HttpResponseParserException If none of the accepted content types match the server's capabilities.
     */
    private static void validateServerResponseContentType(HttpRequest request, HttpResponse response,
                                                          List<ContentType> validResponseTypes) throws HttpResponseParserException {
        List<String> clientAcceptTypes = new ArrayList<>(Arrays.asList(request.getHeaders().get(Header.ACCEPT.getName()).split(",")));
        if (!clientAcceptTypes.isEmpty()) {
            if (clientAcceptTypes.contains(ContentType.ACCEPT_ANY_TYPE.getType())) {
                response.setServerResponseContentType(validResponseTypes.get(0));
            } else {
                for (ContentType type : validResponseTypes) {
                    if (clientAcceptTypes.contains(type.getType())) {
                        response.setServerResponseContentType(type);
                        return;
                    }
                }
                throw new HttpResponseParserException("Supported type(s): " + validResponseTypes,
                        HttpResponseStatus.CLIENT_ERROR_NOT_ACCEPTABLE);
            }
        } else {
            response.setServerResponseContentType(validResponseTypes.get(0));
        }
    }
}

package org.example.http.response;

import org.example.enums.ContentType;
import org.example.model.HttpMessage;
import org.example.util.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This class is responsible for handling server responses based on the data in the provided HttpMessage object. It
 * determines the appropriate response format (JSON, file data, plain text, or empty body) based on the content type and
 * error status of the message, and finalizes the HttpResponse object before it is sent to the client.
 */
public final class ResponseHandler {

    private ResponseHandler() {}

    /**
     * Handles the response from the server by determining the appropriate format based on the message.
     * If the message indicates an error, the response type is set to JSON by default. Otherwise, the response format
     * is determined based on the content type specified in the HttpResponse.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the data for the response.
     */
    public static void handleResponseFromServer(HttpResponse response, HttpMessage message) {
        if (message.isErrorMessage()) {
            response.setServerResponseContentType(ContentType.APPLICATION_JSON);
            jsonResponse(response, message);
        } else {
            createResponse(response, message);
        }
    }

    /**
     * Creates the response based on the server's response content type. It checks the content type set in the
     * HttpResponse and calls the appropriate method to generate the response.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the data for the response.
     */
    private static void createResponse(HttpResponse response, HttpMessage message) {

        switch (response.getServerResponseContentType()) {

            case APPLICATION_JSON -> jsonResponse(response, message);
            case APPLICATION_OCTET_STREAM, VIDEO_MP4, AUDIO_MPEG, IMAGE_JPEG, IMAGE_PNG, IMAGE_GIF -> fileTypeDataResponse(response, message);
            case TEXT_PLAIN -> plainTextResponse(response, message);
            case NO_CONTENT_TYPE -> emptyBodyResponse(response, message);
        }
    }

    /**
     * Creates a JSON response.
     *
     * This method uses the JsonUtil class to convert the HttpMessage to a JSON format and then initializes the response
     * body in the HttpResponse.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the data for the response.
     */
    private static void jsonResponse(HttpResponse response, HttpMessage message) {
        byte[] responseBody = JsonUtil.createJsonMessageBody(message);
        response.initResponse(message, responseBody);
    }

    /**
     * Handles responses where the body contains file data.
     *
     * This method retrieves the byte data of the file from the HttpMessage and initializes the response body in the
     * HttpResponse.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the file data for the response.
     */
    private static void fileTypeDataResponse(HttpResponse response, HttpMessage message ) {
        byte[] byteFile = message.getFileData();
        response.initResponse(message, byteFile);
    }

    /**
     * Creates a plain text response.
     *
     * This method converts the data in the HttpMessage to a plain text format and initializes the response body in the
     * HttpResponse.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the data for the response.
     */
    private static void plainTextResponse(HttpResponse response, HttpMessage message) {
        StringBuilder sb = new StringBuilder();
        List<Object> responseData = message.getDataList();
        for (Object data : responseData) {
            sb.append(data).append("\n");
        }
        sb.setLength(sb.length() - 1);
        response.initResponse(message, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Handles responses with no body.
     *
     * This method is used when the response does not contain any data in the body.
     *
     * @param response The HttpResponse object to be finalized and sent.
     * @param message  The HttpMessage containing the response data.
     */
    private static void emptyBodyResponse(HttpResponse response, HttpMessage message) {
        response.initResponse(message, null);
    }
}
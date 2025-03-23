package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.enums.HttpResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HTTP message response.
 * This class holds various attributes related to an HTTP response, including status codes, error messages, metadata,
 * and response bodies. It also provides a builder class for easy construction of HTTP messages.
 */
public class HttpMessage {

    @JsonIgnore
    private final List<Object> dataList; // Holds all the relevant responses for a text/plain response.
    private boolean isErrorMessage; // Indicates whether the message is an error message.
    private String error; // The error message.
    private Integer status; // The HTTP status code.
    private String message; // General message about the response.
    private String info; // Additional information about the response.
    private String reason; // Reason for the status or error.
    private List<FileDetails> metadata; // A list containing FileDetails objects (metadata).
    @JsonIgnore
    private byte[] fileData; // Holds the binary file data in case of file responses.
    @JsonIgnore
    private HttpResponseStatus serverResponse; // Represents the HTTP response status.

    /**
     * Default constructor initializes the object with an empty data list and sets error message flag to false.
     */
    HttpMessage() {
        this.dataList = new ArrayList<>();
        this.isErrorMessage = false;
    }

    /**
     * Returns the list containing response data. This object is used when the response content type is a plain text
     * response.
     *
     * @return A list of response objects.
     */
    public List<Object> getDataList() {
        return dataList;
    }

    /**
     * Marks the message as an error message.
     */
    private void setErrorMessage() {
        this.isErrorMessage = true;
    }

    /**
     * Indicates whether this message represents an error. Annotated with @JsonIgnore to exclude this field from JSON
     * serialization.
     *
     * @return true if the message represents an error, otherwise false.
     */
    @JsonIgnore
    public boolean isErrorMessage() {
        return this.isErrorMessage;
    }

    /**
     * Gets the error message.
     *
     * @return The error message.
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the error message and adds it to the data list.
     *
     * @param error The error message to set.
     */
    private void setError(String error) {
        this.error = error;
        this.dataList.add("error: " + error);
    }

    /**
     * Gets the HTTP status code.
     *
     * @return The status code.
     */
    public Integer getStatus() {
        return this.status;
    }

    /**
     * Sets the HTTP status code and adds it to the data list.
     *
     * @param status The status code to set.
     */
    private void setStatus(Integer status) {
        this.status = status;
        this.dataList.add("status: " + status);
    }

    /**
     * Gets the response message.
     *
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message and adds it to the data list.
     *
     * @param message The message to set.
     */
    private void setMessage(String message) {
        this.message = message;
        this.dataList.add("message:" + message);
    }

    /**
     * Gets additional response information.
     *
     * @return Additional information.
     */
    public String getInfo() {
        return this.info;
    }

    /**
     * Sets additional response information and adds it to the data list.
     *
     * @param info The additional information.
     */
    private void setInfo(String info) {
        this.info = info;
        this.dataList.add("info: " + info);
    }

    /**
     * Gets the reason associated with the response.
     *
     * @return The reason string.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason associated with the response and adds it to the data list.
     *
     * @param reason The reason string.
     */
    private void setReason(String reason) {
        this.reason = reason;
        this.dataList.add("reason: " + reason);
    }

    /**
     * Gets the list of file metadata.
     *
     * @return A list of FileDetails objects.
     */
    public List<FileDetails> getList() {
        return metadata;
    }

    /**
     * Sets the list of file metadata and adds its data to the data list.
     *
     * @param list The list of FileDetails objects.
     */
    private void setList(List<FileDetails> list) {
        this.metadata = list;
        String data = getListData(list);
        this.dataList.add("data:\n" + data);
    }

    /**
     * Converts the list of file metadata into a formatted string.
     *
     * @param list The list of file details.
     * @return A formatted string representation of the file metadata.
     */
    private String getListData(List<FileDetails> list) {
        StringBuilder sb = new StringBuilder();
        for (FileDetails fileDetails : list) {
            sb.append(fileDetails.toString()).append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Gets the binary file data.
     *
     * @return A byte array containing the file data.
     */
    public byte[] getFileData() {
        return this.fileData;
    }

    /**
     * Sets the binary file data.
     *
     * @param fileData The byte array containing the file data.
     */
    private void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * Gets the HTTP response status.
     *
     * @return The HttpResponseStatus.
     */
    public HttpResponseStatus getServerResponse() {
        return serverResponse;
    }

    /**
     * Sets the HTTP response status.
     *
     * @param serverResponse The HttpResponseStatus to set.
     */
    private void setServerResponse(HttpResponseStatus serverResponse) {
        this.serverResponse = serverResponse;
    }

    /**
     * Creates a new builder instance for constructing an HttpMessage.
     *
     * @return A new instance of the Builder.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Builder class for constructing HttpMessage objects.
     */
    public static class Builder {

        private final HttpMessage message;

        public Builder() {
            this.message = new HttpMessage();
        }

        /**
         * Sets the error message.
         *
         * @param error The error message.
         * @return The builder instance.
         */
        public Builder error(String error) {
            this.message.setError(error);
            return this;
        }

        /**
         * Marks this message as an error message.
         *
         * @return The builder instance.
         */
        public Builder createErrorMessage() {
            this.message.setErrorMessage();
            return this;
        }

        /**
         * Sets the HTTP status code.
         *
         * @param status The status code.
         * @return The builder instance.
         */
        public Builder status(int status) {
            this.message.setStatus(status);
            return this;
        }

        /**
         * Sets the general response message.
         *
         * @param message The response message.
         * @return The builder instance.
         */
        public Builder message(String message) {
            this.message.setMessage(message);
            return this;
        }

        /**
         * Sets additional information for the response.
         *
         * @param info The additional information.
         * @return The builder instance.
         */
        public Builder info(String info) {
            this.message.setInfo(info);
            return this;
        }

        /**
         * Sets the reason for the response status.
         *
         * @param reason The reason.
         * @return The builder instance.
         */
        public Builder reason(String reason) {
            this.message.setReason(reason);
            return this;
        }

        /**
         * Sets the file metadata.
         *
         * @param list The list of FileDetails.
         * @return The builder instance.
         */
        public Builder data(List<FileDetails> list) {
            this.message.setList(list);
            return this;
        }

        /**
         * Sets the response body.
         *
         * @param responseBody The response body.
         * @return The builder instance.
         */
        public Builder responseBody(byte[] responseBody) {
            this.message.setFileData(responseBody);
            return this;
        }

        /**
         * Sets the server response.
         *
         * @param serverResponse The server response.
         * @return The builder instance.
         */
        public Builder serverResponse(HttpResponseStatus serverResponse) {
            this.message.setServerResponse(serverResponse);
            return this;
        }

        /**
         * Builds the final HttpMessage instance.
         *
         * @return The constructed HttpMessage.
         */
        public HttpMessage build() {
            return this.message;
        }
    }
}

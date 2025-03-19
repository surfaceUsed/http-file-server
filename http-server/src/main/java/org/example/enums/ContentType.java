package org.example.enums;

/**
 * Enum representing the content types that the server can handle. This enum includes various media types such as text,
 * JSON, images, audio, and video. It is used to specify and handle different content types for HTTP requests and
 * responses.
 */
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_GIF("image/gif"),
    AUDIO_MPEG("audio/mpeg"),
    VIDEO_MP4("video/mp4"),
    ACCEPT_ANY_TYPE("*/*"),
    NO_CONTENT_TYPE("null");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Parses the input string and returns the corresponding content type. If the input does not match a valid content
     * type, it defaults to the "NO_CONTENT_TYPE".
     *
     * @param type the string input representing the content type to parse.
     * @return the corresponding ContentType enum, or NO_CONTENT_TYPE if the type is invalid or null.
     */
    public static ContentType getContentType(String type) {
        if (type != null) {
            for (ContentType contentType : ContentType.values()) {
                if (type.equals(contentType.getType())) {
                    return contentType;
                }
            }
        }
        return NO_CONTENT_TYPE;
    }
}
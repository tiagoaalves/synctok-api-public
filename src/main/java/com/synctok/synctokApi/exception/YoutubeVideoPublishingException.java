package com.synctok.synctokApi.exception;

/**
 * Exception thrown when there's an error during the YouTube video publishing process.
 */
public class YoutubeVideoPublishingException extends RuntimeException {

    /**
     * Constructs a new YoutubeVideoPublishingException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public YoutubeVideoPublishingException(String message) {
        super(message);
    }

    /**
     * Constructs a new YoutubeVideoPublishingException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public YoutubeVideoPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}

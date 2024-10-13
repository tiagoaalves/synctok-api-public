package com.synctok.synctokApi.exception;

/**
 * Exception thrown when there's an error during the TikTok video publishing process.
 */
public class TiktokVideoPublishingException extends RuntimeException {

    /**
     * Constructs a new TiktokVideoPublishingException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public TiktokVideoPublishingException(String message) {
        super(message);
    }

    /**
     * Constructs a new TiktokVideoPublishingException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public TiktokVideoPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.synctok.synctokApi.exception;

/**
 * Exception thrown when there's an error during the media publishing process.
 * This class is final to prevent extension, as it's not designed for subclassing.
 */
public final class MediaPublishException extends RuntimeException {
    private final String creationId;

    /**
     * Constructs a new MediaPublishException with the specified detail message and creation ID.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param creationId the ID of the media creation attempt
     */
    public MediaPublishException(String message, String creationId) {
        super(message);
        this.creationId = creationId;
    }

    /**
     * Constructs a new MediaPublishException with the specified detail message, cause, and creation ID.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     * @param creationId the ID of the media creation attempt
     */
    public MediaPublishException(String message, Throwable cause, String creationId) {
        super(message, cause);
        this.creationId = creationId;
    }

    /**
     * Returns the creation ID associated with this exception.
     *
     * @return the creation ID
     */
    public String getCreationId() {
        return creationId;
    }
}

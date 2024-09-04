package com.synctok.synctokApi.exception;

public class MediaPublishException extends RuntimeException {
    private final String creationId;

    public MediaPublishException(String message, String creationId) {
        super(message);
        this.creationId = creationId;
    }

    public MediaPublishException(String message, Throwable cause, String creationId) {
        super(message, cause);
        this.creationId = creationId;
    }

    public String getCreationId() {
        return creationId;
    }
}

package com.synctok.synctokApi.exception;

public class TiktokVideoPublishingException extends RuntimeException {
    public TiktokVideoPublishingException(String message) {
        super(message);
    }
    public TiktokVideoPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}

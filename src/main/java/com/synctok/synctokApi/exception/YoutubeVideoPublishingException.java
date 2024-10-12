package com.synctok.synctokApi.exception;

public class YoutubeVideoPublishingException extends RuntimeException {
    public YoutubeVideoPublishingException(String message) {
        super(message);
    }
    public YoutubeVideoPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.synctok.synctokApi.exception;

public class TiktokAuthException extends RuntimeException {

    public TiktokAuthException(String message) {
        super(message);
    }

    public TiktokAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

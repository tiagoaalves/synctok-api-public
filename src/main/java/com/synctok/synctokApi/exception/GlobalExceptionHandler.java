package com.synctok.synctokApi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles various exceptions and provides appropriate HTTP responses.
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    /**
     * Handles UnsupportedPlatformException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(UnsupportedPlatformException.class)
    public ResponseEntity<Object> handleUnsupportedPlatformException(UnsupportedPlatformException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("platform", ex.getPlatform());
        body.put("error", "Unsupported Platform");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MediaContainerCreationException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(MediaContainerCreationException.class)
    public ResponseEntity<Object> handleMediaContainerCreationException(MediaContainerCreationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("videoUrl", ex.getVideoUrl());
        body.put("error", "Media Container Creation Failed");
        body.put("details", ex.getCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles MediaPublishException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(MediaPublishException.class)
    public ResponseEntity<Object> handleMediaPublishException(MediaPublishException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("creationId", ex.getCreationId());
        body.put("error", "Media Publish Failed");
        body.put("details", ex.getCause().getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles TiktokVideoPublishingException.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(TiktokVideoPublishingException.class)
    public ResponseEntity<Object> handleTiktokVideoPublishingException(TiktokVideoPublishingException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "TikTok Video Initialization Failed");
        if (ex.getCause() != null) {
            body.put("details", ex.getCause().getMessage());
        }
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

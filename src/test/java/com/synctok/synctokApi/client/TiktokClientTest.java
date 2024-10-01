package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokVideoPublishingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TiktokClientTest {

    @Mock
    private RestTemplate restTemplate;

    private TiktokClient tiktokClient;

    private static final String ACCESS_TOKEN = "test_access_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tiktokClient = new TiktokClient(restTemplate, ACCESS_TOKEN);
    }

    @Test
    void initializeVideoUpload_Success() {
        String successResponse = "{\"data\":{\"upload_url\":\"https://example.com/upload\",\"publish_id\":\"1234567890\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokClient.VideoUploadInitializationResult result = tiktokClient.initializeVideoUpload("Test Video Title");

        assertEquals("https://example.com/upload", result.uploadUrl());
        assertEquals("1234567890", result.publishId());
    }

    @Test
    void initializeVideoUpload_HttpClientErrorException() {
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoUpload("Test Video Title"));
        assertTrue(exception.getMessage().startsWith("Failed to initialize video upload:"));
    }

    @Test
    void initializeVideoUpload_InvalidJsonResponse() {
        String invalidResponse = "Invalid JSON";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoUpload("Test Video Title"));
        assertEquals("Failed to parse response", exception.getMessage());
    }

    @Test
    void initializeVideoUpload_NullResponse() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoUpload("Test Video Title"));
        assertEquals("Received empty response from server", exception.getMessage());
    }
}

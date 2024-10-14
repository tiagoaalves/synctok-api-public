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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TiktokClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile mockMultipartFile;

    private TiktokClient tiktokClient;

    private static final String ACCESS_TOKEN = "test_access_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tiktokClient = new TiktokClient(restTemplate, ACCESS_TOKEN);
    }

    @Test
    void initializeVideoPublish_Success() {
        String successResponse = "{\"data\":{\"upload_url\":\"https://example.com/upload\",\"publish_id\":\"1234567890\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size

        TiktokClient.VideoUploadInitializationResult result = tiktokClient.initializeVideoPublish(mockMultipartFile, "Test Video Title");

        assertEquals("https://example.com/upload", result.uploadUrl());
        assertEquals("1234567890", result.publishId());
    }

    @Test
    void initializeVideoPublish_FileTooLarge() {
        when(mockMultipartFile.getSize()).thenReturn(501L * 1024 * 1024); // 501 MB, exceeding the 500 MB limit

        assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoPublish(mockMultipartFile, "Test Video Title"),
                "File size exceeds maximum allowed size");
    }

    @Test
    void initializeVideoPublish_HttpClientErrorException() {
        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoPublish(mockMultipartFile, "Test Video Title"));
        assertTrue(exception.getMessage().startsWith("Failed to initialize video upload:"));
    }

    @Test
    void initializeVideoPublish_InvalidJsonResponse() {
        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        String invalidResponse = "Invalid JSON";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoPublish(mockMultipartFile, "Test Video Title"));
        assertEquals("Failed to parse response", exception.getMessage());
    }

    @Test
    void initializeVideoPublish_NullResponse() throws IOException {
        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/post/publish/video/init/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokVideoPublishingException exception = assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.initializeVideoPublish(mockMultipartFile, "Test Video Title"));
        assertEquals("Received empty response from server", exception.getMessage());
    }

    @Test
    void publishVideo_Success() throws IOException {
        when(mockMultipartFile.getSize()).thenReturn(10L * 1024 * 1024); // 10 MB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[10 * 1024 * 1024]);

        String mockResponseBody = "{\"success\":true}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponseBody, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq("https://example.com/upload"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        assertDoesNotThrow(() -> tiktokClient.publishVideo(mockMultipartFile, "https://example.com/upload"));
    }

    @Test
    void uploadVideo_ChunkPublishFailure() throws IOException {
        when(mockMultipartFile.getSize()).thenReturn(10L * 1024 * 1024); // 10 MB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[10 * 1024 * 1024]);

        when(restTemplate.exchange(
                eq("https://example.com/upload"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThrows(TiktokVideoPublishingException.class,
                () -> tiktokClient.publishVideo(mockMultipartFile, "https://example.com/upload"),
                "Failed to upload video chunk: 400 Bad Request");
    }
}

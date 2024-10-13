package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.YoutubeVideoPublishingException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class YoutubeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile mockMultipartFile;

    private YoutubeClient youtubeClient;

    private static final String ACCESS_TOKEN = "test_access_token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        youtubeClient = new YoutubeClient(restTemplate, ACCESS_TOKEN);
    }

    @Test
    void uploadVideo_Success() throws IOException {
        String successResponse = "{\"id\":\"test-video-id\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[1024]);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-video.mp4");

        String result = youtubeClient.uploadVideo(mockMultipartFile, "Test Video Title", "Test Description");

        assertEquals("test-video-id", result);
    }

    @Test
    void uploadVideo_FileTooLarge() {
        when(mockMultipartFile.getSize()).thenReturn(257L * 1024 * 1024 * 1024); // 257 GB, exceeding the 256 GB limit

        assertThrows(YoutubeVideoPublishingException.class,
                () -> youtubeClient.uploadVideo(mockMultipartFile, "Test Video Title", "Test Description"),
                "File size exceeds maximum allowed size");
    }

    @Test
    void uploadVideo_HttpClientErrorException() throws IOException {
        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[1024]);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-video.mp4");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        YoutubeVideoPublishingException exception = assertThrows(YoutubeVideoPublishingException.class,
                () -> youtubeClient.uploadVideo(mockMultipartFile, "Test Video Title", "Test Description"));
        assertTrue(exception.getMessage().startsWith("Failed to upload video:"));
    }

    @Test
    void uploadVideo_NonOkResponseStatus() throws IOException {
        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[1024]);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-video.mp4");

        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.CREATED);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        assertThrows(YoutubeVideoPublishingException.class,
                () -> youtubeClient.uploadVideo(mockMultipartFile, "Test Video Title", "Test Description"),
                "Failed to upload video. Status code: 201");
    }

    @Test
    void uploadVideo_PrivacyStatusSetToPrivate() throws IOException {
        String successResponse = "{\"id\":\"test-video-id\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                argThat(entity -> {
                    String body = entity.getBody().toString();
                    return body.contains("\"privacyStatus\":\"private\"");
                }),
                eq(String.class)
        )).thenReturn(responseEntity);

        when(mockMultipartFile.getSize()).thenReturn(1024L); // 1 KB file size
        when(mockMultipartFile.getBytes()).thenReturn(new byte[1024]);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test-video.mp4");

        String result = youtubeClient.uploadVideo(mockMultipartFile, "Test Video Title", "Test Description");

        assertEquals("test-video-id", result);
    }
}

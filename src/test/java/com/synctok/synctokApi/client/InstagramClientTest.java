package com.synctok.synctokApi.client;
import com.synctok.synctokApi.exception.MediaContainerCreationException;
import com.synctok.synctokApi.exception.MediaPublishException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InstagramClientTest {

    @Mock
    private RestTemplate restTemplate;

    private InstagramClient instagramClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instagramClient = new InstagramClient(restTemplate, "test-token", "test-account-id");
    }

    @Test
    void createMediaContainer_Success() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        String responseBody = "{\"id\":\"123456\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        String result = instagramClient.createMediaContainer(videoUrl, title);

        assertEquals("123456", result);
        verify(restTemplate).postForEntity(contains("/media"), any(), eq(String.class));
    }

    @Test
    void createMediaContainer_HttpClientErrorException() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(MediaContainerCreationException.class, () -> instagramClient.createMediaContainer(videoUrl, title));
    }

    @Test
    void createMediaContainer_InvalidJsonResponse() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        String responseBody = "invalid json";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        assertThrows(MediaContainerCreationException.class, () -> instagramClient.createMediaContainer(videoUrl, title));
    }

    @Test
    void publishMedia_Success() {
        String creationId = "123456";
        String responseBody = "{\"id\":\"published-123\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        String result = instagramClient.publishMedia(creationId);

        assertEquals("published-123", result);
        verify(restTemplate).postForEntity(contains("/media_publish"), any(), eq(String.class));
    }

    @Test
    void publishMedia_RetrySuccess() {
        String creationId = "123456";
        String errorResponseBody = "{\"error\":{\"message\":\"Media ID is not available\"}}";
        String successResponseBody = "{\"id\":\"published-123\"}";

        ResponseEntity<String> successResponse = new ResponseEntity<>(successResponseBody, HttpStatus.OK);

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponseBody.getBytes(), null))
                .doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponseBody.getBytes(), null))
                .doReturn(successResponse)
                .when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

        String result = instagramClient.publishMedia(creationId);

        assertEquals("published-123", result);
        verify(restTemplate, times(3)).postForEntity(contains("/media_publish"), any(), eq(String.class));
    }

    @Test
    void publishMedia_MaxRetriesExceeded() {
        String creationId = "123456";
        String errorResponseBody = "{\"error\":{\"message\":\"Media ID is not available\"}}";

        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", errorResponseBody.getBytes(), null))
                .when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

        assertThrows(MediaPublishException.class, () -> instagramClient.publishMedia(creationId));

        verify(restTemplate, times(5)).postForEntity(contains("/media_publish"), any(), eq(String.class));
    }

    @Test
    void publishMedia_NonRetryableError() {
        String creationId = "123456";

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(MediaPublishException.class, () -> instagramClient.publishMedia(creationId));
        verify(restTemplate, times(1)).postForEntity(contains("/media_publish"), any(), eq(String.class));
    }

    @Test
    void checkContainerStatus_Success() {
        String creationId = "123456";
        String responseBody = "{\"status_code\":\"IN_PROGRESS\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        String result = instagramClient.checkContainerStatus(creationId);

        assertEquals("IN_PROGRESS", result);
        verify(restTemplate).exchange(contains("/123456"), eq(HttpMethod.GET), any(), eq(String.class));
    }

    @Test
    void checkContainerStatus_Error() {
        String creationId = "123456";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RuntimeException.class, () -> instagramClient.checkContainerStatus(creationId));
    }
}

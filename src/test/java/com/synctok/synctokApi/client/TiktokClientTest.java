package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokAuthException;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tiktokClient = new TiktokClient(restTemplate, "clientKey", "clientSecret", "redirectUri", "code");
    }

    @Test
    void getAccessToken_Success() {
        String successResponse = "{\"access_token\":\"test_access_token\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/oauth/token/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        String accessToken = tiktokClient.getAccessToken();
        assertEquals("test_access_token", accessToken);
    }

    @Test
    void getAccessToken_ErrorResponse() {
        String errorResponse = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid authorization code\"}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/oauth/token/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokAuthException exception = assertThrows(TiktokAuthException.class, () -> tiktokClient.getAccessToken());
        assertEquals("Authentication failed: Invalid authorization code", exception.getMessage());
    }

    @Test
    void getAccessToken_HttpClientErrorException() {
        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/oauth/token/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        TiktokAuthException exception = assertThrows(TiktokAuthException.class, () -> tiktokClient.getAccessToken());
        assertTrue(exception.getMessage().startsWith("Failed to get access token:"));
    }

    @Test
    void getAccessToken_InvalidJsonResponse() {
        String invalidResponse = "Invalid JSON";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("https://open.tiktokapis.com/v2/oauth/token/"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        TiktokAuthException exception = assertThrows(TiktokAuthException.class, () -> tiktokClient.getAccessToken());
        assertEquals("Failed to parse response", exception.getMessage());
    }
}

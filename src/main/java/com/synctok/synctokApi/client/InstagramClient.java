package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.MediaContainerCreationException;
import com.synctok.synctokApi.exception.MediaPublishException;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpMethod.GET;

@Component
public class InstagramClient {

    private final RestTemplate restTemplate;
    private final String accessToken;
    private final String accountId;

    @Autowired
    public InstagramClient(
            RestTemplate restTemplate,
            @Value("${instagram.access-token}") String accessToken,
            @Value("${instagram.account-id}") String accountId) {
        this.restTemplate = restTemplate;
        this.accessToken = accessToken;
        this.accountId = accountId;
    }

    public String createMediaContainer(String videoUrl) {
        String requestUrl = String.format("https://graph.facebook.com/v20.0/%s/media", accountId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("video_url", videoUrl);
        requestBody.put("media_type", "REELS");
        requestBody.put("caption", encodeCaption("caption"));
        requestBody.put("access_token", accessToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, request, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("id");
        } catch (HttpClientErrorException e) {
            throw new MediaContainerCreationException("Failed to create media container", e, videoUrl);
        } catch (JSONException e) {
            throw new MediaContainerCreationException("Failed to parse response", e, videoUrl);
        }
    }

    public String publishMedia(String creationId) throws MediaPublishException {
        String requestUrl = String.format("https://graph.facebook.com/v20.0/%s/media_publish", accountId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> requestBody = Map.of(
                "creation_id", creationId,
                "access_token", accessToken
        );
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        int maxRetries = 5;
        int retryDelayMs = 2000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, request, String.class);
                JSONObject jsonResponse = new JSONObject(response.getBody());
                return jsonResponse.getString("id"); // This is the ID of the published media
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.BAD_REQUEST && e.getResponseBodyAsString().contains("Media ID is not available")) {
                    if (attempt == maxRetries) {
                        throw new MediaPublishException("Failed to publish media after max retries", e, creationId);
                    }
                    try {
                        Thread.sleep(retryDelayMs);
                        retryDelayMs *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new MediaPublishException("Retry interrupted", ie, creationId);
                    }
                } else {
                    throw new MediaPublishException("Failed to publish media", e, creationId);
                }
            } catch (JSONException e) {
                throw new MediaPublishException("Failed to parse response", e, creationId);
            }
        }

        throw new MediaPublishException("Failed to publish media after max retries", creationId);
    }

    public String checkContainerStatus(String creationId) {
        String url = String.format("https://graph.instagram.com/v12.0/%s?fields=status_code", creationId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, GET, request, String.class);
            System.out.println("Container Status Response: " + response.getBody());
            JSONObject jsonResponse = new JSONObject(response.getBody());
            return jsonResponse.getString("status_code");
        } catch (HttpClientErrorException e) {
            System.err.println("Status Check Error: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to check container status", e);
        }
    }

    private String encodeCaption(String caption) {
        try {
            return URLEncoder.encode(caption, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode caption", e);
        }
    }
}

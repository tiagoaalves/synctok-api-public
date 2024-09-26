package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokAuthException;
import com.synctok.synctokApi.exception.TiktokVideoPublishingException;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Component
public class TiktokClient {
    private static final Logger logger = LoggerFactory.getLogger(TiktokClient.class);
    private final RestTemplate restTemplate;
    private final String clientKey;
    private final String clientSecret;
    private final String redirectUri;
    private final String code;

    @Autowired
    public TiktokClient(
            RestTemplate restTemplate,
            @Value("${tiktok.client-key}") String clientKey,
            @Value("${tiktok.client-secret}") String clientSecret,
            @Value("${tiktok.redirect-uri}") String redirectUri,
            @Value("${tiktok.code}") String code) {
        this.restTemplate = restTemplate;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.code = code;
    }

    public String getAccessToken() {
        String tokenUrl = "https://open.tiktokapis.com/v2/oauth/token/";
        HttpEntity<String> request = getAccessTokenRequest();

        try {
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
            JSONObject jsonResponse = new JSONObject(Objects.requireNonNull(response.getBody()));
            if (jsonResponse.has("error")) {
                String errorMessage = jsonResponse.getString("error_description");
                throw new TiktokAuthException("Authentication failed: " + errorMessage);
            }
            return jsonResponse.getString("access_token");
        } catch (HttpClientErrorException e) {
            throw new TiktokAuthException("Failed to get access token: " + e.getResponseBodyAsString(), e);
        } catch (JSONException e) {
            throw new TiktokAuthException("Failed to parse response", e);
        }
    }

    private HttpEntity<String> getAccessTokenRequest() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_key", clientKey);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);

        String encodedBody = UriComponentsBuilder.newInstance()
                .queryParams(params)
                .build()
                .toString()
                .substring(1); // Remove the leading '?'

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cache-Control", "no-cache");

        return new HttpEntity<>(encodedBody, headers);
    }

    public String initializeVideoUpload(String accessToken, String title) {
        String uploadUrl = "https://open.tiktokapis.com/v2/post/publish/video/init/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = getInitializeVideoUploadRequest(title, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, request, String.class);

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                throw new TiktokVideoPublishingException("Received empty response from server");
            }

            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.toString();
        } catch (HttpClientErrorException e) {
            throw new TiktokVideoPublishingException("Failed to initialize video upload: " + e.getResponseBodyAsString(), e);
        } catch (JSONException e) {
            throw new TiktokVideoPublishingException("Failed to parse response", e);
        }
    }

    private static HttpEntity<String> getInitializeVideoUploadRequest(String title, HttpHeaders headers) {
        JSONObject postInfo = new JSONObject();
        postInfo.put("title", title);
        postInfo.put("privacy_level", "MUTUAL_FOLLOW_FRIENDS"); //

        JSONObject sourceInfo = new JSONObject();
        sourceInfo.put("source", "FILE_UPLOAD");
        sourceInfo.put("video_size", 30000000);  // 30 MB
        sourceInfo.put("chunk_size", 5000000);   // 5 MB
        sourceInfo.put("total_chunk_count", 3);  // 30 MB / 5 MB = 6 chunks

        JSONObject requestBody = new JSONObject();
        requestBody.put("post_info", postInfo);
        requestBody.put("source_info", sourceInfo);

        return new HttpEntity<>(requestBody.toString(), headers);
    }
}

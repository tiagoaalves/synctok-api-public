package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokAuthException;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
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
        HttpEntity<String> request = getRequest();

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

    private HttpEntity<String> getRequest() {
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
}

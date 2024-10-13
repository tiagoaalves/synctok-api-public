package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.YoutubeVideoPublishingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Client for interacting with the YouTube API.
 * This class provides methods for uploading videos to YouTube.
 */
@Component
public final class YoutubeClient {

    private static final String UPLOAD_URL =
            "https://www.googleapis.com/upload/youtube/v3/videos?uploadType=multipart&part=snippet,status";
    private static final long MAX_FILE_SIZE = 256L * 1024 * 1024 * 1024; // 256GB

    private final RestTemplate restTemplate;
    private final String accessToken;

    /**
     * Constructs a new YoutubeClient with the specified RestTemplate and access token.
     *
     * @param restTemplate the RestTemplate to use for HTTP requests
     * @param accessToken the YouTube API access token
     */
    @Autowired
    public YoutubeClient(
            RestTemplate restTemplate,
            @Value("${youtube.access-token}") String accessToken) {
        this.restTemplate = restTemplate;
        this.accessToken = accessToken;
    }

    /**
     * Uploads a video to YouTube.
     *
     * @param videoFile the video file to upload
     * @param title the title of the video
     * @param description the description of the video
     * @return the ID of the uploaded video
     * @throws YoutubeVideoPublishingException if the upload fails
     */
    public String uploadVideo(MultipartFile videoFile, String title, String description) {
        if (videoFile.getSize() > MAX_FILE_SIZE) {
            throw new YoutubeVideoPublishingException("File size exceeds maximum allowed size");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(accessToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Prepare JSON metadata
        JSONObject videoMetadata = new JSONObject();
        JSONObject snippet = new JSONObject();
        snippet.put("title", title);
        snippet.put("description", description);
        snippet.put("categoryId", "22"); // Category ID for "People & Blogs"
        JSONObject status = new JSONObject();
        status.put("privacyStatus", "private");
        videoMetadata.put("snippet", snippet);
        videoMetadata.put("status", status);

        // Add JSON part
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(videoMetadata.toString(), jsonHeaders);
        body.add("json", jsonPart);

        // Add media part
        try {
            HttpHeaders videoHeaders = new HttpHeaders();
            videoHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            HttpEntity<ByteArrayResource> videoPart = new HttpEntity<>(new ByteArrayResource(videoFile.getBytes()) {
                @Override
                public String getFilename() {
                    return videoFile.getOriginalFilename();
                }
            }, videoHeaders);
            body.add("media", videoPart);
        } catch (IOException e) {
            throw new YoutubeVideoPublishingException("Failed to read video file", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    UPLOAD_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                System.out.println(jsonResponse);
                return jsonResponse.getString("id");
            } else {
                throw new YoutubeVideoPublishingException("Failed to upload video. Status code: "
                        + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new YoutubeVideoPublishingException("Failed to upload video: " + e.getResponseBodyAsString(), e);
        }
    }
}

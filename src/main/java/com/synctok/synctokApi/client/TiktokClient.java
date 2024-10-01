package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokVideoPublishingException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class TiktokClient {
    private final RestTemplate restTemplate;
    private final String accessToken;

    @Autowired
    public TiktokClient(
            RestTemplate restTemplate,
            @Value("${tiktok.access-token}") String accessToken) {
        this.restTemplate = restTemplate;
        this.accessToken = accessToken;
    }

    public VideoUploadInitResult initializeVideoUpload(String title) {
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
            JSONObject data = jsonResponse.getJSONObject("data");
            String uploadUrlResult = data.getString("upload_url");
            String publishId = data.getString("publish_id");
            return new VideoUploadInitResult(uploadUrlResult, publishId);
        } catch (HttpClientErrorException e) {
            throw new TiktokVideoPublishingException("Failed to initialize video upload: " + e.getResponseBodyAsString(), e);
        } catch (JSONException e) {
            throw new TiktokVideoPublishingException("Failed to parse response", e);
        }
    }

    private static HttpEntity<String> getInitializeVideoUploadRequest(String title, HttpHeaders headers) {
        JSONObject postInfo = new JSONObject();
        postInfo.put("title", title);
        postInfo.put("privacy_level", "SELF_ONLY"); //

        JSONObject sourceInfo = new JSONObject();
        sourceInfo.put("source", "FILE_UPLOAD");
        sourceInfo.put("video_size", 50000123);  // 30 MB
        sourceInfo.put("chunk_size", 10000000);   // 5 MB
        sourceInfo.put("total_chunk_count", 5);  // 30 MB / 5 MB = 6 chunks

        JSONObject requestBody = new JSONObject();
        requestBody.put("post_info", postInfo);
        requestBody.put("source_info", sourceInfo);

        return new HttpEntity<>(requestBody.toString(), headers);
    }

    public record VideoUploadInitResult(String uploadUrl, String publishId) {}
}

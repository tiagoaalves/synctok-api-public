package com.synctok.synctokApi.client;

import com.synctok.synctokApi.exception.TiktokVideoPublishingException;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Client for interacting with the TikTok API.
 * This class provides methods for uploading videos to TikTok.
 */
@Component
public final class TiktokClient {
    private static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5 MB chunk size
    private static final long MAX_FILE_SIZE = 500L * 1024 * 1024; // 500 MB, adjust as per TikTok's limits

    private final RestTemplate restTemplate;
    private final String accessToken;

    /**
     * Constructs a new TiktokClient with the specified RestTemplate and access token.
     *
     * @param restTemplate the RestTemplate to use for HTTP requests
     * @param accessToken the TikTok API access token
     */
    @Autowired
    public TiktokClient(
            RestTemplate restTemplate,
            @Value("${tiktok.access-token}") String accessToken) {
        this.restTemplate = restTemplate;
        this.accessToken = accessToken;
    }

    /**
     * Initializes a video upload to TikTok.
     *
     * @param videoFile the video file to upload
     * @param title the title of the video
     * @return a VideoUploadInitializationResult containing the upload URL and publish ID
     * @throws TiktokVideoPublishingException if the initialization fails
     */
    public VideoUploadInitializationResult initializeVideoUpload(MultipartFile videoFile, String title) {
        if (videoFile.getSize() > MAX_FILE_SIZE) {
            throw new TiktokVideoPublishingException("File size exceeds maximum allowed size");
        }

        String uploadUrl = "https://open.tiktokapis.com/v2/post/publish/video/init/";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = getInitializeVideoUploadRequest(videoFile, title, headers);
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
            return new VideoUploadInitializationResult(uploadUrlResult, publishId);
        } catch (HttpClientErrorException e) {
            throw new TiktokVideoPublishingException("Failed to initialize video upload: "
                    + e.getResponseBodyAsString(), e);
        } catch (JSONException e) {
            throw new TiktokVideoPublishingException("Failed to parse response", e);
        }
    }

    private static HttpEntity<String> getInitializeVideoUploadRequest(
            MultipartFile videoFile,
            String title,
            HttpHeaders headers) {
        long fileSize = videoFile.getSize();
        int chunkSize = Math.min(CHUNK_SIZE, (int) fileSize);
        int totalChunkCount = (int) Math.ceil((double) fileSize / chunkSize);

        JSONObject postInfo = new JSONObject();
        postInfo.put("title", title);
        postInfo.put("privacy_level", "SELF_ONLY");

        JSONObject sourceInfo = new JSONObject();
        sourceInfo.put("source", "FILE_UPLOAD");
        sourceInfo.put("video_size", fileSize);
        sourceInfo.put("chunk_size", chunkSize);
        sourceInfo.put("total_chunk_count", totalChunkCount);

        JSONObject requestBody = new JSONObject();
        requestBody.put("post_info", postInfo);
        requestBody.put("source_info", sourceInfo);

        return new HttpEntity<>(requestBody.toString(), headers);
    }

    /**
     * Uploads a video to TikTok.
     *
     * @param videoFile the video file to upload
     * @param uploadUrl the URL to upload the video to
     * @throws IOException if there's an error reading the video file
     * @throws TiktokVideoPublishingException if the upload fails
     */
    public void uploadVideo(MultipartFile videoFile, String uploadUrl) throws IOException {
        long fileSize = videoFile.getSize();
        byte[] fileContent = videoFile.getBytes();

        for (int chunkStart = 0; chunkStart < fileSize; chunkStart += CHUNK_SIZE) {
            int chunkEnd = Math.min(chunkStart + CHUNK_SIZE - 1, (int) fileSize - 1);
            uploadChunk(fileContent, chunkStart, chunkEnd, fileSize, uploadUrl);
        }
    }

    private void uploadChunk(byte[] fileContent, int start, int end,
                             long totalSize, String uploadUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setBearerAuth(accessToken);
        headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, totalSize));

        byte[] chunk = new byte[end - start + 1];
        System.arraycopy(fileContent, start, chunk, 0, chunk.length);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(chunk, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new TiktokVideoPublishingException("Failed to upload video chunk. Status code: "
                        + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            throw new TiktokVideoPublishingException("Failed to upload video chunk: "
                    + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Record class representing the result of video upload initialization.
     *
     * @param uploadUrl the URL to which the video should be uploaded
     * @param publishId the unique identifier for the video publishing process
     */
    public record VideoUploadInitializationResult(String uploadUrl, String publishId) { }
}

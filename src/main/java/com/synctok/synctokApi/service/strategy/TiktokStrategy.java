package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.TiktokClient;
import com.synctok.synctokApi.client.TiktokClient.VideoUploadInitializationResult;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Implementation of FilePlatformStrategy for TikTok video publishing.
 * This class handles the process of uploading and publishing videos to TikTok.
 */
@Component
public final class TiktokStrategy implements FilePlatformStrategy {
    private MultipartFile videoFile;
    private final TiktokClient tiktokClient;

    /**
     * Constructs a new TiktokStrategy with the specified TiktokClient.
     *
     * @param tiktokClient the client used for interacting with TikTok's API
     */
    @Autowired
    public TiktokStrategy(TiktokClient tiktokClient) {
        this.tiktokClient = tiktokClient;
    }

    @Override
    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public void publishVideo(String title) throws IOException {
        VideoUploadInitializationResult videoInitializationResult = tiktokClient.initializeVideoPublish(
                videoFile, title
        );
        tiktokClient.publishVideo(videoFile, videoInitializationResult.uploadUrl());
    }
}

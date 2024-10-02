package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.TiktokClient;
import com.synctok.synctokApi.client.TiktokClient.VideoUploadInitializationResult;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class TiktokStrategy implements PlatformStrategy {

    private final TiktokClient tiktokClient;

    @Autowired
    public TiktokStrategy(TiktokClient tikTokClient) {
        this.tiktokClient = tikTokClient;
    }

    @Override
    public void publishVideo(MultipartFile videoFile, String videoUrl) throws IOException {
        VideoUploadInitializationResult videoInitializationResult = tiktokClient.initializeVideoUpload(videoFile, "caption #test #dev");
        tiktokClient.uploadVideo(videoFile, videoInitializationResult.uploadUrl());
        System.out.println("Publishing video to Tiktok: " + videoUrl);
    }
}

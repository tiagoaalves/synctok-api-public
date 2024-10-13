package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.YoutubeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class YoutubeStrategy implements FilePlatformStrategy {

    private MultipartFile videoFile;
    private final YoutubeClient youtubeClient;

    @Autowired
    public YoutubeStrategy(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    @Override
    public void setVideoFile(MultipartFile videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public void publishVideo() {
        String publishedVideoUrl = youtubeClient.uploadVideo(videoFile, "title", "description");
        System.out.println("Video published to Youtube with the id: " + publishedVideoUrl);
    }
}

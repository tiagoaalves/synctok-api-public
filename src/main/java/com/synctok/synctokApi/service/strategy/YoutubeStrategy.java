package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.YoutubeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class YoutubeStrategy implements PlatformStrategy {

    private final YoutubeClient youtubeClient;

    @Autowired
    public YoutubeStrategy(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    @Override
    public void publishVideo(MultipartFile videoFile, String videoUrl) {
        String publishedVideoUrl = youtubeClient.uploadVideo(videoFile, "title", "description");
        System.out.println("Video published to Youtube with the id: " + publishedVideoUrl);
    }

}

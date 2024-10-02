package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class InstagramStrategy implements PlatformStrategy {

    private final InstagramClient instagramClient;

    @Autowired
    public InstagramStrategy(InstagramClient instagramClient) {
        this.instagramClient = instagramClient;
    }

    @Override
    public void publishVideo(MultipartFile videoFile, String videoUrl) {
        String mediaContainerId;
        mediaContainerId = instagramClient.createMediaContainer(videoUrl);
        System.out.println("Media container ID: " + mediaContainerId);
        instagramClient.publishMedia(mediaContainerId);
    }
}

package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstagramStrategy implements UrlPlatformStrategy {

    private String videoUrl;
    private final InstagramClient instagramClient;

    @Autowired
    public InstagramStrategy(InstagramClient instagramClient) {
        this.instagramClient = instagramClient;
    }

    @Override
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public void publishVideo() {
        String mediaContainerId;
        mediaContainerId = instagramClient.createMediaContainer(videoUrl);
        System.out.println("Media container ID: " + mediaContainerId);
        instagramClient.publishMedia(mediaContainerId);
    }
}

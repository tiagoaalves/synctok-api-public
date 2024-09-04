package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import com.synctok.synctokApi.exception.MediaContainerCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstagramStrategy implements PlatformStrategy {

    private final InstagramClient instagramClient;

    @Autowired
    public InstagramStrategy(InstagramClient instagramClient) {
        this.instagramClient = instagramClient;
    }

    @Override
    public void publishVideo(String videoUrl) {
        System.out.println("Publishing video to Instagram: " + videoUrl);
        String mediaContainerId;
        try {
            mediaContainerId = instagramClient.createMediaContainer(videoUrl);
        } catch (Exception e) {
            throw new MediaContainerCreationException("Failed to create media container", e, videoUrl);
        }
        System.out.println("Media container ID: " + mediaContainerId);
        instagramClient.publishMedia(mediaContainerId);
    }
}

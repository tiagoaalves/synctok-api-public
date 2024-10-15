package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of UrlPlatformStrategy for Instagram video publishing.
 * This class handles the process of uploading and publishing videos to Instagram.
 */
@Component
public final class InstagramStrategy implements UrlPlatformStrategy {
    private String videoUrl;
    private final InstagramClient instagramClient;

    /**
     * Constructs a new InstagramStrategy with the specified InstagramClient.
     *
     * @param instagramClient the client used for interacting with Instagram's API
     */
    @Autowired
    public InstagramStrategy(InstagramClient instagramClient) {
        this.instagramClient = instagramClient;
    }

    @Override
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public void publishVideo(String title) {
        String mediaContainerId = instagramClient.createMediaContainer(videoUrl, title);
        instagramClient.publishMedia(mediaContainerId);
    }
}

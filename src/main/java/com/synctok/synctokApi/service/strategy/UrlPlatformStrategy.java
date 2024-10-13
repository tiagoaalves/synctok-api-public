package com.synctok.synctokApi.service.strategy;

/**
 * Interface for platform strategies that handle URL-based video uploads.
 * This interface extends PlatformStrategy and adds functionality specific to
 * platforms that accept video URLs for publishing.
 */
public interface UrlPlatformStrategy extends PlatformStrategy {

    /**
     * Sets the URL of the video to be published on the platform.
     *
     * @param videoUrl the URL of the video to be published
     */
    void setVideoUrl(String videoUrl);
}

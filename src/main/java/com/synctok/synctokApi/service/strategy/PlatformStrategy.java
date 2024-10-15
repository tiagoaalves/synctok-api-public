package com.synctok.synctokApi.service.strategy;

import java.io.IOException;

/**
 * Interface defining the strategy for publishing videos to different platforms.
 * This interface is part of the strategy pattern implementation for multi-platform video publishing.
 */
public interface PlatformStrategy {

    /**
     * Publishes a video to the platform.
     * The specific implementation of this method will handle the details of
     * uploading and publishing the video on the respective platform.
     *
     * @param title the title of the video to be published
     * @throws IOException if there is an error during the video publishing process
     */
    void publishVideo(String title) throws IOException;
}

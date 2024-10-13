package com.synctok.synctokApi.service.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for platform strategies that handle file-based video uploads.
 * This interface extends PlatformStrategy and adds functionality specific to
 * platforms that require direct file uploads.
 */
public interface FilePlatformStrategy extends PlatformStrategy {

    /**
     * Sets the video file to be uploaded to the platform.
     *
     * @param videoFile the MultipartFile representing the video to be uploaded
     */
    void setVideoFile(MultipartFile videoFile);
}

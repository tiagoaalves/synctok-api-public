package com.synctok.synctokApi.controller;

import com.synctok.synctokApi.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for handling video-related operations.
 * This controller provides endpoints for publishing videos to multiple platforms.
 */
@RestController
@RequestMapping("/api/v1/video")
public final class VideoController {

    private final VideoService videoService;
    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);


    /**
     * Constructs a new VideoController with the specified VideoService.
     *
     * @param videoService the service to handle video operations
     */
    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * Publishes a video to specified platforms.
     *
     * @param video     the video file to be published
     * @param platforms the list of platforms to publish the video to
     * @return a ResponseEntity containing a success message
     * @throws IOException if there's an error handling the video file
     */
    @PostMapping("/publish")
    public CompletableFuture<ResponseEntity<String>> publishVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("platforms") List<String> platforms) throws IOException {
        logger.info("Received request to publish video to platforms: {}", platforms);
        return videoService.publishVideo(video, platforms)
                .thenApply(_ -> ResponseEntity.ok("Video successfully uploaded and published to "
                        + String.join(", ", platforms)))
                .exceptionally(ex -> {
                    logger.error("Error occurred while publishing video", ex);
                    return ResponseEntity.internalServerError()
                            .body("An error occurred while publishing the video: " + ex.getMessage());
                });
    }
}

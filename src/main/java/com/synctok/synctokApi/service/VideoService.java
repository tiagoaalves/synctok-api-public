package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.FilePlatformStrategy;
import com.synctok.synctokApi.service.strategy.PlatformStrategy;
import com.synctok.synctokApi.service.strategy.UrlPlatformStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for handling asynchronous video publishing operations across multiple platforms.
 * This class uses the strategy pattern to support different platform-specific publishing strategies.
 */
@Service
public final class VideoService {
    private final Map<String, PlatformStrategy> strategies;
    private final CloudinaryClient cloudinaryClient;
    private static final Logger logger = LoggerFactory.getLogger(VideoService.class);


    /**
     * Constructs a new VideoService with the specified strategies and CloudinaryClient.
     *
     * @param strategies       the list of platform-specific publishing strategies
     * @param cloudinaryClient the client used for uploading videos to Cloudinary
     */
    @Autowired
    public VideoService(List<PlatformStrategy> strategies, CloudinaryClient cloudinaryClient) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName().toLowerCase().replace("strategy", ""),
                        Function.identity()
                ));
        this.cloudinaryClient = cloudinaryClient;
    }

    /**
     * Publishes a video to the specified platforms asynchronously.
     *
     * @param videoFile the MultipartFile containing the video to be published
     * @param platforms the list of platforms to publish the video to
     * @return CompletableFuture<Void> representing the completion of all publishing operations
     * @throws IOException if there's an error during video upload
     */
    public CompletableFuture<Void> publishVideo(MultipartFile videoFile, List<String> platforms) throws IOException {
        logger.info("Starting video publication process for platforms: {}", platforms);
        String videoUrl = cloudinaryClient.uploadAndGetPublicUrl(videoFile);
        logger.info("Video uploaded to Cloudinary. Public URL: {}", videoUrl);

        List<CompletableFuture<Void>> futures = platforms.stream()
                .map(platform -> CompletableFuture.runAsync(() -> {
                    logger.info("Publishing to platform: {}", platform);
                    publishToPlatform(videoFile, videoUrl, platform);
                    logger.info("Successfully published to platform: {}", platform);
                }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> logger.info("Video published to all platforms successfully"));
    }

    private void publishToPlatform(MultipartFile videoFile, String videoUrl, String platform) {
        logger.info("Starting publication process for platform: {}", platform);
        PlatformStrategy strategy = strategies.get(platform.toLowerCase());
        try {
            switch (strategy) {
                case UrlPlatformStrategy urlPlatformStrategy -> urlPlatformStrategy.setVideoUrl(videoUrl);
                case FilePlatformStrategy filePlatformStrategy -> filePlatformStrategy.setVideoFile(videoFile);
                case null, default -> throw new UnsupportedPlatformException(platform);
            }
            strategy.publishVideo();
        } catch (UnsupportedPlatformException e) {
            throw e;  // Re-throw UnsupportedPlatformException directly
        } catch (Exception e) {
            throw new RuntimeException("Error publishing to " + platform, e);
        }
    }
}

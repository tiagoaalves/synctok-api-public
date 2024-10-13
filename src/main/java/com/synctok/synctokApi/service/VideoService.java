package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.FilePlatformStrategy;
import com.synctok.synctokApi.service.strategy.PlatformStrategy;
import com.synctok.synctokApi.service.strategy.UrlPlatformStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final Map<String, PlatformStrategy> strategies;
    private final CloudinaryClient cloudinaryClient;

    @Autowired
    public VideoService(List<PlatformStrategy> strategies, CloudinaryClient cloudinaryClient) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName().toLowerCase().replace("strategy", ""),
                        Function.identity()
                ));
        this.cloudinaryClient = cloudinaryClient;
    }

    public void publishVideo(MultipartFile videoFile, List<String> platforms) throws IOException {
        String videoUrl = cloudinaryClient.uploadAndGetPublicUrl(videoFile);

        for (String platform : platforms) {
            PlatformStrategy strategy = strategies.get(platform.toLowerCase());
            switch (strategy) {
                case UrlPlatformStrategy urlPlatformStrategy -> urlPlatformStrategy.setVideoUrl(videoUrl);
                case FilePlatformStrategy filePlatformStrategy -> filePlatformStrategy.setVideoFile(videoFile);
                case null, default -> throw new UnsupportedPlatformException(platform);
            }
            strategy.publishVideo();
        }
    }
}

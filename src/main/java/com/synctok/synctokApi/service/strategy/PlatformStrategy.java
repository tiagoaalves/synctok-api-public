package com.synctok.synctokApi.service.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PlatformStrategy {
    void publishVideo(MultipartFile videoFile, String videoUrl) throws IOException;
}

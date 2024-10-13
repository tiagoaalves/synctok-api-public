package com.synctok.synctokApi.service.strategy;

import org.springframework.web.multipart.MultipartFile;

public interface FilePlatformStrategy extends PlatformStrategy {
    void setVideoFile(MultipartFile videoFile);
}

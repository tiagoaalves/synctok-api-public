package com.synctok.synctokApi.service.strategy;

import java.io.IOException;

public interface PlatformStrategy {
    void publishVideo(String videoUrl) throws IOException;
}

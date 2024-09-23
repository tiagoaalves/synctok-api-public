package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.TiktokClient;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TiktokStrategy implements PlatformStrategy {

    private final TiktokClient tiktokClient;

    @Autowired
    public TiktokStrategy(TiktokClient tikTokClient) {
        this.tiktokClient = tikTokClient;
    }

    @Override
    public void publishVideo(String videoUrl) {
        System.out.println(tiktokClient.getAccessToken());
        System.out.println("Publishing video to Tiktok: " + videoUrl);
    }
}

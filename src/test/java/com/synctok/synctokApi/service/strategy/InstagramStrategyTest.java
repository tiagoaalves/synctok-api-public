package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class InstagramStrategyTest {

    @Mock
    private InstagramClient instagramClient;

    private InstagramStrategy instagramStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instagramStrategy = new InstagramStrategy(instagramClient);
    }

    @Test
    void publishVideo_SuccessfulPublish() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";

        instagramStrategy.setVideoUrl(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl, title)).thenReturn(mediaContainerId);

        assertDoesNotThrow(() -> instagramStrategy.publishVideo(title));

        verify(instagramClient).createMediaContainer(videoUrl, title);
        verify(instagramClient).publishMedia(mediaContainerId);
    }

    @Test
    void publishVideo_MediaContainerCreationFails() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        RuntimeException expectedException = new RuntimeException("Creation failed");

        instagramStrategy.setVideoUrl(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl, title)).thenThrow(expectedException);

        assertThrows(RuntimeException.class, () -> instagramStrategy.publishVideo(title));

        verify(instagramClient).createMediaContainer(videoUrl, title);
        verify(instagramClient, never()).publishMedia(anyString());
    }

    @Test
    void publishVideo_PublishMediaFails() {
        String title = "title";
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";
        RuntimeException expectedException = new RuntimeException("Publish failed");

        instagramStrategy.setVideoUrl(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl, title)).thenReturn(mediaContainerId);
        doThrow(expectedException).when(instagramClient).publishMedia(mediaContainerId);

        assertThrows(RuntimeException.class, () -> instagramStrategy.publishVideo(title));

        verify(instagramClient).createMediaContainer(videoUrl, title);
        verify(instagramClient).publishMedia(mediaContainerId);
    }

    @Test
    void setVideoUrl_SuccessfulSet() {
        String videoUrl = "http://example.com/video.mp4";
        assertDoesNotThrow(() -> instagramStrategy.setVideoUrl(videoUrl));
    }
}

package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.InstagramClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

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

    @Mock
    private MultipartFile mockMultipartFile;

    private InstagramStrategy instagramStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instagramStrategy = new InstagramStrategy(instagramClient);
    }

    @Test
    void publishVideo_SuccessfulPublish() {
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";
        when(instagramClient.createMediaContainer(videoUrl)).thenReturn(mediaContainerId);

        assertDoesNotThrow(() -> instagramStrategy.publishVideo(mockMultipartFile, videoUrl));

        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient).publishMedia(mediaContainerId);
    }

    @Test
    void publishVideo_MediaContainerCreationFails() {
        String videoUrl = "http://example.com/video.mp4";
        RuntimeException expectedException = new RuntimeException("Creation failed");
        when(instagramClient.createMediaContainer(videoUrl)).thenThrow(expectedException);

        assertThrows(RuntimeException.class,
                () -> instagramStrategy.publishVideo(mockMultipartFile, videoUrl));

        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient, never()).publishMedia(anyString());
    }

    @Test
    void publishVideo_PublishMediaFails() {
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";
        RuntimeException expectedException = new RuntimeException("Publish failed");
        when(instagramClient.createMediaContainer(videoUrl)).thenReturn(mediaContainerId);
        doThrow(expectedException).when(instagramClient).publishMedia(mediaContainerId);

        assertThrows(RuntimeException.class,
                () -> instagramStrategy.publishVideo(mockMultipartFile, videoUrl));

        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient).publishMedia(mediaContainerId);
    }
}

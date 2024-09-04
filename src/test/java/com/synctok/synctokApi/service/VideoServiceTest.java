package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.client.InstagramClient;
import com.synctok.synctokApi.exception.MediaContainerCreationException;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.InstagramStrategy;
import com.synctok.synctokApi.service.strategy.PlatformStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VideoServiceTest {

    @Mock
    private CloudinaryClient cloudinaryClient;

    @Mock
    private InstagramClient instagramClient;

    @Mock
    private MultipartFile videoFile;

    private InstagramStrategy instagramStrategy;
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instagramStrategy = new InstagramStrategy(instagramClient);
        List<PlatformStrategy> strategies = Arrays.asList(instagramStrategy);
        videoService = new VideoService(strategies, cloudinaryClient);
    }

    @Test
    void publishVideo_SuccessfulPublish() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";
        List<String> platforms = Arrays.asList("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl)).thenReturn(mediaContainerId);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient).publishMedia(mediaContainerId);
    }

    @Test
    void publishVideo_UnsupportedPlatform() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = Arrays.asList("unsupported");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        UnsupportedPlatformException exception = assertThrows(UnsupportedPlatformException.class,
                () -> videoService.publishVideo(videoFile, platforms));

        assertEquals("unsupported", exception.getPlatform());
        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramClient, never()).createMediaContainer(anyString());
        verify(instagramClient, never()).publishMedia(anyString());
    }

    @Test
    void publishVideo_CloudinaryClientThrowsIOException() throws IOException {
        List<String> platforms = Arrays.asList("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramClient, never()).createMediaContainer(anyString());
        verify(instagramClient, never()).publishMedia(anyString());
    }

    @Test
    void publishVideo_MediaContainerCreationFails() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = Arrays.asList("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl)).thenThrow(new RuntimeException("Creation failed"));

        assertThrows(MediaContainerCreationException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient, never()).publishMedia(anyString());
    }

    @Test
    void publishVideo_CaseInsensitivePlatformNames() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        String mediaContainerId = "media123";
        List<String> platforms = Arrays.asList("InStAgRaM");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);
        when(instagramClient.createMediaContainer(videoUrl)).thenReturn(mediaContainerId);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramClient).createMediaContainer(videoUrl);
        verify(instagramClient).publishMedia(mediaContainerId);
    }
}

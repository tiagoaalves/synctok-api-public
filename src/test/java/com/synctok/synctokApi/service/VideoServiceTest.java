package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.InstagramStrategy;
import com.synctok.synctokApi.service.strategy.PlatformStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class VideoServiceTest {

    @Mock
    private CloudinaryClient cloudinaryClient;

    @Mock
    private InstagramStrategy instagramStrategy;

    @Mock
    private MultipartFile videoFile;

    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<PlatformStrategy> strategies = List.of(instagramStrategy);
        videoService = new VideoService(strategies, cloudinaryClient);
    }

    @Test
    void publishVideo_SuccessfulPublish() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy).publishVideo(videoFile, videoUrl);
    }

    @Test
    void publishVideo_UnsupportedPlatform() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("unsupported");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertThrows(UnsupportedPlatformException.class,
                () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy, never()).publishVideo(any(), anyString());
    }

    @Test
    void publishVideo_CloudinaryClientThrowsIOException() throws IOException {
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy, never()).publishVideo(any(), anyString());
    }

    @Test
    void publishVideo_StrategyThrowsException() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);
        doThrow(new RuntimeException("Publish failed")).when(instagramStrategy).publishVideo(videoFile, videoUrl);

        assertThrows(RuntimeException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy).publishVideo(videoFile, videoUrl);
    }

    @Test
    void publishVideo_CaseInsensitivePlatformNames() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("InStAgRaM");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy).publishVideo(videoFile, videoUrl);
    }
}

package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.InstagramStrategy;
import com.synctok.synctokApi.service.strategy.TiktokStrategy;
import com.synctok.synctokApi.service.strategy.UrlPlatformStrategy;
import com.synctok.synctokApi.service.strategy.FilePlatformStrategy;
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
    private TiktokStrategy tiktokStrategy;

    @Mock
    private MultipartFile videoFile;

    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        List<PlatformStrategy> strategies = List.of(instagramStrategy, tiktokStrategy);
        videoService = new VideoService(strategies, cloudinaryClient);
    }

    @Test
    void publishVideo_SuccessfulPublishUrlStrategy() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
        verify(tiktokStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_SuccessfulPublishFileStrategy() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("tiktok");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((FilePlatformStrategy)tiktokStrategy).setVideoFile(videoFile);
        verify(tiktokStrategy).publishVideo();
        verify(instagramStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_UnsupportedPlatform() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("unsupported");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertThrows(UnsupportedPlatformException.class,
                () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy, never()).publishVideo();
        verify(tiktokStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_CloudinaryClientThrowsIOException() throws IOException {
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenThrow(new IOException("Upload failed"));

        assertThrows(IOException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy, never()).publishVideo();
        verify(tiktokStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_StrategyThrowsException() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);
        doThrow(new RuntimeException("Publish failed")).when(instagramStrategy).publishVideo();

        assertThrows(RuntimeException.class, () -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
    }

    @Test
    void publishVideo_CaseInsensitivePlatformNames() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("InStAgRaM");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
    }

    @Test
    void publishVideo_MultipleStrategies() throws IOException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram", "tiktok");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        assertDoesNotThrow(() -> videoService.publishVideo(videoFile, platforms));

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
        verify((FilePlatformStrategy)tiktokStrategy).setVideoFile(videoFile);
        verify(tiktokStrategy).publishVideo();
    }
}

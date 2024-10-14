package com.synctok.synctokApi.service;

import com.synctok.synctokApi.client.CloudinaryClient;
import com.synctok.synctokApi.exception.UnsupportedPlatformException;
import com.synctok.synctokApi.service.strategy.FilePlatformStrategy;
import com.synctok.synctokApi.service.strategy.InstagramStrategy;
import com.synctok.synctokApi.service.strategy.PlatformStrategy;
import com.synctok.synctokApi.service.strategy.TiktokStrategy;
import com.synctok.synctokApi.service.strategy.UrlPlatformStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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
    void publishVideo_SuccessfulPublishUrlStrategy() throws IOException, ExecutionException, InterruptedException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        assertDoesNotThrow(() -> result.get());

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
        verify(tiktokStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_SuccessfulPublishFileStrategy() throws IOException, ExecutionException, InterruptedException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("tiktok");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        assertDoesNotThrow(() -> result.get());

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

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertInstanceOf(UnsupportedPlatformException.class, exception.getCause());

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify(instagramStrategy, never()).publishVideo();
        verify(tiktokStrategy, never()).publishVideo();
    }

    @Test
    void publishVideo_CloudinaryClientThrowsIOException() throws Exception {
        List<String> platforms = List.of("instagram");
        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenThrow(new IOException("Upload failed"));

        IOException exception = assertThrows(IOException.class, () -> videoService.publishVideo(videoFile, platforms));

        assertEquals("Upload failed", exception.getMessage());
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

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        ExecutionException exception = assertThrows(ExecutionException.class, result::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
    }

    @Test
    void publishVideo_CaseInsensitivePlatformNames() throws IOException, ExecutionException, InterruptedException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("InStAgRaM");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        assertDoesNotThrow(() -> result.get());

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
    }

    @Test
    void publishVideo_MultipleStrategies() throws IOException, ExecutionException, InterruptedException {
        String videoUrl = "http://example.com/video.mp4";
        List<String> platforms = List.of("instagram", "tiktok");

        when(cloudinaryClient.uploadAndGetPublicUrl(videoFile)).thenReturn(videoUrl);

        CompletableFuture<Void> result = videoService.publishVideo(videoFile, platforms);

        assertDoesNotThrow(() -> result.get());

        verify(cloudinaryClient).uploadAndGetPublicUrl(videoFile);
        verify((UrlPlatformStrategy)instagramStrategy).setVideoUrl(videoUrl);
        verify(instagramStrategy).publishVideo();
        verify((FilePlatformStrategy)tiktokStrategy).setVideoFile(videoFile);
        verify(tiktokStrategy).publishVideo();
    }
}

package com.synctok.synctokApi.service.strategy;

import com.synctok.synctokApi.client.YoutubeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class YoutubeStrategyTest {

    @Mock
    private YoutubeClient youtubeClient;

    @Mock
    private MultipartFile videoFile;

    private YoutubeStrategy youtubeStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        youtubeStrategy = new YoutubeStrategy(youtubeClient);
        youtubeStrategy.setVideoFile(videoFile);
    }

    @Test
    void publishVideo_ShouldUploadVideoSuccessfully() {
        when(youtubeClient.uploadVideo(videoFile, "title", "description")).thenReturn("video-id");

        assertDoesNotThrow(() -> youtubeStrategy.publishVideo());
        verify(youtubeClient).uploadVideo(videoFile, "title", "description");
    }

    @Test
    void publishVideo_ShouldThrowExceptionWhenUploadFails() {
        when(youtubeClient.uploadVideo(videoFile, "title", "description"))
                .thenThrow(new RuntimeException("Upload failed"));

        Exception exception = assertThrows(RuntimeException.class, () -> youtubeStrategy.publishVideo());
        assertEquals("Upload failed", exception.getMessage());
        verify(youtubeClient).uploadVideo(videoFile, "title", "description");
    }

    @Test
    void setVideoFile_ShouldSetVideoFile() {
        MultipartFile newVideoFile = mock(MultipartFile.class);
        youtubeStrategy.setVideoFile(newVideoFile);

        when(youtubeClient.uploadVideo(newVideoFile, "title", "description")).thenReturn("new-video-id");

        assertDoesNotThrow(() -> youtubeStrategy.publishVideo());
        verify(youtubeClient).uploadVideo(newVideoFile, "title", "description");
    }
}

package com.synctok.synctokApi.controller;

import com.synctok.synctokApi.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    public void testPublishVideo_Success() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile("video", "test.mp4", "video/mp4", "test video content".getBytes());

        when(videoService.publishVideo(any(), any(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/video/publish")
                        .file(videoFile)
                        .param("platforms", "instagram,facebook")
                        .param("title", "title"))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("Video successfully uploaded and published to instagram, facebook"));
    }

    @Test
    public void testPublishVideo_Error() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile("video", "test.mp4", "video/mp4", "test video content".getBytes());

        when(videoService.publishVideo(any(), any(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Publishing failed")));

        MvcResult mvcResult = mockMvc.perform(multipart("/api/v1/video/publish")
                        .file(videoFile)
                        .param("platforms", "instagram,facebook")
                        .param("title", "title"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while publishing the video: java.lang.RuntimeException: Publishing failed"));
    }
}

package com.synctok.synctokApi.controller;

import com.synctok.synctokApi.service.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    public void testPublishVideo() throws Exception {
        MockMultipartFile videoFile = new MockMultipartFile("video", "test.mp4", "video/mp4", "test video content".getBytes());

        doNothing().when(videoService).publishVideo(any(), any());

        mockMvc.perform(multipart("/api/v1/video/publish")
                        .file(videoFile)
                        .param("platforms", "instagram,facebook"))
                .andExpect(status().isOk())
                .andExpect(content().string("Video successfully uploaded and published to instagram, facebook"));
    }
}

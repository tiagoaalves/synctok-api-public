package com.synctok.synctokApi.controller;

import com.synctok.synctokApi.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/video")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishVideo(
            @RequestParam("video") MultipartFile video,
            @RequestParam("platforms") List<String> platforms) {

        try {
            videoService.publishVideo(video, platforms);
            return ResponseEntity.ok("Video successfully uploaded and published to " + String.join(", ", platforms));
//        } catch (UnsupportedPlatformException e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }
}

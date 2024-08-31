package com.synctok.synctokApi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoController {

    @PostMapping("/publish")
    public String publishVideo() {
       return "Video Published!";
    }

}

package com.synctok.synctokApi.exception;

public class MediaContainerCreationException extends RuntimeException {
  private final String videoUrl;

  public MediaContainerCreationException(String message, Throwable cause, String videoUrl) {
    super(message, cause);
    this.videoUrl = videoUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }
}

package com.synctok.synctokApi.exception;

/**
 * Exception thrown when there's an error during the creation of a media container.
 */
public class MediaContainerCreationException extends RuntimeException {
  private final String videoUrl;

  /**
   * Constructs a new MediaContainerCreationException with the specified detail message, cause, and video URL.
   *
   * @param message the detail message (which is saved for later retrieval by the getMessage() method)
   * @param cause the cause (which is saved for later retrieval by the getCause() method)
   * @param videoUrl the URL of the video for which the media container creation failed
   */
  public MediaContainerCreationException(String message, Throwable cause, String videoUrl) {
    super(message, cause);
    this.videoUrl = videoUrl;
  }

  /**
   * Returns the video URL associated with this exception.
   *
   * @return the URL of the video for which the media container creation failed
   */
  public String getVideoUrl() {
    return videoUrl;
  }
}

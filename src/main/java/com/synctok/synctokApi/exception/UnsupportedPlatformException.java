package com.synctok.synctokApi.exception;

public class UnsupportedPlatformException extends RuntimeException {
    private final String platform;

    public UnsupportedPlatformException(String platform) {
        super("Unsupported platform: " + platform);
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "UnsupportedPlatformException{" +
                "platform='" + platform + '\'' +
                '}';
    }
}

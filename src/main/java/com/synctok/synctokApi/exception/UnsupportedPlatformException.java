package com.synctok.synctokApi.exception;

/**
 * Exception thrown when an unsupported platform is encountered.
 */
public class UnsupportedPlatformException extends RuntimeException {
    private final String platform;

    /**
     * Constructs a new UnsupportedPlatformException with the specified platform.
     *
     * @param platform the name of the unsupported platform
     */
    public UnsupportedPlatformException(String platform) {
        super("Unsupported platform: " + platform);
        this.platform = platform;
    }

    /**
     * Returns the name of the unsupported platform.
     *
     * @return the name of the unsupported platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Returns a string representation of this exception.
     *
     * @return a string representation of this exception
     */
    @Override
    public String toString() {
        return "UnsupportedPlatformException{"
                + "platform='" + platform + '\''
                + '}';
    }
}

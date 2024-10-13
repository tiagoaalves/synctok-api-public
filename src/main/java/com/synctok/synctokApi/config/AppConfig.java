package com.synctok.synctokApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for application-wide beans.
 * This class provides configuration for common beans used across the application.
 */
@Configuration
public class AppConfig {

    /**
     * Creates and configures a RestTemplate bean.
     * This bean can be used for making HTTP requests to external services.
     *
     * @return A new instance of RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

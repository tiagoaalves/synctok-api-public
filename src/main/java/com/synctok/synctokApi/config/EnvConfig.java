package com.synctok.synctokApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

/**
 * Configuration class for environment setup.
 * This class provides configuration for reading environment variables from a .env file.
 */
@Configuration
public class EnvConfig {

    /**
     * Creates and configures a PropertySourcesPlaceholderConfigurer bean.
     * This bean is responsible for resolving placeholders in bean definitions using properties from the .env file.
     *
     * @return A configured PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(".env"));
        configurer.setIgnoreResourceNotFound(true);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        return configurer;
    }
}

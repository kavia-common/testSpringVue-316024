package com.upiara.poc;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Centralized CORS configuration for the application.
 *
 * Keep this as the single source of truth (do not add @CrossOrigin on controllers).
 *
 * Configuration:
 * - Dev profile: defaults to allow the Vue dev server origin (http://localhost:8081),
 *   but can be overridden using spring.cors.allowed-origins.
 * - Prod profile: MUST be explicitly configured via spring.cors.allowed-origins
 *   (no wide-open defaults).
 */
@Configuration
public class CorsConfiguration {

    /**
     * Comma-separated list of allowed origins.
     * Example:
     *   spring.cors.allowed-origins=http://localhost:8081,https://app.example.com
     */
    @Value("${spring.cors.allowed-origins:}")
    private String allowedOrigins;

    /**
     * Comma-separated list of allowed HTTP methods.
     * Sensible default for a REST API.
     */
    @Value("${spring.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    /**
     * Comma-separated list of allowed headers.
     * Default is permissive for typical browser + JSON + auth header use-cases.
     */
    @Value("${spring.cors.allowed-headers:Authorization,Content-Type,Accept,Origin,X-Requested-With}")
    private String allowedHeaders;

    @Value("${spring.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${spring.cors.max-age:3600}")
    private long maxAge;

    @Bean
    @Profile("dev")
    public WebMvcConfigurer corsConfigurerDev() {
        // Dev default: allow Vue CLI dev server origin.
        // If spring.cors.allowed-origins is set, it overrides this default.
        final List<String> origins = parseAllowedOriginsOrDefault("http://localhost:8081");

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(origins.toArray(new String[0]))
                        .allowedMethods(splitCsv(allowedMethods))
                        .allowedHeaders(splitCsv(allowedHeaders))
                        .allowCredentials(allowCredentials)
                        .maxAge(maxAge);
            }
        };
    }

    @Bean
    @Profile("prod")
    public WebMvcConfigurer corsConfigurerProd() {
        // Prod: MUST be explicitly configured; fail safe by allowing none if missing.
        final List<String> origins = parseAllowedOriginsOrDefault();

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // If origins list is empty, this effectively blocks cross-origin requests (safe default).
                registry.addMapping("/**")
                        .allowedOrigins(origins.toArray(new String[0]))
                        .allowedMethods(splitCsv(allowedMethods))
                        .allowedHeaders(splitCsv(allowedHeaders))
                        .allowCredentials(allowCredentials)
                        .maxAge(maxAge);
            }
        };
    }

    private List<String> parseAllowedOriginsOrDefault(String... defaults) {
        if (StringUtils.hasText(allowedOrigins)) {
            return Arrays.asList(splitCsv(allowedOrigins));
        }
        return Arrays.asList(defaults);
    }

    private static String[] splitCsv(String value) {
        if (!StringUtils.hasText(value)) {
            return new String[0];
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }
}
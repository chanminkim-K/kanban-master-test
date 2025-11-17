package com.kanban.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 *
 * This configuration class sets up the security settings for the Kanban Board application.
 * It configures authentication, authorization, CORS, and session management.
 *
 * Current Configuration:
 * - All /api/** endpoints are permitted without authentication (for initial development)
 * - H2 console access is enabled for development
 * - CORS is configured to allow frontend requests
 * - Stateless session management (JWT-ready)
 * - BCrypt password encoding
 *
 * Note: This is a basic configuration for development. Additional security features
 * (JWT authentication, role-based access control) will be added in subsequent iterations.
 *
 * @author Megazone Cloud Internship
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * Security Rules:
     * - /api/** : All API endpoints are currently permitted (will be secured with JWT later)
     * - /h2-console/** : H2 database console access (development only)
     * - CSRF protection is disabled (using JWT for stateless authentication)
     * - Session management is stateless (JWT-based)
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF configuration - disabled for stateless API
                .csrf(csrf -> csrf.disable())

                // CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all API endpoints (temporary for development)
                        .requestMatchers("/api/**").permitAll()

                        // Allow H2 console access (development only)
                        .requestMatchers("/h2-console/**").permitAll()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Session management - stateless for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // H2 console configuration (allow frames for H2 console)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     * BCrypt is a strong, adaptive hashing function designed for password storage.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS configuration to allow requests from the frontend application.
     *
     * Allowed Origins: http://localhost:3000 (React development server)
     * Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
     * Allowed Headers: All headers
     * Allow Credentials: true (for cookies/auth headers)
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

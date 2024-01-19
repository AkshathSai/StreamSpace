package com.akshathsaipittala.streamspace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    //@Profile("dev")
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatcher(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .authorizeHttpRequests( auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(
                        requests -> requests
                        .requestMatchers(
                                "/",
                                "/webjars/**",
                                "/search/**",
                                "/api/**",
                                "/swagger-ui/**",
                                "/local/media/**",
                                "/stream/**",
                                "/movies/**",
                                "/music/**",
                                "/yts/**",
                                "/yt/**",
                                "/download/**",
                                "/download-progress/**"
                                )
                                .permitAll()
                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}

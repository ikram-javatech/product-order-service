package com.example.config;

import com.example.filter.RequestResponseLoggingFilter;
import com.example.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RequestResponseLoggingFilter loggingFilter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/**",
                                "/h2-console/**",
                                "/h2-console/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/products/**")
                        .hasAnyAuthority("USER", "PREMIUM_USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/products/**")
                        .hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/products/**")
                        .hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/products/**")
                        .hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/orders/**")
                        .hasAnyAuthority("USER", "PREMIUM_USER")

                        .requestMatchers(HttpMethod.GET, "/api/orders")
                        .hasAuthority("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/orders/*")
                        .hasAnyAuthority("USER", "PREMIUM_USER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(loggingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}

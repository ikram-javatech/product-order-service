package com.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final String MASK = "*****";

    private String maskSensitive(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        // Mask common sensitive JSON fields (best-effort, minimal overhead)
        String masked = body;
        masked = masked.replaceAll("(?i)(\\\"password\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\\\"" + MASK + "\\\"");
        masked = masked.replaceAll("(?i)(\\\"token\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\\\"" + MASK + "\\\"");
        masked = masked.replaceAll("(?i)(\\\"secret\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\\\"" + MASK + "\\\"");
        return masked;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(req, res);
        } finally {
            String reqBody = new String(req.getContentAsByteArray(), StandardCharsets.UTF_8);
            String resBody = new String(res.getContentAsByteArray(), StandardCharsets.UTF_8);

            log.info("REQ {} {} body={}", request.getMethod(), request.getRequestURI(), maskSensitive(reqBody));
            log.info("RES {} durationMs={} body={}", res.getStatus(), (System.currentTimeMillis() - start),
                    maskSensitive(resBody));

            res.copyBodyToResponse();
        }
    }
}

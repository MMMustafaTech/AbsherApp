package com.absher.absherapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Small single-node limiter for public credential and OTP endpoints. */
@Component
public class PublicEndpointRateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int loginLimit;
    private final int otpRequestLimit;
    private final int otpVerificationLimit;
    private final int refreshLimit;
    private final long windowMillis;

    public PublicEndpointRateLimitFilter(
            @Value("${app.security.rate-limit.login.max-requests:5}") int loginLimit,
            @Value("${app.security.rate-limit.otp-request.max-requests:3}") int otpRequestLimit,
            @Value("${app.security.rate-limit.otp-verification.max-requests:10}") int otpVerificationLimit,
            @Value("${app.security.rate-limit.refresh.max-requests:10}") int refreshLimit,
            @Value("${app.security.rate-limit.window:PT5M}") Duration window
    ) {
        this.loginLimit = loginLimit;
        this.otpRequestLimit = otpRequestLimit;
        this.otpVerificationLimit = otpVerificationLimit;
        this.refreshLimit = refreshLimit;
        this.windowMillis = window.toMillis();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod()) || limitFor(request.getRequestURI()) == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Integer limit = limitFor(request.getRequestURI());
        long now = System.currentTimeMillis();
        String key = request.getRequestURI() + ':' + request.getRemoteAddr();
        Window window = windows.compute(key, (ignored, previous) -> {
            if (previous == null || previous.resetAtMillis <= now) {
                return new Window(1, now + windowMillis);
            }
            return previous.incremented();
        });
        if (window.requestCount > limit) {
            long retryAfterSeconds = Math.max(1, (window.resetAtMillis - now + 999) / 1000);
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Integer limitFor(String path) {
        return switch (path) {
            case "/api/v1/auth/login" -> loginLimit;
            case "/api/v1/auth/refresh" -> refreshLimit;
            case "/auth/enrollment/otp" -> otpRequestLimit;
            case "/auth/enrollment/otp/verify" -> otpVerificationLimit;
            default -> null;
        };
    }

    private record Window(int requestCount, long resetAtMillis) {
        private Window incremented() {
            return new Window(requestCount + 1, resetAtMillis);
        }
    }
}

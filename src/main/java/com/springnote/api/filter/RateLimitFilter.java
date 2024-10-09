package com.springnote.api.filter;


import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final String bucketName;
    private final Integer restTime;
    private final Integer maxCount;
    private final Integer disadvantageWrite;
    private final Integer disadvantageRead;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, ServletException, IOException {
        var httpRequest = (HttpServletRequest) request;

        if (Objects.equals(httpRequest.getMethod(), "OPTIONS") || Objects.equals(httpRequest.getMethod(), "GET")) {
            chain.doFilter(request, response);
            return;
        }

        var isWrite = Objects.equals(httpRequest.getMethod(), "POST") || Objects.equals(httpRequest.getMethod(), "PUT") || Objects.equals(httpRequest.getMethod(), "DELETE");

        var httpResponse = (HttpServletResponse) response;
        var cookie = httpRequest.getCookies();

        if (cookie == null) {
            setBucketCookie(httpResponse, bucketName, getTokenAmount(isWrite), restTime);
            chain.doFilter(request, response);
            return;
        }

        var bucket = Arrays.stream(cookie).filter(c -> c.getName().equals(bucketName)).findFirst();

        if (bucket.isEmpty()) {
            setBucketCookie(httpResponse, bucketName, getTokenAmount(isWrite), restTime);
        } else {
            var bucketValue = Integer.parseInt(bucket.get().getValue());

            if (bucketValue >= maxCount) {
                httpResponse.setStatus(429);
                log.debug("Rate limit exceeded");
                return;
            }
            setBucketCookie(httpResponse, bucketName, String.valueOf(bucketValue + Integer.parseInt(getTokenAmount(isWrite))), bucket.get().getMaxAge());
        }

        chain.doFilter(request, response);
    }

    private void setBucketCookie(HttpServletResponse response, String bucketName, String value, Integer age) {
        var cookie = new Cookie(bucketName, value);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    private String getTokenAmount(boolean isWrite) {
        return String.valueOf(isWrite ? disadvantageWrite : disadvantageRead);

    }
}

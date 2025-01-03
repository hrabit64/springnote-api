package com.springnote.api.filter;


import com.springnote.api.utils.context.RequestContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class SetupRequestContextFilter implements Filter {

    private final RequestContext requestContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        requestContext.setId(UUID.randomUUID().toString());
        requestContext.setIp(getIp(httpRequest));
        chain.doFilter(request, response);

    }
//
//    private String getIp(HttpServletRequest httpRequest) {
//        var ip = httpRequest.getHeader("X-Forwarded-For");
//
//        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
//            // X-Forwarded-For 헤더에서 첫 번째 IP 주소 추출
//            ip = ip.split(",")[0].trim();
//        } else {
//            ip = httpRequest.getHeader("Proxy-Client-IP");
//
//            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//                ip = httpRequest.getHeader("WL-Proxy-Client-IP");
//            }
//
//            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//                ip = httpRequest.getHeader("HTTP_CLIENT_IP");
//            }
//
//            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//                ip = httpRequest.getHeader("HTTP_X_FORWARDED_FOR");
//            }
//
//            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//                ip = httpRequest.getRemoteAddr();
//            }
//        }
//        return ip;
//    }

    private String getIp(HttpServletRequest httpRequest) {
        return httpRequest.getRemoteAddr();
    }
}

//package com.springnote.api.filter;
//
//import com.springnote.api.config.AuthConfig;
//import com.springnote.api.security.auth.FirebaseAuthManager;
//import com.springnote.api.utils.context.UserContext;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.Objects;
//
//@Slf4j
//@RequiredArgsConstructor
//public class AuthenticationFilter implements Filter {
//
//    private final FirebaseAuthManager firebaseAuthManager;
//    private final AuthConfig authConfig;
//    private final UserContext userContext;
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, ServletException, IOException {
//        var httpRequest = (HttpServletRequest) request;
//
//        if (Objects.equals(httpRequest.getMethod(), "OPTIONS")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        var tokenHeader = httpRequest.getHeader("Authorization");
//
//        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
//            var token = tokenHeader.substring(7);
//            authenticate(token);
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    private void authenticate(String token) {
//        var user = firebaseAuthManager.authenticate(token);
//
//        if (user == null) {
//            return;
//        }
//
//        userContext.init(user);
//        log.debug("User authenticated: {}", user.getUid());
//
//        if (user.getUid().equals(authConfig.getAdminUid())) {
//            userContext.setAdmin(true);
//            log.debug("Admin authenticated: {}", user.getUid());
//        }
//    }
//}

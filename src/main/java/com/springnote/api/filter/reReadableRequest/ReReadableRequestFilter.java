package com.springnote.api.filter.reReadableRequest;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class ReReadableRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var reReadableRequestWrapper = new CustomRequestWrapper((HttpServletRequest) request);
        chain.doFilter(reReadableRequestWrapper, response);
    }
}


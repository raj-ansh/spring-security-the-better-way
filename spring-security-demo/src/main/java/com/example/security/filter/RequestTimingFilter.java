package com.example.security.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTimingFilter implements Filter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestTimingFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. BEFORE: runs before the rest of the application
        long start = System.currentTimeMillis();
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());

        // 2. Pass the request on to the next filter (and finally the controller)
        chain.doFilter(request, response);

        // 3. AFTER: runs after the rest of the application has finished
        long time = System.currentTimeMillis() - start;
        log.info("<-- {} {} returned {} in {} ms",
                request.getMethod(), request.getRequestURI(),
                response.getStatus(), time);
    }
}

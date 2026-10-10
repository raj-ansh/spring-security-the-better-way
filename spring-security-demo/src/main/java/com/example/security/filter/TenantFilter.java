package com.example.security.filter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TenantFilter extends OncePerRequestFilter {

    // Which user may work with which tenant.
    // In a real application this information would come from a database.
    private static final Map<String, Set<String>> TENANTS_BY_USER = Map.of(
            "alice", Set.of("acme"),
            "mike", Set.of("acme", "globex"),
            "admin", Set.of("acme", "globex", "initech")
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only check requests that work with tenant data
        return !request.getRequestURI().startsWith("/api/tenants/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String tenantId = request.getHeader("X-Tenant-Id");     // 1. read the header
        boolean hasAccess = isUserAllowed(tenantId);             // 2. check access

        if (hasAccess) {
            filterChain.doFilter(request, response);             // 3. allowed: continue
            return;
        }
        throw new AccessDeniedException(                         // 4. not allowed
                "Access denied to tenant: " + tenantId);
    }

    private boolean isUserAllowed(String tenantId) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (tenantId == null || authentication == null) {
            return false;
        }
        Set<String> allowedTenants =
                TENANTS_BY_USER.getOrDefault(authentication.getName(), Set.of());
        return allowedTenants.contains(tenantId);
    }
}

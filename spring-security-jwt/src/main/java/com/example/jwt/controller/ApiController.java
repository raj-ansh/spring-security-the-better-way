package com.example.jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    // Any logged-in user
    @GetMapping("/products")
    public List<String> products() {
        return List.of("Laptop", "Keyboard", "Mouse");
    }

    // Any logged-in user: shows what is inside their token
    @GetMapping("/user/profile")
    public Map<String, Object> profile(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "username", jwt.getSubject(),
                "roles", jwt.getClaimAsStringList("roles"),
                "expiresAt", jwt.getExpiresAt().toString()
        );
    }

    // ADMIN only
    @GetMapping("/admin/users")
    public List<String> users() {
        return List.of("alice", "admin");
    }
}

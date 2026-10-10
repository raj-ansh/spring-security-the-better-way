package com.example.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public String home() {
        return "Welcome! This home page is public.";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        return "Dashboard of " + authentication.getName();
    }
}

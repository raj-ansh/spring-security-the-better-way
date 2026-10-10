package com.example.security.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WhoAmIController {

    @GetMapping("/api/whoami")
    public Map<String, Object> whoAmI(HttpServletRequest request) {
        return Map.of(
                "remoteUser", request.getRemoteUser(),
                "principal", request.getUserPrincipal().getName(),
                "isUserInRoleUSER", request.isUserInRole("USER"),
                "isUserInRoleADMIN", request.isUserInRole("ADMIN")
        );
    }
}

package com.example.security.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @GetMapping("/orders")
    public List<String> orders(@RequestHeader("X-Tenant-Id") String tenantId) {
        return List.of(tenantId + "-order-1001", tenantId + "-order-1002");
    }
}

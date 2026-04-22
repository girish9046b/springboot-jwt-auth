package com.example.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public String getUserProfile(Authentication authentication) {
        return "Welcome " + authentication.getName() + "! This is your user profile";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAdminPanel(Authentication authentication) {
        return "Welcome " + authentication.getName() + "! This is admin panel";
    }

    @GetMapping("/public")
    public String getPublicData() {
        return "This is public data accessible to all";
    }
}
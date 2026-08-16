package com.hospital.appointment_system.controllers;

import com.hospital.appointment_system.models.Role;
import com.hospital.appointment_system.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam Role role) {
        return ResponseEntity.ok(authService.register(email, password, role));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String email,
            @RequestParam String password) {
        return ResponseEntity.ok(authService.login(email, password));
    }
}

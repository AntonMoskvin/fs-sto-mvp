package com.sfsto.controller;

import com.sfsto.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    // Very small in-memory login for MVP
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        // simple check for MVP
        if (("user@sfsto.test".equals(email) && "password".equals(password)) ||
            ("admin@sfsto.test".equals(email) && "password".equals(password))) {
            String role = "CLIENT".equals(email) ? "CLIENT" : "STATION_ADMIN";
            String token = jwtUtil.generateToken(email, role);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of("user", authentication.getName(), "roles", authentication.getAuthorities()));
    }
}

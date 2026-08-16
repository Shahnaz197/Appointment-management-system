package com.hospital.appointment_system.services;

import com.hospital.appointment_system.models.Role;
import com.hospital.appointment_system.models.User;
import com.hospital.appointment_system.repositories.UserRepository;
import com.hospital.appointment_system.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service

public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, String> register(String email, String password, Role role) {
        // check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // create new user
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // encrypt password!
        user.setRole(role);

        userRepository.save(user);

        // generate token
        String token = jwtUtil.generateToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("role", role.toString());
        response.put("message", "registered successfully!");

        return response;
    }

    public Map<String, String> login(String email, String password) {
        // find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // generate token
        String token = jwtUtil.generateToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("email", email);
        response.put("role", user.getRole().toString());
        response.put("message", "logged in successfully!");

        return response;
    }
}

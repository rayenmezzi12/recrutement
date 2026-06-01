package com.recrutement.auth.controller;

import com.recrutement.auth.dto.AuthRequest;
import com.recrutement.auth.dto.AuthResponse;
import com.recrutement.auth.dto.RegisterRequest;
import com.recrutement.auth.dto.UserDto;
import com.recrutement.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(authService.getUserByUsername(username));
    }
}

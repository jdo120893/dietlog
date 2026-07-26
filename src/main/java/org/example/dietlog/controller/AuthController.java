package org.example.dietlog.controller;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.User;
import org.example.dietlog.security.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        User user = authService.signup(request.email(), request.password(), request.nickname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SignupResponse(user.getId(), user.getEmail(), user.getNickname()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    record SignupRequest(String email, String password, String nickname) {}
    record SignupResponse(Long id, String email, String nickname) {}
    record LoginRequest(String email, String password) {}
    record LoginResponse(String accessToken) {}
}
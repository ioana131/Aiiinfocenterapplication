package org.example.aiinfocenter.web;

import jakarta.validation.Valid;
import org.example.aiinfocenter.dto.LoginRequest;
import org.example.aiinfocenter.dto.RegisterRequest;
import org.example.aiinfocenter.model.User;
import org.example.aiinfocenter.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    public User register(@RequestBody @Valid RegisterRequest dto) {
        return auth.register(dto);
    }

    @PostMapping("/login")
    public User login(@RequestBody @Valid LoginRequest dto) {
        return auth.login(dto);
    }
}

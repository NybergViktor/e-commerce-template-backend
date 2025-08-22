package com.e_commerce.template.auth;

import com.e_commerce.template.auth.dto.AuthResponse;
import com.e_commerce.template.auth.dto.LoginRequest;
import com.e_commerce.template.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration}")
    private long expirationSec;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        String token = jwtService.generateToken(auth.getName());
        return AuthResponse.bearer(token, expirationSec);
    }
}

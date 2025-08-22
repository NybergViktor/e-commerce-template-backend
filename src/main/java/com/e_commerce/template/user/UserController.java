package com.e_commerce.template.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public Object me(@AuthenticationPrincipal UserDetails user) {
        // Simple payload for testing
        return java.util.Map.of(
                "username", user.getUsername(),
                "authorities", user.getAuthorities()
        );
    }
}

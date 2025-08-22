package com.e_commerce.template.admin;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/ping")
    public Object ping() {
        return java.util.Map.of("ok", true, "area", "admin");
    }
}

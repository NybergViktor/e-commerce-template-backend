package com.e_commerce.template.security;

import com.e_commerce.template.user.model.Role;
import com.e_commerce.template.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest extends com.e_commerce.template.testsupport.MongoContainerSupport {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    com.e_commerce.template.user.UserRepository users;
    @Autowired
    org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void seedUsers() {

        users.findByUsername("admin").orElseGet(() ->
                users.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Set.of(Role.ADMIN, Role.USER))
                        .enabled(true)
                        .build())
        );


        users.findByUsername("alice").orElseGet(() ->
                users.save(User.builder()
                        .username("alice")
                        .password(passwordEncoder.encode("password"))   // MUST be encoded
                        .roles(Set.of(Role.USER))
                        .enabled(true)
                        .build())
        );
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        var mvcResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())  // was 401 before seeding
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var body = mvcResult.getResponse().getContentAsString();
        var map = new com.fasterxml.jackson.databind.ObjectMapper().readValue(body, java.util.Map.class);
        return (String) map.get("accessToken");
    }

    @Test
    void user_can_access_me_but_gets_403_on_admin() throws Exception {
        String token = loginAndGetToken("alice", "password");
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
        mockMvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_can_access_admin_endpoint() throws Exception {
        String token = loginAndGetToken("admin", "admin123");
        mockMvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area").value("admin"));
    }

    @Test
    void missing_or_bad_token_yields_401() throws Exception {
        mockMvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users/me").header("Authorization", "Bearer not.a.valid.token"))
                .andExpect(status().isUnauthorized());
    }
}

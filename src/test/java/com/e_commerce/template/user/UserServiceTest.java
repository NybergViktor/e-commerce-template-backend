package com.e_commerce.template.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserServiceTest extends com.e_commerce.template.testsupport.MongoContainerSupport {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @org.junit.jupiter.api.BeforeEach
    void seed() {
        userRepository.findByUsername("admin").orElseGet(() ->
                userRepository.save(
                        com.e_commerce.template.user.model.User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin123"))
                                .roles(java.util.Set.of(com.e_commerce.template.user.model.Role.ADMIN,
                                        com.e_commerce.template.user.model.Role.USER))
                                .enabled(true)
                                .build()
                )
        );

        userRepository.findByUsername("alice").orElseGet(() ->
                userRepository.save(
                        com.e_commerce.template.user.model.User.builder()
                                .username("alice")
                                .password(passwordEncoder.encode("password"))
                                .roles(java.util.Set.of(com.e_commerce.template.user.model.Role.USER))
                                .enabled(true)
                                .build()
                )
        );
    }

    @Test
    void admin_and_alice_are_seeded_and_passwords_match() {
        var admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new AssertionError("Admin should be present"));
        var alice = userRepository.findByUsername("alice")
                .orElseThrow(() -> new AssertionError("Alice should be present"));

        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches("admin123", admin.getPassword())).isTrue();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches("password", alice.getPassword())).isTrue();
        org.assertj.core.api.Assertions.assertThat(admin.isEnabled()).isTrue();
        org.assertj.core.api.Assertions.assertThat(alice.isEnabled()).isTrue();
    }
}


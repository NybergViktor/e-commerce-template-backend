package com.e_commerce.template.user.model;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Email
    private String mail;

    private String firstName;

    private String lastName;

    private Set<Role> roles;
}

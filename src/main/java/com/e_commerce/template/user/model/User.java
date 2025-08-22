package com.e_commerce.template.user.model;

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
    private String id;               // <-- String (ObjectId som text)

    @Indexed(unique = true)
    private String username;

    private String password;

    private Set<Role> roles;

    private boolean enabled = true;  // Lombok -> isEnabled()
}

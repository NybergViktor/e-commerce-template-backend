package com.e_commerce.template.user.dto;

import com.e_commerce.template.user.model.Role;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String userId;
    private String mail;
    private String password;
    private String firstName;
    private String lastName;
}

package com.e_commerce.template.product.model;

import com.e_commerce.template.user.model.Role;
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
public class Product {
    @Id
    private String id;

    private String title;

    private String description;
    private double price;

    private boolean enabled = true;
}

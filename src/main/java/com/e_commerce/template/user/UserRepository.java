package com.e_commerce.template.user;

import com.e_commerce.template.user.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> { // <-- String
    Optional<User> findByUsername(String username);
}

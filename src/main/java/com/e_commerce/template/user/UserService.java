package com.e_commerce.template.user;

import com.e_commerce.template.common.exception.UserNotFoundException;
import com.e_commerce.template.product.model.Product;
import com.e_commerce.template.user.dto.UserCreateRequest;
import com.e_commerce.template.user.dto.UserUpdateRequest;
import com.e_commerce.template.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserCreateRequest> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserCreateRequest getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return convertToDto(user);
    }

    public UserCreateRequest registerUser(UserCreateRequest userDto) {
        User user = convertToEntity(userDto);
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);

    }

    public UserCreateRequest updateUser(UserUpdateRequest userDto) {
        User existingUser = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userDto.getUserId()));

        existingUser.setPassword(userDto.getPassword());
        existingUser.setMail(userDto.getMail());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());

        User updatedUser = userRepository.save(existingUser);
        return convertToDto(updatedUser);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserCreateRequest convertToDto(User user) {
        return UserCreateRequest.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .mail(user.getMail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .build();
    }

    private User convertToEntity(UserCreateRequest userDto) {
        return User.builder()
                .id(userDto.getUserId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .mail(userDto.getMail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .roles(userDto.getRoles())
                .build();
    }
}

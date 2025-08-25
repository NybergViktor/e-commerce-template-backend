package com.e_commerce.template.user;

import com.e_commerce.template.common.exception.UserNotFoundException;
import com.e_commerce.template.user.dto.UserCreateRequest;
import com.e_commerce.template.user.dto.UserUpdateRequest;
import com.e_commerce.template.user.model.Role;
import com.e_commerce.template.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id("1")
                .username("testuser1")
                .password("password123")
                .mail("test1@example.com")
                .firstName("Test")
                .lastName("UserOne")
                .roles(Set.of(Role.USER))
                .build();

        user2 = User.builder()
                .id("2")
                .username("testuser2")
                .password("password456")
                .mail("test2@example.com")
                .firstName("Test")
                .lastName("UserTwo")
                .roles(Set.of(Role.USER, Role.ADMIN))
                .build();
    }

    @Test
    void getAllUsers_shouldReturnListOfUserDtos() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserCreateRequest> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user1.getUsername(), result.get(0).getUsername());
        assertEquals(user2.getUsername(), result.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenNoUsers_shouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserCreateRequest> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserDto() {
        // Arrange
        when(userRepository.findById("1")).thenReturn(Optional.of(user1));

        // Act
        UserCreateRequest result = userService.getUserById("1");

        // Assert
        assertNotNull(result);
        assertEquals(user1.getId(), result.getUserId());
        assertEquals(user1.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById("1");
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
        // Arrange
        String userId = "nonexistent";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void registerUser_shouldSaveAndReturnUserDto() {
        // Arrange
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .password("password123")
                .mail("new@example.com")
                .firstName("New")
                .lastName("User")
                .roles(Set.of(Role.USER))
                .build();

        User savedUser = User.builder()
                .id("new-id")
                .username(request.getUsername())
                .password(request.getPassword())
                .mail(request.getMail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(request.getRoles())
                .build();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userArgumentCaptor.capture())).thenReturn(savedUser);

        // Act
        UserCreateRequest result = userService.registerUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getUserId());
        assertEquals(savedUser.getUsername(), result.getUsername());

        User capturedUser = userArgumentCaptor.getValue();
        assertNull(capturedUser.getId());
        assertEquals(request.getUsername(), capturedUser.getUsername());
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateAndReturnDto() {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .userId("1")
                .password("newpassword")
                .mail("new.mail@example.com")
                .firstName("NewFirst")
                .lastName("NewLast")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserCreateRequest result = userService.updateUser(updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updateRequest.getUserId(), result.getUserId());
        assertEquals(updateRequest.getMail(), result.getMail());
        assertEquals(updateRequest.getFirstName(), result.getFirstName());
        assertEquals(updateRequest.getLastName(), result.getLastName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).findById("1");
        verify(userRepository, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("newpassword", capturedUser.getPassword());
        assertEquals("new.mail@example.com", capturedUser.getMail());
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
        // Arrange
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().userId("nonexistent").build();
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(updateRequest));
        assertEquals("User not found with id: nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findById("nonexistent");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_whenUserExists_shouldCallDeleteById() {
        // Arrange
        String userId = "1";
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_whenUserDoesNotExist_shouldThrowUserNotFoundException() {
        // Arrange
        String userId = "nonexistent";
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}
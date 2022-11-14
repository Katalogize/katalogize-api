package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import graphql.GraphQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"test"})
class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UploadFileService uploadFileService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    User user;

    @BeforeEach
    void setUp() {
        when(uploadFileService.uploadFile(anyString(), anyString(), anyString())).thenReturn("File Name");
        when(uploadFileService.deleteFile(anyString())).thenReturn(true);
        doNothing().when(mock(EmailService.class)).sendRegistrationEmail(anyString(), anyString());
        doNothing().when(mock(EmailService.class)).sendRegistrationEmail("katalogize@gmail.com", "KatalogizeUser");
        when(emailService.isValidEmailAddress(anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        user = new User("Katalogize User","katalogize@gmail.com", "KatalogizeUser", "KatalogizeUser");
    }

    @Test
    void getAll() {
        assertEquals(userService.getAll().size(), 0);

        when(userRepository.findAll()).thenReturn(Arrays.asList(
                new User("Katalogize User1","katalogize1@email.com", "KatalogizeUser1", "KatalogizeUser1"),
                new User("Katalogize User2","katalogize2@email.com", "KatalogizeUser2", "KatalogizeUser2")
        ));
        assertEquals(userService.getAll().size(), 2);
    }

    @Test
    void getById() {
        assertNull(userService.getById(anyString()));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User response = userService.getById(user.getId());
        assertEquals(user, response);
    }

    @Test
    void getByUsername() {
        assertNull(userService.getById(anyString()));

        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User response = userService.getByUsername(user.getUsername());
        assertEquals(user, response);
    }


    @Test
    void getLoggedUser() {
        UserDetails userDetails = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User response = userService.getLoggedUser();
        assertEquals(user, response);
    }

    @Test
    void updateUserInfo() {
        UserDetails userDetails = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newUsername = "New Username";
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.getUserByUsername(newUsername)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        User response = userService.updateUsername(newUsername);
        assertEquals(newUsername, response.getUsername());
        String newDisplayName = "New DisplayName";
        response = userService.updateDisplayName(newDisplayName);
        assertEquals(newDisplayName, response.getDisplayName());

        when(userRepository.getUserByUsername("ExistUser")).thenReturn(Optional.of(user));
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.updateUsername("ExistUser");
                },
                "Expected updateUserName to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "Username already exists!");
    }

    @Test
    void updatePassword() {
        UserDetails userDetails = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(passwordEncoder.encode(anyString())).thenReturn("newPassword");
        user.setPassword(passwordEncoder.encode("KatalogizeUser"));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User response = userService.updatePassword("KatalogizeUser", "newPassword");
        assertEquals(response.getPassword(), "newPassword");

        when(passwordEncoder.encode(anyString())).thenReturn("wrongPassword");
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.updatePassword("wrongPassword","password");
                },
                "Expected updatePassword to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "Old password does not match.");
    }

    @Test
    void getLoggedUserThrowsException() {
        GraphQLException thrown = assertThrows(
            GraphQLException.class,
            () -> {
                userService.getLoggedUser();
            },
            "Expected getLoggedUser to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "User not logged in!");
    }

    @Test
    void deleteUserById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(mock(UserRepository.class)).deleteById(user.getId());
        User response = userService.deleteById(user.getId());
        assertEquals(user, response);
    }

    @Test
    void deleteUserByIdThrowsException() {
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.deleteById(anyString());
                },
                "Expected deleteUserById to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "User does not exist");
    }

    @Test
    void deleteUserPicture() {
        user.setPicture(anyString());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User response = userService.deleteById(user.getId());
        assertNull(response.getPicture());
    }

    @Test
    void addUserPicture() {
        user.setPicture("Test");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User response = userService.addUserPicture(user, "Test2");
        assertNotEquals(response.getPicture(), "Test");
    }

    @Test
    void logOut() {
        when(refreshTokenService.deleteByUserId(anyString())).thenReturn(anyString());
        String response = userService.logOut(user.getId());
        assertEquals(response,"Log out successful!");
    }

    @Test
    void logOutThrowsException() {
        when(refreshTokenService.deleteByUserId(anyString())).thenThrow(new GraphQLException("Exception"));
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.logOut(user.getId());
                },
                "Expected deleteUserById to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "User does not exist!");
    }

    @Test
    void signUpThrowsException() {
        when(userRepository.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.signUp(user);
                },
                "Expected signUp to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "Username is already in use!");

        when(userRepository.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        GraphQLException thrown2 = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.signUp(user);
                },
                "Expected signUp to throw, but it didn't"
        );

        assertEquals(thrown2.getMessage(), "Email is already in use!");

        when(emailService.isValidEmailAddress(anyString())).thenReturn(false);

        GraphQLException thrown3 = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.signUp(user);
                },
                "Expected signUp to throw, but it didn't"
        );

        assertEquals(thrown3.getMessage(), "Invalid email!");
    }

    @Test
    void signUp() {
        when(userRepository.save(user)).thenReturn(user);
        String response = userService.signUp(user);
        assertEquals(response, "User registered successfully!");
    }

    @Test
    void refreshTokenThrowsException() {
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.refreshToken(anyString());
                },
                "Expected refreshToken to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(),"Refresh token is not in database!");
    }

    @Test
    void signInThrowsException() {
        GraphQLException thrown = assertThrows(
                GraphQLException.class,
                () -> {
                    userService.signIn(user.getUsername(), user.getPassword());
                },
                "Expected signIn to throw, but it didn't"
        );
        assertEquals(thrown.getMessage(), "Invalid Credentials");
    }
}
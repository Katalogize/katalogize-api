package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.JwtResponse;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/User")
public class UserController {
    @Autowired
    UserService userService;

    @MutationMapping
    public JwtResponse signIn(@Argument String username, @Argument String password) {
        return userService.signIn(username, password);
    }

    @MutationMapping
    public JwtResponse refreshToken(@Argument String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    @MutationMapping
    public String signUp(@Argument User user) {
        return userService.signUp(user);
    }

    @MutationMapping
    public String logOut(@Argument String userId) {
        return userService.logOut(userId);
    }

    @MutationMapping
    public String forgotPassword(@Argument String email) {
        return userService.forgotPassword(email);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User addUserPicture(@Argument String encodedFile) {
        User user = userService.deleteUserPicture(userService.getLoggedUser());
        return userService.addUserPicture(user, encodedFile);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User deleteUserPicture() {
        User user = userService.getLoggedUser();
        userService.deleteUserPicture(user);
        return user;
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User updatePassword(@Argument String oldPassword, @Argument String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User updateUsername(@Argument String username) {
        return userService.updateUsername(username);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User updateDisplayNam(@Argument String displayName) {
        return userService.updateDisplayName(displayName);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public User deleteUser(@Argument String id) {
        return userService.deleteById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @QueryMapping
    public User getUserById(@Argument String id) {
        return  userService.getById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public User getLoggedUser() {
        return userService.getLoggedUser();
    }

    @QueryMapping
    public User getUserByUsername(@Argument String username) {
        User response =  userService.getByUsername(username);
        return response;
    }

    @SchemaMapping
    public User user(Catalog catalog) {
        return userService.getById(catalog.getUserId());
    }
}

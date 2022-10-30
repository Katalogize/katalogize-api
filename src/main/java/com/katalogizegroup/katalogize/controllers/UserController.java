package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.config.security.jwt.JwtTokenProvider;
import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.JwtResponse;
import com.katalogizegroup.katalogize.models.RefreshToken;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.RefreshTokenService;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import com.katalogizegroup.katalogize.services.UploadFileService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/User")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UploadFileService uploadFileService;

    @MutationMapping
    public JwtResponse signIn(@Argument String username, @Argument String password) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new GraphQLException("User disabled");
        } catch (BadCredentialsException e) {
            throw new GraphQLException("Invalid Credentials");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        UserPrincipal userDetails = (UserPrincipal)authentication.getPrincipal();
//        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();
        return new JwtResponse(jwt, refreshToken, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getIsAdmin());
    }

    @MutationMapping
    public JwtResponse refreshToken(@Argument String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            throw new GraphQLException("Refresh token is not in database!");
        } else {
            RefreshToken refreshTokenEntity = refreshTokenService.verifyExpiration(refreshTokenOptional.get());
            Optional<User> userOptional = userRepository.findById(refreshTokenEntity.getUserId());
            if (userOptional.isEmpty()) throw new GraphQLException("User does not exist!");
            User user = userOptional.get();
            String accessToken = tokenProvider.createTokenFromUserId(user.getId());
            String refreshedToken = refreshTokenService.createRefreshToken(tokenProvider.getUserIdFromToken(accessToken)).getToken();
            return new JwtResponse(accessToken, refreshedToken, user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
        }
    }

    @MutationMapping
    public String signUp(@Argument User user) {
        if (!userRepository.getUserByEmail(user.getEmail()).isEmpty()) {
            throw new GraphQLException("Email is already in use!");
        }

        if (!userRepository.getUserByUsername(user.getUsername()).isEmpty()) {
            throw new GraphQLException("Username is already taken!");
        }

        User userObject = new User(user.getDisplayName(), user.getEmail(), user.getUsername(), passwordEncoder.encode(user.getPassword()));
        User userEntity = userRepository.insert(userObject);

        return "User registered successfully!";
    }

    @MutationMapping
    public String logOut(@Argument String userId) {
        try {
            refreshTokenService.deleteByUserId(userId);
            return "Log out successful!";
        } catch (Exception e) {
            throw new GraphQLException("User does not exist!");
        }
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public User createUser(@Argument User user) {
        user.setEmailVerified(false);
        try {
            User userEntity = userRepository.insert(user);
            return userEntity;
        }catch (Exception e) {
            throw new GraphQLException("Error while creating user");
        }
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User addUserPicture(@Argument String encodedFile) {
        User user = deleteUserPicture();
        String pictureLink = uploadFileService.uploadFile(user.getUsername(), "profile",encodedFile);
        user.setPicture(pictureLink);
        return userRepository.save(user);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public User deleteUserPicture() {
        User user = getLoggedUser().get();
        if (user.getPicture() != null) {
            uploadFileService.deleteFile(user.getPicture());
            user.setPicture(null);
        }
        return userRepository.save(user);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public User deleteUser(@Argument String id) {
        Optional<User> userEntity = userRepository.findById(id);
        if (!userEntity.isEmpty()) {
            userRepository.deleteById(id);
            return userEntity.get();
        }
        throw  new GraphQLException("User does not exist");
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @QueryMapping
    public Optional<User> getUserById(@Argument String id) {
        return  userRepository.findById(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public Optional<User> getLoggedUser() {
        UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId());
    }

    @QueryMapping
    public Optional<User> getUserByUsername(@Argument String username) {
        return userRepository.getUserByUsername(username);
    }

    @SchemaMapping
    public Optional<User> user(Catalog catalog) {
        Optional<User> userEntity = userRepository.findById(catalog.getUserId());
        return userEntity;
    }
}

package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.config.security.jwt.JwtTokenProvider;
import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.JwtResponse;
import com.katalogizegroup.katalogize.models.RefreshToken;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import graphql.GraphQLException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    public User getLoggedUser() {
        try {
            UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userRepository.findById(userDetails.getId()).orElseThrow();
        } catch (Exception e) {
            throw new GraphQLException("User not logged in!");
        }
    }

    public JwtResponse signIn(String username, String password) {
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
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();
        return new JwtResponse(jwt, refreshToken, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getIsAdmin());
    }

    public JwtResponse refreshToken(String refreshToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            throw new GraphQLException("Refresh token is not in database!");
        } else {
            RefreshToken refreshTokenEntity = refreshTokenService.verifyExpiration(refreshTokenOptional.get());
            User user = getById(refreshTokenEntity.getUserId());
            if (user == null) throw new GraphQLException("User does not exist!");
            String accessToken = tokenProvider.createTokenFromUserId(user.getId());
            String refreshedToken = refreshTokenService.createRefreshToken(tokenProvider.getUserIdFromToken(accessToken)).getToken();
            return new JwtResponse(accessToken, refreshedToken, user.getId(), user.getUsername(), user.getEmail(), user.isAdmin());
        }
    }

    public String signUp(User user) {
        if (!emailService.isValidEmailAddress(user.getEmail())) {
            throw new GraphQLException("Invalid email!");
        }
        if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
            throw new GraphQLException("Email is already in use!");
        }
        if (userRepository.getUserByUsername(user.getUsername()).isPresent()) {
            throw new GraphQLException("Username is already in use!");
        }
        User userObject = new User(user.getDisplayName(), user.getEmail(), user.getUsername(), passwordEncoder.encode(user.getPassword()));
        userRepository.insert(userObject);

        emailService.sendRegistrationEmail(userObject.getEmail(), userObject.getUsername());

        return "User registered successfully!";
    }

    public String logOut(String id) {
        try {
            refreshTokenService.deleteByUserId(id);
            return "Log out successful!";
        } catch (Exception e) {
            throw new GraphQLException("User does not exist!");
        }
    }

    public String forgotPassword(String email) {
        User user = getByEmail(email);
        if (user == null) throw new GraphQLException("Email not registered!");
        String newPassword = new ObjectId().toString();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendForgotPasswordEmail(email, user.getUsername(), newPassword);
        return "An email with instructions was sent!";
    }

    public User addUserPicture(User user, String encodedFile) {
        String pictureLink = uploadFileService.uploadFile(user.getId(), "profile", encodedFile);
        user.setPicture(pictureLink);
        return userRepository.save(user);
    }

    public User updateUsername(String username) {
        User user = getLoggedUser();
        User existUser = getByUsername(username);
        if(existUser != null) throw new GraphQLException("Username already exists!");
        user.setUsername(username);
        return userRepository.save(user);
    }

    public User updateDisplayName(String displayName) {
        User user = getLoggedUser();
        user.setDisplayName(displayName);
        return userRepository.save(user);
    }

    public User updatePassword(String oldPassword, String newPassword) {
        User user = getLoggedUser();
        if (!user.getPassword().equals(passwordEncoder.encode(oldPassword))) throw new GraphQLException("Old password does not match.");
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User deleteUserPicture(User user) {
        if (user.getPicture() != null) {
            uploadFileService.deleteFile(user.getPicture());
        }
        user.setPicture(null);
        return userRepository.save(user);
    }

    public List<User> getAll () {
        return userRepository.findAll();
    }

    public User getByUsername(String username) {
        return userRepository.getUserByUsername(username).orElse(null);
    }

    public User getByEmail(String email) {
        return userRepository.getUserByEmail(email).orElse(null);
    }

    public User getById (String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User deleteById(String id) {
        User user = getById(id);
        if (user != null) {
            deleteUserPicture(user);
            userRepository.deleteById(user.getId());
            return user;
        }
        throw  new GraphQLException("User does not exist");
    }

}

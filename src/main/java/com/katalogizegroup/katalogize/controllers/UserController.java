package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.CatalogTemplate;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/User")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @MutationMapping
    public User createUser(@Argument User user) {
        user.setId((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
        try {
            User userEntity = userRepository.insert(user);
            return userEntity;
        }catch (Exception e) {
            throw new GraphQLException("Error while creating user");
        }
    }

    @MutationMapping
    public User deleteUser(@Argument int id) {
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
    public Optional<User> getUserById(@Argument int id) {
        return  userRepository.findById(id);
    }

    @SchemaMapping
    public Optional<User> user(Catalog catalog) {
        Optional<User> userEntity = userRepository.findById(catalog.getUserId());
        return userEntity;
    }
}

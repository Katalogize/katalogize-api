package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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

    @PostMapping("/Add")
    public User add(@RequestBody User user) {
        if (user.getId() == 0) {
            user.setId((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
        }
        User userEntity = userRepository.insert(user);
        return userEntity;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") int id) {
        userRepository.deleteById(id);
    }

    @QueryMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @QueryMapping
    public Optional<User> getUserById(@Argument int id) {
        return  userRepository.findById(id);
    }
}

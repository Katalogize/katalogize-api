package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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

    @PostMapping("/Add")
    public ResponseEntity<User> add(@RequestBody User user) {
        if (user.getId() == 0) {
            user.setId((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
        }
        try {
            User userEntity = userRepository.insert(user);
            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") int id) {
        if (!userRepository.findById(id).isEmpty()) {
            userRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
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

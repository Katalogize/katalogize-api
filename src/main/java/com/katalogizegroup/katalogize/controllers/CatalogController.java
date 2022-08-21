package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/Catalog")
public class CatalogController {
    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @PostMapping("/Add")
    public ResponseEntity<Catalog> add(@RequestBody Catalog catalog) {
        if (catalog.getId() == 0) {
            catalog.setId((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME));
        }
        try {
            Catalog catalogEntity = catalogRepository.insert(catalog);
            return new ResponseEntity<>(catalogEntity, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteById(@PathVariable("id") int id) {
        if (!catalogRepository.findById(id).isEmpty()) {
            catalogRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity<Catalog>(HttpStatus.NOT_FOUND);
    }

    @QueryMapping
    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    @QueryMapping
    public Optional<Catalog> getCatalogById(@Argument int id) {
        return  catalogRepository.findById(id);
    }

    @QueryMapping
    public List<Catalog> getAllCatalogsByUserId(@Argument int id) {
        return catalogRepository.getCatalogsByUserId(id);
    }

    @SchemaMapping
    public Optional<User> user(Catalog catalog) {
        Optional<User> userEntity = userRepository.findById(catalog.getUserId());
        return userEntity;
    }
}

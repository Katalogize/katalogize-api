package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
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
    SequenceGeneratorService sequenceGenerator;

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog createCatalog(@Argument Catalog catalog) {
        try {
            Catalog catalogEntity = catalogRepository.insert(catalog);
            return catalogEntity;
        } catch (Exception e){
            throw new GraphQLException("Error while creating the catalog");
        }
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog deleteCatalog(@Argument String id) {
        Optional<Catalog> catalogEntity = catalogRepository.findById(id);
        if (!catalogEntity.isEmpty()) {
            catalogRepository.deleteById(id);
            return catalogEntity.get();
        }
        return null;
    }

    @QueryMapping
    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    @QueryMapping
    public Optional<Catalog> getCatalogById(@Argument String id) {
        return  catalogRepository.findById(id);
    }

    @QueryMapping
    public List<Catalog> getAllCatalogsByUserId(@Argument String id) {
        return catalogRepository.getCatalogsByUserId(id);
    }
}

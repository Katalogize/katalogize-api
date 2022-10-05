package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog createCatalog(@Argument Catalog catalog) {
        //TODO: Validate User
        UserPrincipal userDetails;
        try {
            userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            catalog.setUserId(userDetails.getId());
        } catch (Exception e) {
            throw new GraphQLException("Invalid user");
        }
        Catalog catalogExists = catalogRepository.getCatalogByUserIdAndCatalogName(userDetails.getId(), catalog.getName());
        if (catalogExists != null) throw new GraphQLException("Catalog with this name already exists in this account");
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
        //TODO: Return only public catalogs
        return catalogRepository.getCatalogsByUserId(id);
    }

    @QueryMapping
    public List<Catalog> getCatalogsByUsername(@Argument String username) {
        String loggedUsername = "";
        try {
            UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            loggedUsername = userDetails.getUsername();
        } finally {
            Optional<User> user = userRepository.getUserByUsername(username);
            if (user.isEmpty()) throw new GraphQLException("Invalid user");
            if (username.equals(loggedUsername)) {
                return catalogRepository.getCatalogsByUserId(user.get().getId());
            }else{
                return catalogRepository.getPublicCatalogsByUserId(user.get().getId());
            }
        }
    }

    @QueryMapping
    public Catalog getCatalogByUsernameAndCatalogName(@Argument String username, @Argument String catalogName) {
        String loggedUsername = "";
        try {
            UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            loggedUsername = userDetails.getUsername();
        } finally {
            Optional<User> user = userRepository.getUserByUsername(username);
            if (user.isEmpty()) throw new GraphQLException("Invalid user");
            Catalog catalog = catalogRepository.getCatalogByUserIdAndCatalogName(user.get().getId(), catalogName);
            if (catalog == null) throw new GraphQLException("Invalid catalog");
            if ((catalog.isPrivate() && username.equals(loggedUsername)) || !catalog.isPrivate()) {
                return catalog;
            }else{
                throw new GraphQLException("Unauthorized");
            }
        }
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<Catalog> getAllCatalogsByLoggedUser() {
        UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return catalogRepository.getCatalogsByUserId(userDetails.getId());
    }
}

package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.services.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/Catalog")
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    @MutationMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Catalog createCatalog(@Argument Catalog catalog) {
        return catalogService.createCatalog(catalog);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog saveCatalogAndTemplate(@Argument Catalog catalog, @Argument CatalogTemplate catalogTemplate) {
        return catalogService.saveCatalogAndTemplate(catalog, catalogTemplate);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog deleteCatalog(@Argument String id) {
        return catalogService.deleteCatalog(id);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Catalog> getAllCatalogs() {
        return catalogService.getAllCatalogs();
    }

    @QueryMapping
    public List<Catalog> getOfficialCatalogs() {
        return catalogService.getOfficialCatalogs();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Catalog getCatalogById(@Argument String id) {
        return  catalogService.getCatalogById(id);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Catalog updateCatalogGeneralPermission(@Argument String catalogId, @Argument int permission) {
        return catalogService.updateCatalogGeneralPermission(catalogId, permission);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<Permission> shareCatalog(@Argument String catalogId, @Argument String email, @Argument int permission) {
        return catalogService.shareCatalog(catalogId, email, permission);
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('USER')")
    public Boolean leaveCatalog(@Argument String catalogId) {
        return catalogService.leaveCatalog(catalogId);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<Permission> getCatalogPermissions(@Argument String catalogId) {
        return catalogService.getCatalogPermissions(catalogId);
    }

    @QueryMapping
    public List<Catalog> getCatalogsByUsername(@Argument String username) {
        return catalogService.getCatalogsByUsername(username);
    }

    @QueryMapping
    public Catalog getCatalogByUsernameAndCatalogName(@Argument String username, @Argument String catalogName) {
        return catalogService.getCatalogByUsernameAndCatalogName(username, catalogName);
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<Catalog> getAllCatalogsByLoggedUser() {
        return catalogService.getAllCatalogsByLoggedUser();
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('USER')")
    public List<Catalog> getSharedCatalogsByLoggedUser() {
        return catalogService.getSharedCatalogsByLoggedUser();
    }
}

package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.CatalogTemplateRepository;
import com.katalogizegroup.katalogize.services.CatalogService;
import com.katalogizegroup.katalogize.services.UploadFileService;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/Catalog")
public class CatalogController {

    @Autowired
    CatalogService catalogService;

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    CatalogItemRepository catalogItemRepository;

    @Autowired
    CatalogTemplateRepository catalogTemplateRepository;

    @Autowired
    UploadFileService uploadFileService;

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
    public Catalog saveCatalogAndTemplate(@Argument Catalog catalog, @Argument CatalogTemplate catalogTemplate) {
        if (catalog.getName().equals("")) throw new GraphQLException("Invalid Catalog name");
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
            CatalogTemplate catalogTemplateEntity = catalogTemplateRepository.insert(catalogTemplate);
            catalog.setTemplateIds(Arrays.asList(catalogTemplateEntity.getId()));
        } catch (Exception e){
            throw new GraphQLException("Error while creating the catalog template");
        }
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
            boolean isAdmin = false;
            String userId = "";
            try {
                UserPrincipal userDetails = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
                userId = userDetails.getId();
            } finally {
                if ((catalogEntity.get().getUserId().equals(userId)) || isAdmin) {
                    List<CatalogItem> deletedItems = catalogItemRepository.deleteAllByCatalogId(catalogEntity.get().getId());
                    for (CatalogItem item : deletedItems) {
                        List<ItemField> imagesField = item.getFields().stream().filter(field -> field.getClass() == ItemFieldImage.class).collect(Collectors.toList());
                        for (ItemField field: imagesField) {
                            for (UploadFile image : ((ItemFieldImage) field).getValue()) {
                                uploadFileService.deleteFile(image.getPath());
                            }
                        }
                    }
                    catalogTemplateRepository.deleteById(catalogEntity.get().getTemplateIds().get(0));
                    catalogRepository.deleteById(id);
                    return catalogEntity.get();
                } else {
                    throw new GraphQLException("Unauthorized");
                }
            }
        }
        return null;
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
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

package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.Permission;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    UserService userService;

    public List<Catalog> getOfficialCatalogs() {
        return catalogRepository.getOfficialCatalogs();
    }

    public Catalog getCatalogByUsernameAndCatalogName(String username, String catalogName) {
        User user;
        try {
            user = userService.getLoggedUser();
        } catch (Exception e) {
            user = null;
        }
        User owner = userService.getByUsername(username);
        if (owner == null) throw new GraphQLException("Invalid user");
        Catalog catalog = getCatalogByUserIdAndCatalogName(owner.getId(), catalogName);
        if (catalog == null) throw new GraphQLException("Invalid catalog");
        int userPermission = getUserCatalogPermission(user, catalog);
        if (userPermission > 0){
            catalog.setUserPermission(userPermission);
            return catalog;
        }else{
            throw new GraphQLException("Unauthorized");
        }
    }

    public int getUserCatalogPermission (User user, Catalog catalog) {
        if (user == null) {
            if (catalog.getGeneralPermission() >= 2) return 1; //Only allow edition for authenticated users
            return catalog.getGeneralPermission();
        }
        if (user.getId().equals(catalog.getUserId()))return 3;
        if (catalog.getGeneralPermission() == 2) return 2; //Owner or public edit
        Permission userPermission = catalog.getPermissions().stream().filter(permission-> permission.getEmail().equals(user.getEmail())).findFirst().orElse(null);
        if (userPermission == null) return catalog.getGeneralPermission();
        return userPermission.getPermission();
    }

    public Catalog getCatalogByUserIdAndCatalogName (String userId, String catalogName) {
        return catalogRepository.getCatalogByUserIdAndCatalogName(userId, catalogName);
    }

    public List<Catalog> getAllCatalogsByLoggedUser() {
        User user = userService.getLoggedUser();
        return catalogRepository.getCatalogsByUserId(user.getId());
    }
}

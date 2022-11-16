package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.config.security.user.UserPrincipal;
import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.Permission;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    UserService userService;

    public List<Catalog> getOfficialCatalogs() {
        return catalogRepository.getOfficialCatalogs();
    }

    public Catalog updateCatalogGeneralPermission(String catalogId, int permission) {
        Catalog catalog = getCatalogById(catalogId);
        if (catalog == null) throw new GraphQLException("Invalid catalog");
        User user = userService.getLoggedUser();
        if (!catalog.getUserId().equals(user.getId())) throw new GraphQLException("Unauthorized");
        if (permission < 0 || permission > 2) throw new GraphQLException("Invalid permission");
        catalog.setGeneralPermission(permission);
        return catalogRepository.save(catalog);
    }

    public List<Permission> shareCatalog(String catalogId, String email, int permission) {
        Catalog catalog = getCatalogById(catalogId);
        if (catalog == null) throw new GraphQLException("Invalid catalog");
        User user = userService.getLoggedUser();
        if (!catalog.getUserId().equals(user.getId())) throw new GraphQLException("Unauthorized");
        if (permission < 0 || permission > 2) throw new GraphQLException("Invalid permission");
        List<Permission> permissions = catalog.getPermissions();
        permissions.removeIf(p -> p.getEmail().equals(email));
        if (permission != 0) {
            permissions.add(new Permission(email, permission));
        }
        catalog.setPermissions(permissions);
        return catalogRepository.save(catalog).getPermissions();
    }

    public List<Permission> getCatalogPermissions(String catalogId) {
        User user = userService.getLoggedUser();
        Catalog catalog = getCatalogById(catalogId);
        if (user == null || catalog == null || !catalog.getUserId().equals(user.getId())) throw new GraphQLException("Unauthorized");
        return catalog.getPermissions();
    }

    public List<Catalog> getCatalogsByUsername(@Argument String username) {
        User user;
        try {
            user = userService.getLoggedUser();
        } catch (Exception e) {
            user = null;
        }
        User owner = userService.getByUsername(username);
        if (owner == null) throw new GraphQLException("Invalid user");
        if (user != null && username.equals(user.getUsername())) {
            return catalogRepository.getCatalogsByUserId(owner.getId());
        }else{
            return catalogRepository.getPublicCatalogsByUserId(owner.getId());
        }
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

    public Catalog getCatalogById (String id) {
        return catalogRepository.findById(id).orElse(null);
    }

    public List<Catalog> getAllCatalogsByLoggedUser() {
        User user = userService.getLoggedUser();
        return catalogRepository.getCatalogsByUserId(user.getId());
    }
}

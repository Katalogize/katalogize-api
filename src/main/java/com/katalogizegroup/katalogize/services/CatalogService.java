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

    @Autowired
    EmailService emailService;

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
        emailService.sendSharedKatalogEmail(email, catalog.getName());
        return catalogRepository.save(catalog).getPermissions();
    }

    public Boolean leaveCatalog(String catalogId) {
        Catalog catalog = getCatalogById(catalogId);
        if (catalog == null) throw new GraphQLException("Invalid catalog");
        User user = userService.getLoggedUser();
        List<Permission> permissions = catalog.getPermissions();
        permissions.removeIf(p -> p.getEmail().equals(user.getEmail()));
        catalog.setPermissions(permissions);
        catalogRepository.save(catalog);
        return true;
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
        catalog.setUserPermission(user);
        if (catalog.getUserPermission() > 0){
            return catalog;
        }else{
            throw new GraphQLException("Unauthorized");
        }
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

    public List<Catalog> getSharedCatalogsByLoggedUser() {
        User user = userService.getLoggedUser();
        return catalogRepository.getSharedCatalogsByEmail(user.getEmail());
    }
}

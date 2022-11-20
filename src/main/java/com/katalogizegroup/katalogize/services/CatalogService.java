package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import graphql.GraphQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class CatalogService {

    @Autowired
    CatalogRepository catalogRepository;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    CatalogItemRepository catalogItemRepository;

    @Autowired
    CatalogTemplateService catalogTemplateService;

    public Catalog createCatalog(Catalog catalog) {
        User loggedUser = userService.getLoggedUser();
        catalog.setUserId(loggedUser.getId());
        Catalog catalogExists = catalogRepository.getCatalogByUserIdAndCatalogName(loggedUser.getId(), catalog.getName());
        if (catalogExists != null) throw new GraphQLException("Catalog with this name already exists in this account");
        if (catalogTemplateService.getTemplateById(catalog.getTemplateIds().get(0)) == null) throw new GraphQLException("Invalid template");
        catalog.setCreationDate(Instant.now());
        return catalogRepository.insert(catalog);
    }

    public Catalog saveCatalogAndTemplate(Catalog catalog, CatalogTemplate catalogTemplate) {
        if (catalog.getName().equals("")) throw new GraphQLException("Invalid Catalog name");
        User loggedUser = userService.getLoggedUser();
        catalog.setUserId(loggedUser.getId());
        Catalog catalogExists = catalogRepository.getCatalogByUserIdAndCatalogName(loggedUser.getId(), catalog.getName());
        if (catalogExists != null) throw new GraphQLException("Catalog with this name already exists in this account");
        CatalogTemplate catalogTemplateEntity = catalogTemplateService.createCatalogTemplate(catalogTemplate);
        catalog.setTemplateIds(Collections.singletonList(catalogTemplateEntity.getId()));
        catalog.setCreationDate(Instant.now());
        return catalogRepository.insert(catalog);
    }

    public Catalog deleteCatalog(String id) {
        Catalog catalog = getCatalogById(id);
        if (catalog == null) throw new GraphQLException("Invalid Catalog");
        User loggedUser = userService.getLoggedUser();
        if (!(catalog.getUserId().equals(loggedUser.getId()) || loggedUser.isAdmin())) throw new GraphQLException("Unauthorized");
        List<CatalogItem> deletedItems = catalogItemRepository.deleteAllByCatalogId(catalog.getId());
        for (CatalogItem item : deletedItems) {
            List<ItemField> imagesField = item.getFields().stream().filter(field -> field.getClass() == ItemFieldImage.class).toList();
            for (ItemField field: imagesField) {
                for (UploadFile image : ((ItemFieldImage) field).getValue()) {
                    uploadFileService.deleteFile(image.getPath());
                }
            }
        }
        catalogTemplateService.deleteTemplateById(catalog.getTemplateIds().get(0));
        catalogRepository.deleteById(id);
        return catalog;
    }

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

    public List<Catalog> getCatalogsByUsername(String username) {
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

    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    public List<Catalog> getSharedCatalogsByLoggedUser() {
        User user = userService.getLoggedUser();
        return catalogRepository.getSharedCatalogsByEmail(user.getEmail());
    }
}

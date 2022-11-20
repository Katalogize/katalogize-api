package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document (collection = "catalogs")
public class Catalog {
    @Id
    private String id = new ObjectId().toString();
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private boolean isOfficial = false;
    @NonNull private String userId;
    @NonNull private List<String> templateIds;
    @NonNull private Instant creationDate = Instant.now();
    @Getter @Setter private int generalPermission = 1; //0: No Access, 1: View, 2: Edit, 3: Owner
    @Getter @Setter private List<Permission> permissions = new ArrayList<>();
    @Transient @Getter @Setter private int userPermission;
    @Transient @Getter @Setter private boolean isShared = false;


    public int setUserPermission (User user) {
        if (user == null) { //Not authenticated
            if (getGeneralPermission() >= 2) {  //Only allow edition for authenticated users
                userPermission = 1;
                return userPermission;
            }
            userPermission = getGeneralPermission();
            return userPermission;
        }
        if (user.isAdmin()) { //Admin
            userPermission = 3;
            return userPermission;
        }
        if (user.getId().equals(userId)) { //Owner
            userPermission = 3;
            return userPermission;
        }
        Permission userCatalogPermission = getPermissions().stream().filter(permission-> permission.getEmail().equals(user.getEmail())).findFirst().orElse(null);
        if (userCatalogPermission != null) setShared(true);
        if (getGeneralPermission() == 2) { // public edit
            userPermission = 2;
            return userPermission;
        }
        if (userCatalogPermission == null) { //Not shared with user
            userPermission = getGeneralPermission();
            return userPermission;
        }
        userPermission = userCatalogPermission.getPermission(); //Private catalog with specific user permission
        return userPermission;
    }

}
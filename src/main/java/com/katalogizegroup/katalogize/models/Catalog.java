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
    @NonNull private boolean isPrivate;
    @NonNull private boolean isOfficial = false;
    @NonNull private String userId;
    @NonNull private List<String> templateIds;
    @NonNull private Instant creationDate = Instant.now();
    @Getter @Setter private int generalPermission = 1; //0: No Access, 1: View, 2: Edit, 3: Owner
    @Getter @Setter private List<Permission> permissions = new ArrayList<>();
    @Getter @Setter private int userPermission;

}
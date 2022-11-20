package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id = new ObjectId().toString();

    @NonNull private String displayName;

    @Indexed(unique = true)
    @NonNull private String email;

    private Boolean emailVerified = false;

    @Indexed(unique = true)
    @NonNull private String username;

    @NonNull private String password;

    @Getter @Setter private  String description;

    @Getter @Setter private  String picture;

    @Getter @Setter private  boolean isAdmin = false;

    @NonNull private Instant creationDate = Instant.now();

}

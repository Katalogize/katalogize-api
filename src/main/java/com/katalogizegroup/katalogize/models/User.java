package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    @Getter @Setter private  boolean isAdmin = false;

//    @Transient
//    public static final String SEQUENCE_NAME = "users_sequence";
}

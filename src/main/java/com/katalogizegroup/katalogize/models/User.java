package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id = new ObjectId().toString();
    @NonNull private String firstName;
    @NonNull private String lastName;
    @NonNull private String email;
    @NonNull private Boolean emailVerified;
    @NonNull private String username;
    @NonNull private String password;

//    @Transient
//    public static final String SEQUENCE_NAME = "users_sequence";
}

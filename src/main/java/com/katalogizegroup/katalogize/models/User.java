package com.katalogizegroup.katalogize.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
@AllArgsConstructor
public class User {
    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified = false;
    private String username;
    private String password = null;

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";
}

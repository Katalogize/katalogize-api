package com.katalogizegroup.katalogize.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class User {
    @Getter private  String id;
    private String firstName;
    private String lastName;
    private String email;

    public static List<User> users = Arrays.asList(
            new User("user-0", "Joao", "Moraes", "email1@email.com"),
            new User("user-1", "Renan", "Gama", "email2@email.com"),
            new User("user-2", "Skye", "Liam", "email3@email.com")
    );

    public static User getById(String id) {
        return users.stream().filter(catalog -> catalog.getId().equals(id)).findFirst().orElse(null);
    }

}

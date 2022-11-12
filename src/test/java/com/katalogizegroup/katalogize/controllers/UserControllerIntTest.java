package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles({"test"})
class UserControllerIntTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    private UserService userService;


    static class UserMock {
        private String displayName;
        public UserMock() {}
        public String getDisplayName() {
            return displayName;
        }
    }

    User user;

    @BeforeEach
    void setUp() {
        user = new User("Katalogize User","katalogize@email.com", "KatalogizeUser", "KatalogizeUser");
    }

    @Test
    void testGetUserByUsernameShouldReturnUser() {
        Mockito.when(userService.getByUsername(user.getUsername())).thenReturn(user);

        //language=GraphQL
        String document = """
        query  {
            getUserByUsername(username: "KatalogizeUser") {
                displayName
            }
        }
        """;

        graphQlTester.document(document)
                .execute()
                .path("getUserByUsername")
                .entity(UserMock.class)
                .satisfies(userResult -> {
                    assertEquals(userResult.getDisplayName(), user.getDisplayName());
                });
    }
}
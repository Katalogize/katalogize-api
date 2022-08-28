package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureGraphQlTester
@ActiveProfiles({"test"})
class CatalogControllerIntTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    private CatalogRepository catalogRepository;

    @Test
    void testGetAllCatalogsShouldReturnAllCatalogs() {
        Mockito.when(catalogRepository.findAll()).thenReturn(Arrays.asList(new Catalog(0, "Mock", "Mock", 0, Arrays.asList(0)), new Catalog(0, "Mock", "Mock", 0, Arrays.asList(0))));

        //language=GraphQL
        String document = """
        query  {
            getAllCatalogs {
                id
                name
            }
        }
        """;

        graphQlTester.document(document)
                .execute()
                .path("getAllCatalogs")
                .entityList(Catalog.class)
                .hasSize(2);
    }
}
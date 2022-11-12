package com.katalogizegroup.katalogize.controllers;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test"})
class CatalogControllerTest {

    @Autowired
    CatalogController catalogController;

    @MockBean
    private CatalogRepository catalogRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createCatalog() {

    }

    @Test
    void deleteCatalog() {
    }

    @Test
    void getOfficialCatalogs() {
        List<Catalog> catalogs = Arrays.asList(new Catalog("Test", "Mock", false, "0", Arrays.asList("0")), new Catalog("Test2", "Mock", false, "0", Arrays.asList("0")));
        Mockito.when(catalogRepository.getOfficialCatalogs()).thenReturn(catalogs);
        List<Catalog> response = catalogController.getOfficialCatalogs();
        assertEquals(catalogs, response);
    }

    @Test
    void getCatalogById() {
    }

    @Test
    void getAllCatalogsByUserId() {
    }
}
package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    @Autowired
    CatalogRepository catalogRepository;

    public List<Catalog> getOfficialCatalogs() {
        return catalogRepository.getOfficialCatalogs();
    }
}

package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogItemService {
    @Autowired
    CatalogItemRepository catalogItemRepository;
}

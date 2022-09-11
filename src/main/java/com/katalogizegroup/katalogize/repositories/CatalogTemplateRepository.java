package com.katalogizegroup.katalogize.repositories;

import com.katalogizegroup.katalogize.models.CatalogTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CatalogTemplateRepository extends MongoRepository<CatalogTemplate, String> {
}
package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.CatalogItem;
import org.springframework.data.mongodb.repository.*;


public interface CatalogItemRepository extends MongoRepository<CatalogItem, Integer> {
}

package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.CatalogItem;
import org.springframework.data.mongodb.repository.*;

import java.util.List;


public interface CatalogItemRepository extends MongoRepository<CatalogItem, String> {
    @Query("{catalogId : ?0}")
    List<CatalogItem> getCatalogItemsByCatalogId(String id);
}

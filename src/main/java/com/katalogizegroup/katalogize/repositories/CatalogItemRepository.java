package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.CatalogItem;
import org.springframework.data.mongodb.repository.*;

import java.util.List;
import java.util.Optional;


public interface CatalogItemRepository extends MongoRepository<CatalogItem, String> {
    @Query("{catalogId : ?0}")
    List<CatalogItem> getCatalogItemsByCatalogId(String id);

    @Query("{name : ?0, catalogId : ?1}")
    CatalogItem getCatalogItemByNameAndCatalogId(String name, String id);

    @Query(value="{catalogId : ?0}", delete = true)
    List<CatalogItem> deleteAllByCatalogId(String catalogId);
}

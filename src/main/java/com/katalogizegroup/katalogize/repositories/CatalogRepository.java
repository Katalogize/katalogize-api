package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.Catalog;
import org.springframework.data.mongodb.repository.*;

import java.util.List;

public interface CatalogRepository extends MongoRepository<Catalog, String> {
    @Query("{userId : ?0}")
    List<Catalog> getCatalogsByUserId(String id);

    @Query("{userId : ?0, isPrivate: false}")
    List<Catalog> getPublicCatalogsByUsername(String userId);
}
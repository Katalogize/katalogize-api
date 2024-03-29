package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.Catalog;
import org.springframework.data.mongodb.repository.*;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends MongoRepository<Catalog, String> {
    @Query("{userId : ?0}")
    List<Catalog> getCatalogsByUserId(String id);

    @Query("{userId : ?0, name : ?1}")
    Catalog getCatalogByUserIdAndCatalogName(String userId, String name);

    @Query("{userId : ?0, generalPermission: { $gt: 0 }}")
    List<Catalog> getPublicCatalogsByUserId(String userId);

    @Query("{'permissions.email' : ?0}")
    List<Catalog> getSharedCatalogsByEmail(String email);

    @Query("{isOfficial: true}")
    List<Catalog> getOfficialCatalogs();
}
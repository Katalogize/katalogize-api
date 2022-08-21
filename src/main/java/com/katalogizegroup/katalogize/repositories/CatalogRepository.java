package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.Catalog;
import org.springframework.data.mongodb.repository.*;

import java.util.List;

public interface CatalogRepository extends MongoRepository<Catalog, Integer> {
    @Query("{userId : ?0}")
    List<Catalog> getCatalogsByUserId(int id);
}
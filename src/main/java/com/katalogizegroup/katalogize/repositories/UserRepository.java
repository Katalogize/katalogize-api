package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import org.springframework.data.mongodb.repository.*;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Integer> {
    @Query("{email : ?0}")
    Optional<User> getUserByEmail(String email);

    @Query("{username : ?0}")
    Optional<User> getUserByUsername(String username);
}
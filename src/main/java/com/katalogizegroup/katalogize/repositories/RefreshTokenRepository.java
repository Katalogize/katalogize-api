package com.katalogizegroup.katalogize.repositories;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.RefreshToken;
import com.katalogizegroup.katalogize.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    @Query(value = "{userId : ?0}", delete = true)
    Optional<RefreshToken> deleteByUserId(String userId);

    @Query("{token : ?0}")
    Optional<RefreshToken> findByToken(String token);
}
package com.katalogizegroup.katalogize.repositories;

import com.katalogizegroup.katalogize.models.RefreshToken;
import com.katalogizegroup.katalogize.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
}
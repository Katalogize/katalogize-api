package com.katalogizegroup.katalogize.repositories;
import com.katalogizegroup.katalogize.models.User;
import org.springframework.data.mongodb.repository.*;

public interface UserRepository extends MongoRepository<User, Integer> {
}
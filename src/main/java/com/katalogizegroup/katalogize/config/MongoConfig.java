package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.Catalog;
import com.katalogizegroup.katalogize.models.User;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
@EnableMongoRepositories(basePackageClasses = {CatalogRepository.class, UserRepository.class})
@Configuration
public class MongoConfig {
    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Bean
    CommandLineRunner commandLineRunner(CatalogRepository catalogRepository, UserRepository userRepository) {
        if (catalogRepository.findAll().size() == 0 && userRepository.findAll().size() == 0) {
            return strings -> {
                userRepository.insert(new User((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME), "User1", "Mock1", "katalogize@email.com"));
                userRepository.insert(new User((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME), "User2", "Mock2", "katalogize2@email.com"));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Catalog Mock 1", "Games played", 1));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Catalog Mock 2", "Music listened", 1));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Catalog Mock 3", "Movies watched", 2));
            };
        }
        return null;
    }
}
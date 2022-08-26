package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import com.katalogizegroup.katalogize.repositories.CatalogItemRepository;
import com.katalogizegroup.katalogize.repositories.CatalogRepository;
import com.katalogizegroup.katalogize.repositories.CatalogTemplateRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@EnableMongoRepositories(basePackageClasses = {CatalogRepository.class, CatalogItemRepository.class, UserRepository.class, CatalogTemplateRepository.class})
@Configuration
public class MongoConfig {
    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Bean
    CommandLineRunner commandLineRunner(CatalogRepository catalogRepository, CatalogItemRepository catalogItemRepository, UserRepository userRepository, CatalogTemplateRepository templateRepository) {
        if (catalogRepository.findAll().size() == 0 && userRepository.findAll().size() == 0) {
            return strings -> {
                userRepository.insert(new User((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME), "User1", "Mock1", "katalogize@email.com"));
                userRepository.insert(new User((int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME), "User2", "Mock2", "katalogize2@email.com"));
                templateRepository.insert(new CatalogTemplate(0, "Default template", Arrays.asList(), true));
                int templateId = (int)sequenceGenerator.generateSequence(CatalogTemplate.SEQUENCE_NAME);
                templateRepository.insert(new CatalogTemplate(templateId, "Movies template", Arrays.asList(new TemplateField(1, "Name", 2), new TemplateField(2, "Stars", 1)), false));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Games", "Games played", 1, Arrays.asList(templateId)));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Music", "Music listened", 1, Arrays.asList(0)));
                catalogRepository.insert(new Catalog((int)sequenceGenerator.generateSequence(Catalog.SEQUENCE_NAME), "Movies", "Movies watched", 2, Arrays.asList(0)));
                catalogItemRepository.insert(new CatalogItem((int)sequenceGenerator.generateSequence(CatalogItem.SEQUENCE_NAME), 1, 1, Arrays.asList(new ItemFieldString(1, "", "It takes two"), new ItemFieldInt(2, "", 9))));
                catalogItemRepository.insert(new CatalogItem((int)sequenceGenerator.generateSequence(CatalogItem.SEQUENCE_NAME), 3, 0, Arrays.asList(new ItemFieldString(1, "Name", "Gremlins"), new ItemFieldInt(2, "Rating", 9))));
            };
        }
        return null;
    }
}
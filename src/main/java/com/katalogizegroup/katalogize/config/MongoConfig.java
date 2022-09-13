package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldInt;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import com.katalogizegroup.katalogize.repositories.*;
import com.katalogizegroup.katalogize.services.SequenceGeneratorService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.List;

@EnableMongoRepositories(basePackageClasses = {CatalogRepository.class, CatalogItemRepository.class, UserRepository.class, CatalogTemplateRepository.class, RefreshTokenRepository.class})
@Configuration
@Profile({"default"})
public class MongoConfig {
    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Bean
    CommandLineRunner commandLineRunner(CatalogRepository catalogRepository, CatalogItemRepository catalogItemRepository, UserRepository userRepository, CatalogTemplateRepository templateRepository, RefreshTokenRepository refreshTokenRepository) {
//        (int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME);
        if (catalogRepository.findAll().size() == 0 && userRepository.findAll().size() == 0) {
            return strings -> {
                List<User> users = Arrays.asList(
                        new User("User1", "Mock1", "katalogize@email.com", "FakeUser1", "FakePassword1"),
                        new User("User2", "Mock2", "katalogize2@email.com", "FakeUser2", "FakePassword2")
                );

                List<CatalogTemplate> templates = Arrays.asList(
                        new CatalogTemplate("Default template", Arrays.asList(), true),
                        new CatalogTemplate("Games template", Arrays.asList(new TemplateField(1, "Description", 2), new TemplateField(2, "Stars", 1)), false)
                );

                List<Catalog> catalogs = Arrays.asList(
                        new Catalog("Music", "Music listened", true, new ObjectId(users.get(0).getId()).toString(), Arrays.asList(templates.get(0).getId())),
                        new Catalog("Games", "Games played", true, users.get(0).getId(), Arrays.asList(templates.get(1).getId())),
                        new Catalog("Movies", "Movies watched", false, users.get(1).getId(), Arrays.asList(templates.get(0).getId()))
                );

                List<CatalogItem> catalogItems = Arrays.asList(
                        new CatalogItem(catalogs.get(0).getId(), templates.get(0).getId(), "Summer Renaissance", Arrays.asList(new ItemFieldString(1, "Artist", "Beyonce"), new ItemFieldInt(2, "Likes", 9))),
                        new CatalogItem(catalogs.get(1).getId(), templates.get(1).getId(), "It Takes Two", Arrays.asList(new ItemFieldString(1, "", "A coop game"), new ItemFieldInt(2, "", 9))),
                        new CatalogItem(catalogs.get(2).getId(), templates.get(0).getId(), "Gremlins", Arrays.asList(new ItemFieldString(1, "Description", "A gremlins movie"), new ItemFieldInt(2, "Year", 1984)))
                );

                userRepository.insert(users);
                templateRepository.insert(templates);
                catalogRepository.insert(catalogs);
                catalogItemRepository.insert(catalogItems);
            };
        }
        return null;
    }
}
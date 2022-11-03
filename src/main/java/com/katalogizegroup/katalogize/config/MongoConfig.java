package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldNumber;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@EnableMongoRepositories(basePackageClasses = {CatalogRepository.class, CatalogItemRepository.class, UserRepository.class, CatalogTemplateRepository.class, RefreshTokenRepository.class})
@Configuration
@Profile({"default"})
public class MongoConfig {
    @Autowired
    SequenceGeneratorService sequenceGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner commandLineRunner(CatalogRepository catalogRepository, CatalogItemRepository catalogItemRepository, UserRepository userRepository, CatalogTemplateRepository templateRepository, RefreshTokenRepository refreshTokenRepository) {
//        (int)sequenceGenerator.generateSequence(User.SEQUENCE_NAME);
        if (catalogRepository.findAll().size() == 0 && userRepository.findAll().size() == 0) {
            return strings -> {
                List<User> users = Arrays.asList(
                        new User("Katalogize Admin", "katalogize@email.com", "KatalogizeAdmin", passwordEncoder.encode("KatalogizeAdmin")),
                        new User("Katalogize Team","katalogize2@email.com", "KatalogizeUser", passwordEncoder.encode("KatalogizeUser"))
                );

                users.get(0).setAdmin(true);

                List<CatalogTemplate> templates = Arrays.asList(
                        new CatalogTemplate("Default template", Arrays.asList(), true),
                        new CatalogTemplate("Games template", Arrays.asList(new TemplateField(1, "Description", 1), new TemplateField(2, "Stars", 2)), false)
                );

                List<Catalog> catalogs = Arrays.asList(
                        new Catalog("Music", "Music listened", false, new ObjectId(users.get(0).getId()).toString(), Arrays.asList(templates.get(0).getId())),
                        new Catalog("Games", "Games played", false, users.get(0).getId(), Arrays.asList(templates.get(1).getId())),
                        new Catalog("Movies", "Movies watched", false, users.get(1).getId(), Arrays.asList(templates.get(0).getId()))
                );

                List<CatalogItem> catalogItems = Arrays.asList(
                        new CatalogItem(catalogs.get(0).getId(), templates.get(0).getId(), "Summer Renaissance", Arrays.asList(new ItemFieldString(1, "Artist", "Beyonce"), new ItemFieldNumber(2, "Likes", 9))),
                        new CatalogItem(catalogs.get(0).getId(), templates.get(0).getId(), "Energy", Arrays.asList(new ItemFieldString(1, "Artist", "Beyonce"), new ItemFieldNumber(2, "Likes", 9))),
                        new CatalogItem(catalogs.get(1).getId(), templates.get(1).getId(), "It Takes Two", Arrays.asList(new ItemFieldString(1, "", "A coop game"), new ItemFieldNumber(2, "", 9))),
                        new CatalogItem(catalogs.get(2).getId(), templates.get(0).getId(), "Gremlins", Arrays.asList(new ItemFieldString(1, "Description", "A gremlins movie"), new ItemFieldNumber(2, "Year", 1984)))
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
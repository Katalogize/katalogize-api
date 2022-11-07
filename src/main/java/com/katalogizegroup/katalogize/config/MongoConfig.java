package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
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
                        new User("Katalogize Team", "katalogize@email.com", "KatalogizeAdmin", passwordEncoder.encode("K4t4l0g1z3Adm1n321")),
                        new User("Katalogize User","katalogize2@email.com", "KatalogizeUser", passwordEncoder.encode("KatalogizeUser"))
                );

                users.get(0).setAdmin(true);

                List<CatalogTemplate> templates = Arrays.asList(
                        new CatalogTemplate("Default template", Arrays.asList(), true),
                        new CatalogTemplate("Games template", Arrays.asList(new TemplateField(1, "Description", 1), new TemplateField(2, "Genre", 1), new TemplateField(3, "Release Year", 2), new TemplateField(4, "Screenshots", 3)), false),
                        new CatalogTemplate("Artists template", Arrays.asList(new TemplateField(1, "Description", 1), new TemplateField(2, "Born in", 2), new TemplateField(3, "Images", 3)), false),
                        new CatalogTemplate("Movies template", Arrays.asList(new TemplateField(1, "Description", 1), new TemplateField(2, "Release Year", 2), new TemplateField(3, "Duration (minutes)", 2), new TemplateField(4, "Scenes", 3)), false),
                        new CatalogTemplate("Books template", Arrays.asList(new TemplateField(1, "Description", 1), new TemplateField(2, "Release Year", 2), new TemplateField(3, "Number of Pages", 2), new TemplateField(4, "Cover", 3)), false)
                );

                List<Catalog> catalogs = Arrays.asList(
                        new Catalog("Games", "Video game recommendations from the Katalogize Team", false, users.get(0).getId(), Arrays.asList(templates.get(1).getId())),
                        new Catalog("Artists", "A collection of the biggest pop music artists selected by the Katalogize Team", false, users.get(0).getId(), Arrays.asList(templates.get(2).getId())),
                        new Catalog("Movies", "Movies recommendations from the Katalogize Team", false, users.get(0).getId(), Arrays.asList(templates.get(3).getId())),
                        new Catalog("Books", "Best-Seller books recommendations from the Katalogize Team", false, users.get(0).getId(), Arrays.asList(templates.get(4).getId()))
                );
                catalogs.get(0).setOfficial(true);
                catalogs.get(1).setOfficial(true);
                catalogs.get(2).setOfficial(true);
                catalogs.get(3).setOfficial(true);

                List<CatalogItem> catalogItems = Arrays.asList(
                    //Games
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Overwatch", Arrays.asList(
                        new ItemFieldString(1, "", "Overwatch is a first-person multiplayer shooter, set in a future where a conflict between robots and humanity necessitated the creation of a task force, conveniently called \"Overwatch.\" In the game's primary competitive mode, players are arranged into two teams of six and compete on a variety of maps and game types."),
                        new ItemFieldString(2, "", "Shooter, Action"),
                        new ItemFieldNumber(3, "", 2016),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Overwatch/Overwatch1.jpg", null), new UploadFile("official/Games/Overwatch/Overwatch2.jpg", null), new UploadFile("official/Games/Overwatch/Overwatch3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "It Takes Two", Arrays.asList(
                        new ItemFieldString(1, "", "Play as the clashing couple Cody and May, two humans turned into dolls by a magic spell. Trapped in a fantastical world, they're reluctantly challenged with saving their fractured relationship by the suave love guru Dr. Hakim."),
                        new ItemFieldString(2, "", "Adventure, Cooperative"),
                        new ItemFieldNumber(3, "", 2021),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/ItTakesTwo/ItTakesTwo1.jpg", null), new UploadFile("official/Games/ItTakesTwo/ItTakesTwo2.jpg", null), new UploadFile("official/Games/ItTakesTwo/ItTakesTwo3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Warframe", Arrays.asList(
                        new ItemFieldString(1, "", "Confront warring factions throughout a sprawling interplanetary system as you follow the guidance of the mysterious Lotus and level up your Warframe, build an Arsenal of destructive firepower, and realize your true potential across massive open worlds in this thrilling, genre-defining third-person combat experience."),
                        new ItemFieldString(2, "", "Action"),
                        new ItemFieldNumber(3, "", 2013),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Warframe/Warframe1.jpg", null), new UploadFile("official/Games/Warframe/Warframe2.jpg", null), new UploadFile("official/Games/Warframe/Warframe3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Outer Wilds", Arrays.asList(
                        new ItemFieldString(1, "", "Outer Wilds is an open world mystery about a solar system trapped in an endless time loop. Welcome to the Space Program! You're the newest recruit of Outer Wilds Ventures, a fledgling space program searching for answers in a strange, constantly evolving solar system."),
                        new ItemFieldString(2, "", "Adventure, Exploration"),
                        new ItemFieldNumber(3, "", 2019),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/OuterWilds/Outerwilds1.jpeg", null), new UploadFile("official/Games/OuterWilds/Outerwilds2.jpg", null), new UploadFile("official/Games/OuterWilds/Outerwilds3.png", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "League of Legends", Arrays.asList(
                        new ItemFieldString(1, "", "League of Legends is one of the world's most popular video games, developed by Riot Games. It features a team-based competitive game mode based on strategy and outplaying opponents. Players work with their team to break the enemy Nexus before the enemy team breaks theirs."),
                        new ItemFieldString(2, "", "Multiplayer, MOBA"),
                        new ItemFieldNumber(3, "", 2009),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends1.jpg", null), new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends2.jpg", null), new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Hades", Arrays.asList(
                        new ItemFieldString(1, "", "Hades is a rogue-like dungeon crawler in which you defy the god of the dead as you hack and slash your way out of the Underworld of Greek myth."),
                        new ItemFieldString(2, "", "Roguelike, Action"),
                        new ItemFieldNumber(3, "", 2020),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Hades/Hades1.jpg", null), new UploadFile("official/Games/Hades/Hades2.jpg", null), new UploadFile("official/Games/Hades/Hades3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Risk of Rain 2", Arrays.asList(
                        new ItemFieldString(1, "", "Escape a chaotic alien planet by fighting through hordes of frenzied monsters – with your friends, or on your own."),
                        new ItemFieldString(2, "", "Roguelike, Action"),
                        new ItemFieldNumber(3, "", 2020),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/RiskOfRain/RiskOfRain1.jpg", null), new UploadFile("official/Games/RiskOfRain/RiskOfRain2.jpg", null), new UploadFile("official/Games/RiskOfRain/RiskOfRain3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "The Legend of Zelda: Breath of the Wild", Arrays.asList(
                        new ItemFieldString(1, "", "After a 100-year slumber, Link wakes up alone in a world he no longer remembers. Now the legendary hero must explore a vast and dangerous land and regain his memories before Hyrule is lost forever. Armed only with what he can scavenge, Link sets out to find answers and the resources needed to survive."),
                        new ItemFieldString(2, "", "Adventure, RPG"),
                        new ItemFieldNumber(3, "", 2017),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/ZeldaBOTW/Zelda1.jpg", null), new UploadFile("official/Games/ZeldaBOTW/Zelda2.jpg", null), new UploadFile("official/Games/ZeldaBOTW/Zelda3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Cuphead", Arrays.asList(
                        new ItemFieldString(1, "", "Cuphead is a classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s, the visuals and audio are painstakingly created with the same techniques of the era: traditional hand drawn cel animation, watercolor backgrounds, and original jazz recordings."),
                        new ItemFieldString(2, "", "Run and Gun, Platform"),
                        new ItemFieldNumber(3, "", 2017),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Cuphead/Cuphead1.jpg", null), new UploadFile("official/Games/Cuphead/Cuphead2.jpg", null), new UploadFile("official/Games/Cuphead/Cuphead3.jpg", null))))),
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Super Mario Odyssey", Arrays.asList(
                        new ItemFieldString(1, "", "Super Mario Odyssey is a platform game in which players control Mario as he travels across many different worlds, known as \"Kingdoms\" within the game, on the hat-shaped ship Odyssey, to rescue Princess Peach from Bowser, who plans to forcibly marry her."),
                        new ItemFieldString(2, "", "Adventure"),
                        new ItemFieldNumber(3, "", 2017),
                        new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/MarioOdyssey/Mario1.jpg", null), new UploadFile("official/Games/MarioOdyssey/Mario2.jpg", null), new UploadFile("official/Games/MarioOdyssey/Mario3.jpg", null)))))
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
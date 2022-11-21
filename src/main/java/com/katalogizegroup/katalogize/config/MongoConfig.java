package com.katalogizegroup.katalogize.config;

import com.katalogizegroup.katalogize.models.*;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldImage;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldNumber;
import com.katalogizegroup.katalogize.models.itemfields.ItemFieldString;
import com.katalogizegroup.katalogize.repositories.*;
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
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner commandLineRunner(CatalogRepository catalogRepository, CatalogItemRepository catalogItemRepository, UserRepository userRepository, CatalogTemplateRepository templateRepository, RefreshTokenRepository refreshTokenRepository) {
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
                        new CatalogTemplate("Movies template", Arrays.asList(new TemplateField(1, "Plot", 1), new TemplateField(2, "Director", 1), new TemplateField(3, "Release Year", 2), new TemplateField(4, "Duration (minutes)", 2), new TemplateField(5, "Scenes", 3)), false),
                        new CatalogTemplate("Series template", Arrays.asList(new TemplateField(1, "Plot", 1), new TemplateField(2, "Original Broadcaster", 1), new TemplateField(3, "Release Year", 2), new TemplateField(4, "Number of Episodes", 2), new TemplateField(5, "Scenes", 3)), false),
                        new CatalogTemplate("Books template", Arrays.asList(new TemplateField(1, "Plot", 1), new TemplateField(2, "Genre", 1), new TemplateField(3, "Author", 1), new TemplateField(4, "Publisher", 1), new TemplateField(5, "Publication Year", 2), new TemplateField(6, "Number of Pages", 2)), false)
                );

                List<Catalog> catalogs = Arrays.asList(
                        new Catalog("Games", "Video game recommendations from the Katalogize Team", users.get(0).getId(), Arrays.asList(templates.get(1).getId())),
                        new Catalog("Movies", "Movies recommendations from the Katalogize Team", users.get(0).getId(), Arrays.asList(templates.get(2).getId())),
                        new Catalog("Series", "Series recommendations from the Katalogize Team", users.get(0).getId(), Arrays.asList(templates.get(3).getId())),
                        new Catalog("Books", "Best-Seller books recommendations from the Katalogize Team", users.get(0).getId(), Arrays.asList(templates.get(4).getId()))
                );
                catalogs.get(0).setOfficial(true);
                catalogs.get(1).setOfficial(true);
                catalogs.get(2).setOfficial(true);
                catalogs.get(3).setOfficial(true);

                List<CatalogItem> catalogItems = Arrays.asList(
                    //Games
                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Overwatch", Arrays.asList(
                            new ItemFieldString(templates.get(1).getTemplateFields().get(0).getId(), "", "Overwatch is a first-person multiplayer shooter, set in a future where a conflict between robots and humanity necessitated the creation of a task force, conveniently called \"Overwatch.\" In the game's primary competitive mode, players are arranged into two teams of six and compete on a variety of maps and game types."),
                            new ItemFieldString(templates.get(1).getTemplateFields().get(1).getId(), "", "Shooter, Action"),
                            new ItemFieldNumber(templates.get(1).getTemplateFields().get(2).getId(), "", 2016),
                            new ItemFieldImage(templates.get(1).getTemplateFields().get(3).getId(), "", Arrays.asList(new UploadFile("official/Games/Overwatch/Overwatch1.jpg", null), new UploadFile("official/Games/Overwatch/Overwatch2.jpg", null), new UploadFile("official/Games/Overwatch/Overwatch3.jpg", null)))))
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "It Takes Two", Arrays.asList(
//                            new ItemFieldString(1, "", "Play as the clashing couple Cody and May, two humans turned into dolls by a magic spell. Trapped in a fantastical world, they're reluctantly challenged with saving their fractured relationship by the suave love guru Dr. Hakim."),
//                            new ItemFieldString(2, "", "Adventure, Cooperative"),
//                            new ItemFieldNumber(3, "", 2021),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/ItTakesTwo/ItTakesTwo1.jpg", null), new UploadFile("official/Games/ItTakesTwo/ItTakesTwo2.jpg", null), new UploadFile("official/Games/ItTakesTwo/ItTakesTwo3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Warframe", Arrays.asList(
//                            new ItemFieldString(1, "", "Confront warring factions throughout a sprawling interplanetary system as you follow the guidance of the mysterious Lotus and level up your Warframe, build an Arsenal of destructive firepower, and realize your true potential across massive open worlds in this thrilling, genre-defining third-person combat experience."),
//                            new ItemFieldString(2, "", "Action"),
//                            new ItemFieldNumber(3, "", 2013),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Warframe/Warframe1.jpg", null), new UploadFile("official/Games/Warframe/Warframe2.jpg", null), new UploadFile("official/Games/Warframe/Warframe3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Outer Wilds", Arrays.asList(
//                            new ItemFieldString(1, "", "Outer Wilds is an open world mystery about a solar system trapped in an endless time loop. Welcome to the Space Program! You're the newest recruit of Outer Wilds Ventures, a fledgling space program searching for answers in a strange, constantly evolving solar system."),
//                            new ItemFieldString(2, "", "Adventure, Exploration"),
//                            new ItemFieldNumber(3, "", 2019),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/OuterWilds/Outerwilds1.jpeg", null), new UploadFile("official/Games/OuterWilds/Outerwilds2.jpg", null), new UploadFile("official/Games/OuterWilds/Outerwilds3.png", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "League of Legends", Arrays.asList(
//                            new ItemFieldString(1, "", "League of Legends is one of the world's most popular video games, developed by Riot Games. It features a team-based competitive game mode based on strategy and outplaying opponents. Players work with their team to break the enemy Nexus before the enemy team breaks theirs."),
//                            new ItemFieldString(2, "", "Multiplayer, MOBA"),
//                            new ItemFieldNumber(3, "", 2009),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends1.jpg", null), new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends2.jpg", null), new UploadFile("official/Games/LeagueOfLegends/LeagueOfLegends3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Hades", Arrays.asList(
//                            new ItemFieldString(1, "", "Hades is a rogue-like dungeon crawler in which you defy the god of the dead as you hack and slash your way out of the Underworld of Greek myth."),
//                            new ItemFieldString(2, "", "Roguelike, Action"),
//                            new ItemFieldNumber(3, "", 2020),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Hades/Hades1.jpg", null), new UploadFile("official/Games/Hades/Hades2.jpg", null), new UploadFile("official/Games/Hades/Hades3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Risk of Rain 2", Arrays.asList(
//                            new ItemFieldString(1, "", "Escape a chaotic alien planet by fighting through hordes of frenzied monsters – with your friends, or on your own."),
//                            new ItemFieldString(2, "", "Roguelike, Action"),
//                            new ItemFieldNumber(3, "", 2020),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/RiskOfRain/RiskOfRain1.jpg", null), new UploadFile("official/Games/RiskOfRain/RiskOfRain2.jpg", null), new UploadFile("official/Games/RiskOfRain/RiskOfRain3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "The Legend of Zelda: Breath of the Wild", Arrays.asList(
//                            new ItemFieldString(1, "", "After a 100-year slumber, Link wakes up alone in a world he no longer remembers. Now the legendary hero must explore a vast and dangerous land and regain his memories before Hyrule is lost forever. Armed only with what he can scavenge, Link sets out to find answers and the resources needed to survive."),
//                            new ItemFieldString(2, "", "Adventure, RPG"),
//                            new ItemFieldNumber(3, "", 2017),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/ZeldaBOTW/Zelda1.jpg", null), new UploadFile("official/Games/ZeldaBOTW/Zelda2.jpg", null), new UploadFile("official/Games/ZeldaBOTW/Zelda3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Cuphead", Arrays.asList(
//                            new ItemFieldString(1, "", "Cuphead is a classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s, the visuals and audio are painstakingly created with the same techniques of the era: traditional hand drawn cel animation, watercolor backgrounds, and original jazz recordings."),
//                            new ItemFieldString(2, "", "Run and Gun, Platform"),
//                            new ItemFieldNumber(3, "", 2017),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/Cuphead/Cuphead1.jpg", null), new UploadFile("official/Games/Cuphead/Cuphead2.jpg", null), new UploadFile("official/Games/Cuphead/Cuphead3.jpg", null))))),
//                    new CatalogItem(catalogs.get(0).getId(), templates.get(1).getId(), "Super Mario Odyssey", Arrays.asList(
//                            new ItemFieldString(1, "", "Super Mario Odyssey is a platform game in which players control Mario as he travels across many different worlds, known as \"Kingdoms\" within the game, on the hat-shaped ship Odyssey, to rescue Princess Peach from Bowser, who plans to forcibly marry her."),
//                            new ItemFieldString(2, "", "Adventure"),
//                            new ItemFieldNumber(3, "", 2017),
//                            new ItemFieldImage(4, "", Arrays.asList(new UploadFile("official/Games/MarioOdyssey/Mario1.jpg", null), new UploadFile("official/Games/MarioOdyssey/Mario2.jpg", null), new UploadFile("official/Games/MarioOdyssey/Mario3.jpg", null))))),
//
//                    //Movies
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Avatar", Arrays.asList(
//                            new ItemFieldString(templates.get(2).getTemplateFields().get(0).getId(), "", "In 2154, Earth's natural resources have been depleted. The Resources Development Administration (RDA) mines the valuable mineral unobtanium on Pandora, a moon in the Alpha Centauri star system. Pandora, whose atmosphere is poisonous to humans, is inhabited by the Na'vi, 10-foot-tall (3.0 m), blue-skinned, sapient humanoids that live in harmony with nature. To explore Pandora, genetically matched human scientists use Na'vi-human hybrids called \"avatars\". Paraplegic Marine Jake Sully is sent to Pandora to replace his deceased identical twin, who had signed up to be an operator. Avatar Program head Dr. Grace Augustine considers Sully inadequate but accepts him as a bodyguard."),
//                            new ItemFieldString(2, "", "James Cameron"),
//                            new ItemFieldNumber(3, "", 2009),
//                            new ItemFieldNumber(4, "", 162),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/Avatar/Avatar1.jpg", null), new UploadFile("official/Movies/Avatar/Avatar2.jpg", null), new UploadFile("official/Movies/Avatar/Avatar3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Chicago", Arrays.asList(
//                            new ItemFieldString(1, "", "Fame hungry Roxie Hart (Renée Zellweger) dreams of a life on the Vaudville stage, and spends her nights jazzing it up in the bright lights of Chicago, continually hoping that she'll find her lucky break, and be shot into 1920's stardom, so able to flee her boring husband Amos (John C. Reilly). In awe of seductive club singer Velma Kelly (Catherine Zeta-Jones) (who is subsequantly arrested for the murder of her husband and sister - after discovering their affair), Roxie meets Fred Casely (Dominic West), a man who convinces her he can \"make her showbiz career take off\"."),
//                            new ItemFieldString(2, "", "Rob Marshall"),
//                            new ItemFieldNumber(3, "", 2002),
//                            new ItemFieldNumber(4, "", 113),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/Chicago/Chicago1.jpg", null), new UploadFile("official/Movies/Chicago/Chicago2.jpg", null), new UploadFile("official/Movies/Chicago/Chicago3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Harry Potter and the Goblet of Fire", Arrays.asList(
//                            new ItemFieldString(1, "", "The Tri-Wizard Tournament is open. Four champions are selected to compete in three terrifying tasks in order to win the Tri-Wizard Cup. Meanwhile, Harry Potter (Daniel Radcliffe) is selected by the Goblet of Fire to compete while struggling to keep up the pace with classes and friends. He must confront fierce dragons, aggressive mermaids, and a dark wizard that hasn't been able to make his move for thirteen years."),
//                            new ItemFieldString(2, "", "Mike Newell"),
//                            new ItemFieldNumber(3, "", 2005),
//                            new ItemFieldNumber(4, "", 157),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/HarryPotter/HarryPotter1.jpg", null), new UploadFile("official/Movies/HarryPotter/HarryPotter2.jpg", null), new UploadFile("official/Movies/HarryPotter/HarryPotter3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Hocus Pocus 2", Arrays.asList(
//                            new ItemFieldString(1, "", "It's been 29 years since someone lit the Black Flame Candle and resurrected the 17th-century Sanderson sisters, and they are looking for revenge. Now it is up to three high-school students to stop the ravenous witches from wreaking a new kind of havoc on Salem before dawn on All Hallow's Eve."),
//                            new ItemFieldString(2, "", "Anne Fletcher"),
//                            new ItemFieldNumber(3, "", 2022),
//                            new ItemFieldNumber(4, "", 103),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/HocusPocus/HocusPocus1.jpg", null), new UploadFile("official/Movies/HocusPocus/HocusPocus2.jpg", null), new UploadFile("official/Movies/HocusPocus/HocusPocus3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Interestellar", Arrays.asList(
//                            new ItemFieldString(1, "", "Earth's future has been riddled by disasters, famines, and droughts. There is only one way to ensure mankind's survival: Interstellar travel. A newly discovered wormhole in the far reaches of our solar system allows a team of astronauts to go where no man has gone before, a planet that may have the right environment to sustain human life. Interestellar is highly recommended by the Katalogize Team."),
//                            new ItemFieldString(2, "", "Christopher Nolan"),
//                            new ItemFieldNumber(3, "", 2014),
//                            new ItemFieldNumber(4, "", 169),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/Interestelar/Interestelar1.jpg", null), new UploadFile("official/Movies/Interestelar/Interestelar2.jpg", null), new UploadFile("official/Movies/Interestelar/Interestelar3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "The Shape of Water", Arrays.asList(
//                            new ItemFieldString(1, "", "Elisa is a mute, isolated woman who works as a cleaning lady in a hidden, high-security government laboratory in 1962 Baltimore. Her life changes forever when she discovers the lab's classified secret: a mysterious, scaled creature from South America that lives in a water tank. As Elisa develops a unique bond with her new friend, she soon learns that its fate and very survival lies in the hands of a hostile government agent and a marine biologist."),
//                            new ItemFieldString(2, "", "Guillermo del Toro"),
//                            new ItemFieldNumber(3, "", 2018),
//                            new ItemFieldNumber(4, "", 123),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/ShapeOfWater/ShapeOfWater1.jpg", null), new UploadFile("official/Movies/ShapeOfWater/ShapeOfWater2.jpg", null), new UploadFile("official/Movies/ShapeOfWater/ShapeOfWater3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Shrek", Arrays.asList(
//                            new ItemFieldString(1, "", "Shrek is a big ogre who lives alone in the woods, feared by all the people in the land of Duloc. When Lord Farquaad, the ruler of Duloc, exiles all the fairy-tale beings to the woods, Shrek loses his peaceful life and his home becomes a refugee camp. So he sets out to find Lord Farquaad and convince him to take the fairy-tale beings back where they belong, and leave him alone. Lord Farquaad accepts, under one condition. Shrek must first go and find the beautiful young princess Fiona, who will become Farquaad's bride. So the big Ogre begins his quest, along with his newfound donkey friend."),
//                            new ItemFieldString(2, "", "Vicky Jenson, Andrew Adamson"),
//                            new ItemFieldNumber(3, "", 2001),
//                            new ItemFieldNumber(4, "", 89),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/Shrek/Shrek1.jpg", null), new UploadFile("official/Movies/Shrek/Shrek2.jpg", null), new UploadFile("official/Movies/Shrek/Shrek3.jpg", null))))),
//                    new CatalogItem(catalogs.get(1).getId(), templates.get(2).getId(), "Charlie and the Chocolate Factory", Arrays.asList(
//                            new ItemFieldString(1, "", "A young boy wins a tour through the most magnificent chocolate factory in the world, led by the world's most unusual candy maker. When Willy Wonka decides to let five children into his chocolate factory, he decides to release five golden tickets in five separate chocolate bars, causing complete mayhem."),
//                            new ItemFieldString(2, "", "Tim Burton"),
//                            new ItemFieldNumber(3, "", 2005),
//                            new ItemFieldNumber(4, "", 115),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Movies/ChocolateFactory/ChocolateFactory1.jpg", null), new UploadFile("official/Movies/ChocolateFactory/ChocolateFactory2.jpg", null), new UploadFile("official/Movies/ChocolateFactory/ChocolateFactory3.jpg", null))))),
//
//                    //Series
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "American Horror Story", Arrays.asList(
//                            new ItemFieldString(1, "", "An anthology series centering on different characters and locations, including a house with a murderous past, an insane asylum, a witch coven, a freak show circus, a haunted hotel, a possessed farmhouse, a cult, the apocalypse, a slasher summer camp, a bleak beach town and desert valley, and NYC."),
//                            new ItemFieldString(2, "", "FX"),
//                            new ItemFieldNumber(3, "", 2011),
//                            new ItemFieldNumber(4, "", 103),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/AHS/AHS1.jpg", null), new UploadFile("official/Series/AHS/AHS2.jpg", null), new UploadFile("official/Series/AHS/AHS3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "Arcane", Arrays.asList(
//                            new ItemFieldString(1, "", "Based on the world behind League of Legends, Arcane dives into the delicate balance between the rich, utopian city of Piltover Crest icon.png Piltover and the seedy, oppressed underground of Zaun Crest icon.png Zaun. Known across Runeterra as the \"city of progress,\" many of the most brilliant minds call these cities home. But the creation of hextech, a way for any person to control magical energy, threatens that balance. The story follows the origins of two iconic League champions-and the power that will tear them apart."),
//                            new ItemFieldString(2, "", "Netflix"),
//                            new ItemFieldNumber(3, "", 2021),
//                            new ItemFieldNumber(4, "", 9),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/Arcane/Arcane1.jpg", null), new UploadFile("official/Series/Arcane/Arcane2.jpg", null), new UploadFile("official/Series/Arcane/Arcane3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "Black Mirror", Arrays.asList(
//                            new ItemFieldString(1, "", "Set in a world only minutes from our own, \"Black Mirror\", a UK and USA non-hosted anthology series; unveils how modern technologies can backfire and be used against their makers, every episode set in a slightly different reality with different characters combating different types of technologies."),
//                            new ItemFieldString(2, "", "Netflix"),
//                            new ItemFieldNumber(3, "", 2011),
//                            new ItemFieldNumber(4, "", 22),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/BlackMirror/BlackMirror1.jpg", null), new UploadFile("official/Series/BlackMirror/BlackMirror2.jpg", null), new UploadFile("official/Series/BlackMirror/BlackMirror3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "Friends", Arrays.asList(
//                            new ItemFieldString(1, "", "Friends is a 90's Comedy TV show, based in Manhattan, about 6 friends who go through just about every life experience imaginable together; love, marriage, divorce, children, heartbreaks, fights, new jobs and job losses and all sorts of drama."),
//                            new ItemFieldString(2, "", "NBC"),
//                            new ItemFieldNumber(3, "", 1994),
//                            new ItemFieldNumber(4, "", 236),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/Friends/Friends1.jpg", null), new UploadFile("official/Series/Friends/Friends2.jpg", null), new UploadFile("official/Series/Friends/Friends3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "House", Arrays.asList(
//                            new ItemFieldString(1, "", "The series follows the life of anti-social, pain killer addict, witty and arrogant medical doctor Gregory House (Hugh Laurie) with only half a muscle in his right leg. He and his team of medical doctors try to cure complex and rare diseases from very ill ordinary people in the United States of America."),
//                            new ItemFieldString(2, "", "FOX"),
//                            new ItemFieldNumber(3, "", 2004),
//                            new ItemFieldNumber(4, "", 177),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/House/House1.jpg", null), new UploadFile("official/Series/House/House2.jpg", null), new UploadFile("official/Series/House/House3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "Love, Death + Robots", Arrays.asList(
//                            new ItemFieldString(1, "", "This collection of animated short stories spans several genres, including science fiction, fantasy, horror and comedy. World-class animation creators bring captivating stories to life in the form of a unique and visceral viewing experience. The animated anthology series includes tales that explore alternate histories, life for robots in a post-apocalyptic city and a plot for world domination by super-intelligent yogurt."),
//                            new ItemFieldString(2, "", "Netflix"),
//                            new ItemFieldNumber(3, "", 2019),
//                            new ItemFieldNumber(4, "", 35),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/LoveDeathRobots/LoveDeathRobots1.jpeg", null), new UploadFile("official/Series/LoveDeathRobots/LoveDeathRobots2.jpg", null), new UploadFile("official/Series/LoveDeathRobots/LoveDeathRobots3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "Stranger Things", Arrays.asList(
//                            new ItemFieldString(1, "", "When Will Byers suddenly goes missing, the whole town of Hawkins, Indiana, turns upside down. Many people are on the search for Will, including his mother Joyce, his brother Jonathan, his friends Mike, Dustin, and Lucas, Police Chief Jim Hopper, and other notable people. But one thing leads to another, creating a supernatural trail. And things get even weirder when a little girl with a shaved head comes into the story."),
//                            new ItemFieldString(2, "", "Netflix"),
//                            new ItemFieldNumber(3, "", 2016),
//                            new ItemFieldNumber(4, "", 34),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/StrangerThings/StrangerThings1.jpg", null), new UploadFile("official/Series/StrangerThings/StrangerThings2.jpg", null), new UploadFile("official/Series/StrangerThings/StrangerThings3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "The Big Bang Theory", Arrays.asList(
//                            new ItemFieldString(1, "", "The Big Bang Theory was centered on physicists Sheldon Cooper and Leonard Hofstadter, whose geeky and introverted lives were changed when Penny, an attractive waitress and aspiring actress, moved into the apartment across from theirs. Penny quickly became a part of Sheldon and Leonard's social group, which included the equally geeky engineer Howard Wolowitz and astrophysicist Raj Koothrappali. The group later expanded to include Howard's wife Bernadette Rostenkowski-Wolowitz and Sheldon's wife Amy Farrah Fowler."),
//                            new ItemFieldString(2, "", "CBS"),
//                            new ItemFieldNumber(3, "", 2007),
//                            new ItemFieldNumber(4, "", 279),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/TBBT/TBBT1.jpg", null), new UploadFile("official/Series/TBBT/TBBT2.jpg", null), new UploadFile("official/Series/TBBT/TBBT3.jpg", null))))),
//                    new CatalogItem(catalogs.get(2).getId(), templates.get(3).getId(), "House of the Dragon", Arrays.asList(
//                            new ItemFieldString(1, "", "House of the Dragon is set about 200 years before the events of Game of Thrones, 172 years before the birth of Daenerys Targaryen, descendant of the eponymous royal house, and 100 years after the Seven Kingdoms are united by the Targaryen Conquest. Featuring an ensemble cast, the show portrays the beginning of the end of House Targaryen, the events leading up to and covering the Targaryen civil war of succession, known as the \"Dance of the Dragons\"."),
//                            new ItemFieldString(2, "", "HBO"),
//                            new ItemFieldNumber(3, "", 2022),
//                            new ItemFieldNumber(4, "", 10),
//                            new ItemFieldImage(5, "", Arrays.asList(new UploadFile("official/Series/HouseOfTheDragon/HouseOfTheDragon1.jpg", null), new UploadFile("official/Series/HouseOfTheDragon/HouseOfTheDragon2.jpg", null), new UploadFile("official/Series/HouseOfTheDragon/HouseOfTheDragon3.jpg", null))))),
//
//                    //Books
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "Dark Matter", Arrays.asList(
//                            new ItemFieldString(1, "", "Dark Matter is the story of Jason Dessen, an intensely devoted family man with a lackluster career. One day he's abducted by a mysterious stranger, and every assumption he'd had about his life is yanked away. He finds himself in a disorienting world both similar and different from his \"real\" life."),
//                            new ItemFieldString(2, "", "Science Fiction, Thriller, Romance"),
//                            new ItemFieldString(3, "", "Blake Crouch"),
//                            new ItemFieldString(4, "", "Crown Publishing Group"),
//                            new ItemFieldNumber(5, "", 2016),
//                            new ItemFieldNumber(6, "", 342))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "The Woman in the Window", Arrays.asList(
//                            new ItemFieldString(1, "", "Anna Fox lives alone, a recluse in her New York City home, unable to venture outside. She spends her day drinking wine (maybe too much), watching old movies, recalling happier times... and spying on her neighbors. Then the Russells move into the house across the way: a father, mother, their teenaged son. The perfect family. But when Anna, gazing out her window one night, sees something she shouldn’t, her world begins to crumble—and its shocking secrets are laid bare. What is real? What is imagined? Who is in danger? Who is in control? In this diabolically gripping thriller, no one (and nothing) is what it seems."),
//                            new ItemFieldString(2, "", "Thriller"),
//                            new ItemFieldString(3, "", "AJ Finn"),
//                            new ItemFieldString(4, "", "William Morrow"),
//                            new ItemFieldNumber(5, "", 2018),
//                            new ItemFieldNumber(6, "", 448))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "Fahrenheit 451", Arrays.asList(
//                            new ItemFieldString(1, "", "Guy Montag is a fireman. His job is to destroy the most illegal of commodities, the printed book, along with the houses in which they are hidden. Montag never questions the destruction and ruin his actions produce, returning each day to his bland life and wife, Mildred, who spends all day with her television “family.” But when he meets an eccentric young neighbor, Clarisse, who introduces him to a past where people didn’t live in fear and to a present where one sees the world through the ideas in books instead of the mindless chatter of television, Montag begins to question everything he has ever known."),
//                            new ItemFieldString(2, "", "Dystopian"),
//                            new ItemFieldString(3, "", "Ray Bradbury"),
//                            new ItemFieldString(4, "", "Ballantine Books"),
//                            new ItemFieldNumber(5, "", 1953),
//                            new ItemFieldNumber(6, "", 256))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "The Da Vinci Code", Arrays.asList(
//                            new ItemFieldString(1, "", "The Da Vinci Code follows symbologist Robert Langdon and cryptologist Sophie Neveu after a murder in the Louvre Museum in Paris causes them to become involved in a battle between the Priory of Sion and Opus Dei over the possibility of Jesus Christ and Mary Magdalene having had a child together."),
//                            new ItemFieldString(2, "", "Mystery, Detective fiction"),
//                            new ItemFieldString(3, "", "Dan Brown"),
//                            new ItemFieldString(4, "", "Doubleday"),
//                            new ItemFieldNumber(5, "", 2003),
//                            new ItemFieldNumber(6, "", 489))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "Dune", Arrays.asList(
//                            new ItemFieldString(1, "", "Dune tells the story of young Paul Atreides, whose family accepts the stewardship of the planet Arrakis. While the planet is an inhospitable and sparsely populated desert wasteland, it is the only source of melange, or \"spice\", a drug that extends life and enhances mental abilities."),
//                            new ItemFieldString(2, "", "Science fiction"),
//                            new ItemFieldString(3, "", "Frank Herbert"),
//                            new ItemFieldString(4, "", "Chilton Books"),
//                            new ItemFieldNumber(5, "", 1965),
//                            new ItemFieldNumber(6, "", 412))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "I, Robot", Arrays.asList(
//                            new ItemFieldString(1, "", "The book is a set of stories about the first robotic machines and the problems and pitfalls of living with and working alongside them. The book is the first in a series of several novels about robots; it is famous for its Three Laws of Robotics that govern machine behavior, and for its device, the positronic brain, which contains a robot’s conscious intelligence."),
//                            new ItemFieldString(2, "", "Science fiction"),
//                            new ItemFieldString(3, "", "Isaac Asimov"),
//                            new ItemFieldString(4, "", "Gnome Press"),
//                            new ItemFieldNumber(5, "", 1965),
//                            new ItemFieldNumber(6, "", 253))),
//                    new CatalogItem(catalogs.get(3).getId(), templates.get(4).getId(), "Ready Player One", Arrays.asList(
//                            new ItemFieldString(1, "", "In the year 2044, reality is an ugly place. The only time teenage Wade Watts really feels alive is when he's jacked into the virtual utopia known as the OASIS. Wade's devoted his life to studying the puzzles hidden within this world's digital confines—puzzles that are based on their creator's obsession with the pop culture of decades past and that promise massive power and fortune to whoever can unlock them. But when Wade stumbles upon the first clue, he finds himself beset by players willing to kill to take this ultimate prize. The race is on, and if Wade's going to survive, he'll have to win—and confront the real world he's always been so desperate to escape."),
//                            new ItemFieldString(2, "", "Science fiction"),
//                            new ItemFieldString(3, "", "Ernest Cline"),
//                            new ItemFieldString(4, "", "Crown Publishing Group"),
//                            new ItemFieldNumber(5, "", 2011),
//                            new ItemFieldNumber(6, "", 374)))
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
package com.katalogizegroup.katalogize.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class Catalog {
    @Getter private String id;
    private String name;
    private String description;
    @Getter private String userId;

    private static List<Catalog> catalogs = Arrays.asList(
            new Catalog("catalog-0", "Games", "Games played", "user-0"),
            new Catalog("catalog-1", "Books", "Book read", "user-1"),
            new Catalog("catalog-2", "Movies", "Movies watched", "user-3")
    );

    public static Catalog getById(String id) {
        return catalogs.stream().filter(catalog -> catalog.getId().equals(id)).findFirst().orElse(null);
    }

}

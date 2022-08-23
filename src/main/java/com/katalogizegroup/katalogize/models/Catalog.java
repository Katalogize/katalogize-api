package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document (collection = "catalogs")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Catalog {
    @Id
    @NonNull private int id;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private int userId;
    private List<CatalogItem> items;

    @Transient
    public static final String SEQUENCE_NAME = "catalogs_sequence";
}
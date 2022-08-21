package com.katalogizegroup.katalogize.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "catalogs")
@Getter
@Setter
@AllArgsConstructor
public class Catalog {
    @Id
    private int id;
    private String name;
    private String description;
    private int userId;

    @Transient
    public static final String SEQUENCE_NAME = "catalogs_sequence";
}
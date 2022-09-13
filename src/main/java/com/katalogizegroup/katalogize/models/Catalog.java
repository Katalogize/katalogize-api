package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
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
    private String id = new ObjectId().toString();
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private boolean isPrivate;
    @NonNull private String userId;
    @NonNull private List<String> templateIds;

//    @Transient
//    public static final String SEQUENCE_NAME = "catalogs_sequence";
}
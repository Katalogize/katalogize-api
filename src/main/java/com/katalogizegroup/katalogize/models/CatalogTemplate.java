package com.katalogizegroup.katalogize.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document (collection = "catalog-templates")
public class CatalogTemplate {
    @Id
    private String id = new ObjectId().toString();

    @NonNull private String name;

    @NonNull private List<TemplateField> templateFields;

    @NonNull private boolean allowNewFields;

    @NonNull private Instant modifiedDate = Instant.now();

    @Getter @Setter private Instant creationDate;

}
package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document (collection = "catalog-items")
public class CatalogItem {
    @Id
    private String id = new ObjectId().toString();

    @NonNull private String catalogId;

    @NonNull private String templateId;

    @NonNull private String name;
    @NonNull private List<ItemField> fields;

    @NonNull private Instant creationDate = Instant.now();

//    @Transient
//    public static final String SEQUENCE_NAME = "catalog_items_sequence";

    public void addField (ItemField field) {
        fields.add(field);
    }
}
package com.katalogizegroup.katalogize.models;

import com.katalogizegroup.katalogize.models.itemfields.ItemField;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document (collection = "catalog-items")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class CatalogItem {
    @Id
    @NonNull private int id;

    @NonNull private int catalogId;

    @NonNull private int templateId;
    @NonNull private List<ItemField> fields = new ArrayList<>();

    @Transient
    public static final String SEQUENCE_NAME = "catalog_items_sequence";

    public void addField (ItemField field) {
        fields.add(field);
    }
}